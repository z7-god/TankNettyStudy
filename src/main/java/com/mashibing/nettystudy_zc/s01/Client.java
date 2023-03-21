package com.mashibing.nettystudy_zc.s01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

        Bootstrap b = new Bootstrap();
        try {
            ChannelFuture f = b.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = null;
                            try {
                                buf = (ByteBuf) msg;
                                byte[] bytes = new byte[buf.readableBytes()];
                                buf.getBytes(buf.readerIndex(), bytes);
                                System.out.println(new String(bytes));
                            } finally {
                                if (buf != null) ReferenceCountUtil.release(buf);
                            }
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buf = Unpooled.copiedBuffer("hello server".getBytes());
                            ctx.writeAndFlush(buf);
                        }
                    })
                    .connect("localhost", 8888)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) {
                            if (!channelFuture.isSuccess()) {
                                System.out.println("not connected");
                            } else {
                                System.out.println("connected!");
                            }
                        }
                    })
                    .sync();// 阻塞住，同步等待I/O操作的结果=>如果I/O操作已经完成，则立即返回操作结果； 如果I/O操作还没有完成，则当前线程会阻塞，直到I/O操作完成或者出现异常； 如果I/O操作出现异常，则直接抛出异常。
//            Thread.sleep(5000);
            f.channel().closeFuture().sync();

        } finally {
            eventLoopGroup.shutdownGracefully();

        }
    }


}
