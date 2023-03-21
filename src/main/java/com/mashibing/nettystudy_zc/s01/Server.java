package com.mashibing.nettystudy_zc.s01;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workLoopGroup = new NioEventLoopGroup(2);

        ServerBootstrap b = new ServerBootstrap();

        try {
            ChannelFuture f = b.group(bossLoopGroup, workLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = null;
                                    buf = (ByteBuf) msg;
                                    byte[] bytes = new byte[buf.readableBytes()];
                                    buf.getBytes(buf.readerIndex(), bytes);
                                    System.out.println(new String(bytes));  // hello

                                    if (new String(bytes).equals("hello server")) {
                                        ctx.writeAndFlush(Unpooled.copiedBuffer("hello client".getBytes()));
                                    }
                                }
                            });

                        }
                    })
                    .bind(8888)
                    .sync();
            System.out.println("server started!");

            f.channel().closeFuture().sync();

        } finally {
            workLoopGroup.shutdownGracefully();
            bossLoopGroup.shutdownGracefully();
        }

    }
}
