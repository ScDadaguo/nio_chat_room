/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @FileName: NioClientHandler.java
 * @Description: 客户端线程类，专门接受服务器数据
 * @Author: guohao
 * @Date: 2019/8/1 12:24
 */
public class NioClientHandler implements Runnable {
    private Selector selector;


    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {

        try {
            for (; ; ) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    //selectionKey实例
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    //移除Set中的当前selectionKey
                    iterator.remove();
                    /**
                     *如果是可读 事件
                     */
                    // TODO: 2019/7/31
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey,selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        // 要从selectionKey中获取已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取服务器端请求信息
        String response = "";
        while (socketChannel.read(byteBuffer) > 0) {
            //切换buffer为读模式
            byteBuffer.flip();
            //读取buffer中的内容
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }
        //将channel再次注册到selector上，监听他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将服务器端响应信息打印到本地
        if (response.length() > 0) {
            //response
            System.out.println(response);
        }
    }
}
