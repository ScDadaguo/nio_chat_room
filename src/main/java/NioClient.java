/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @FileName: NioClient.java
 * @Description: NioClient.java类说明
 * @Author: guohao
 * @Date: 2019/8/1 12:14
 */
public class NioClient {
    public void start(String nickName) throws IOException {
        //连接服务器
        SocketChannel socketChannel=SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));
        System.out.println("客户端启动成功");

        // TODO: 2019/8/1  接受服务器响应数据 新开线程，专门负责来接受服务器的相应数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        //向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && request.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(nickName+":"+request));
            }
        }
        //接受服务器相应
    }

    public static void main(String[] args) throws IOException {
//        new NioClient().start();
    }

}
