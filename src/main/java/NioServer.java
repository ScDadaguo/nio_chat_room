/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

/**
 * @FileName: NioServer.java
 * @Description: NioServer.java类说明
 * @Author: guohao
 * @Date: 2019/7/31 22:00
 */


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * nio 服务器
 */
public class NioServer {
    public void start() throws IOException {
        //1.创建selector
        Selector selector = Selector.open();
        //2.通过ServerSocketChannel创建channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3.为channel通道绑定监听窗口
        serverSocketChannel.bind(new InetSocketAddress(8000));
        //4.设置channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //5. 将channel注册到selector上，监听连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器连接成功");
        //6.循环等待新接入的连接
        for (; ; ) {
            // TODO: 2019/7/31   获取可用的channel数量
            int readyChannels = selector.select();
            // TODO: 2019/7/31 为什么要这样    防止空轮询   但是还是会占用cpu资源 ，造成cpu100%
            if (readyChannels == 0) {
                continue;
            }
            /**
             * 获取可用的channel集合
             */
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                //selectionKey实例
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                //移除Set中的当前selectionKey
                iterator.remove();

                //7. 根据就绪状态，调用对应方法处理业务逻辑
                /**
                 *如果是接入事件
                 */
                // TODO: 2019/7/31
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel,selector);
                }
                /**
                 *如果是可读 事件
                 */
                // TODO: 2019/7/31
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey,selector);
                }
            }
            //7.根据就绪状态，调用对应方法处理业务逻辑
        }
    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel,Selector selector) throws IOException {
        // 如果是接入事件， 创建socketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        //将sockerchannel设置为非阻塞工作模式
        socketChannel.configureBlocking(false);
        //将channel注册到selector上，监听 可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //恢复客户端提示信息
        socketChannel.write(Charset.forName("UTF-8").encode("你与聊天室里其他人都不是朋友关系，请注意隐私安全"));

    }
    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey,Selector selector) throws IOException {
        // 要从selectionKey中获取已经就绪的channel
        SocketChannel socketChannel= (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取客户端请求信息
        String request = "";
        while (socketChannel.read(byteBuffer)>0) {
            //切换buffer为读模式
            byteBuffer.flip();
            //读取buffer中的内容
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }
        //将channel再次注册到selector上，监听他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将客户端发送的请求事件，广播给其他客户端
        if (request.length() > 0) {
            //广播给其他客户端
            System.out.println("::"+request);
            broadCast(selector, socketChannel, request);
        }
    }

    /**
     * 广播给其他客户端
     */
    private void broadCast(Selector selector,SocketChannel sourceChannel,String request){
        /**
         * 获取到所有已接入的客户端channel
         */
        Set<SelectionKey> selectionKeySet = selector.keys();

        /**
         * 循环向所有的channel广播信息
         */
        selectionKeySet.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();
            //剔除发消息的客户端
            if (targetChannel instanceof SocketChannel
                    && targetChannel != sourceChannel) {
                try {
                    //将消息发送到targetChannel客户端
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();
    }
}
