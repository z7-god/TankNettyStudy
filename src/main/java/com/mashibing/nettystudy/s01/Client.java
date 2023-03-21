package com.mashibing.nettystudy.s01;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        // 线程池 事件Event循环处理Loop组Group 传参12345，默认电脑核数*2，一般客户端为1
        EventLoopGroup group = new NioEventLoopGroup(1);
        // 辅助启动类
        Bootstrap b = new Bootstrap();

        try {
            // f extends Future
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)    // 使用Java NIO的SocketChannel来进行通信。其他类型的Channel，如OioSocketChannel、EpollSocketChannel等
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//
//                        }
//                    })  // 事件来了怎么处理
//                    .handler(new ClientChannelInitializer())  // 事件来了怎么处理
                    .handler(new ClientHandler())  // 事件来了怎么处理
                    .connect("localhost", 8888);
//                    .sync();  // 等待结束才能继续
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    if (!channelFuture.isSuccess()) {
                        System.out.println("not connected");
                    }else {
//                        1.在Netty中，异步获取链接时，如果IO操作失败，Netty会自动尝试重新连接。
//                        具体来说，Netty会根据连接失败的原因，选择不同的重连策略，如指数退避策略、随机退避策略等。
//                        这些重连策略都是在ChannelFuture对象的addListener()方法中自动启用的，所以我们不需要手动编写重连逻辑。
//                        2.为了保证程序的可靠性和性能，我们也可以对重连策略进行自定义和调优。
//                        在Netty中，我们可以通过ChannelOption.CONNECT_TIM/EOUT_MILLIS和ChannelOption.SO_BACKLOG等参数来调整连接超时时间和连接队列大小等参数。
//                        此外，还可以通过重写ChannelInitializer中的initChannel()方法，添加一些业务相关的处理逻辑，如连接认证、心跳检测等，以提高连接的稳定性和可靠性。
//                        总之，Netty提供了一套完善的异步IO模型和连接管理机制，可以帮助我们更加方便地进行网络编程，同时也可以保证程序的可靠性和性能。
                        System.out.println("connected!");
                    }
                }
            });
            f.sync(); // 阻塞住，同步等待I/O操作的结果=>如果I/O操作已经完成，则立即返回操作结果； 如果I/O操作还没有完成，则当前线程会阻塞，直到I/O操作完成或者出现异常； 如果I/O操作出现异常，则直接抛出异常。
//            Thread.sleep(5000);
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
//        socketChannel.pipeline().addLast(new ChannelHandler() {
//        })
        System.out.println(socketChannel);
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // channel第一次连上可用，写出一个字符串,Direct Memory直接内存
        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        ctx.writeAndFlush(buf); // 自动释放
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            System.out.println(new String(bytes));  // hello

        } finally {
            if (buf != null) ReferenceCountUtil.release(buf);

        }
    }
}