package com.mashibing.io.nio;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.PseudoColumnUsage;
import java.util.Iterator;
import java.util.Set;


// NIO单线程模型
public class Server {
    public static void main(String[] args) throws IOException {
        // Channel为全双工，可同时读写
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 8888));
        // 设置非阻塞态
        ssc.configureBlocking(false);
        System.out.println("server started, listening on :" + ssc.getLocalAddress());
        // 管家轮询
        Selector selector = Selector.open();
        // 管理ssc，申请连上来的，并处理
        ssc.register(selector, SelectionKey.OP_ACCEPT); // operation accept


        while (true) {
            selector.select();  // 阻塞方法
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                // 先删除后处理
                it.remove();
                handle(key);
            }
        }

    }
    private static void handle(SelectionKey key ) {
        if( key.isAcceptable()) {
            try {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);

                sc.register(key.selector(), SelectionKey.OP_READ);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
            }
        } else if (key.isReadable()){
            SocketChannel sc = null;
            try {
                sc = (SocketChannel) key.channel();
                // ByteBuffer读写单指针，ByteBuf读写双指针
                ByteBuffer buffer = ByteBuffer.allocate(512);
                buffer.clear();
                int len = sc.read(buffer);

                if (len != -1) {
                    System.out.println(new String(buffer.array(), 0, len));
                }

                ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
                sc.write(bufferToWrite);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (sc != null) {
                    try {
                        sc.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
