package com.mashibing.nettystudy.s01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);    // 负责客户端的链接
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);  // 负责链接后的处理

        try {

            // Server端辅助启动类
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture f = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ServerChildHandler());
                            System.out.println(socketChannel);
                        }
                    })
                    .bind(8888) // L：服务端，R：客户端
                    .sync();
            System.out.println("server started!");

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }
}

class ServerChildHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            System.out.println(new String(bytes));  // hello

            ctx.writeAndFlush(msg); // 自行释放，不需要finally中的再判断释放
//            System.out.println(buf);
//            System.out.println(buf.refCnt());   //  有多少人引用了它}
        } finally {
//            if (buf != null) ReferenceCountUtil.release(buf);
//            System.out.println(buf.refCnt());

        }
    }

}