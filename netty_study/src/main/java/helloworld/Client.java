package helloworld;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: sunxianpeng
 * \* Date: 2019/9/30
 * \* Time: 11:28
 * \* To change this template use File | Settings | File Templates.
 * \* Description: based on netty 5.0.0.Alpha1
 * \
 */
public class Client {
    public static void main(String[] args) {
        String host = "openbarrage.douyutv.com"; //"127.0.0.1"; //
        int port = 8601; //10101; //
        String url = "";
        // 服务类
        Bootstrap bootstrap = new Bootstrap();
        // EventLoopGroup可以理解为是一个线程池，这个线程池用来处理连接、接受数据、发送数据
        // EventLoopGroup类就是一个“事件循环组”，它的底层是一个线程池。
        // 它有多重实现，这里我们选择其中一种常用的实现类“NioEventLoopGroup”。
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //设置线程池,多线程处理
            bootstrap.group(worker);
            //设置socket工厂,//制定通道类型为NioSocketChannel
            bootstrap.channel(NioSocketChannel.class);
            //设置管道
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    //注册handler
                    ch.pipeline().addLast(new ClientHandler());
                }
            });
            //连接服务器
            ChannelFuture connect = bootstrap.connect(host, port).sync();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));


/**
 *
 */
            String room_id = "431460";
            String loginCMD = "type@=loginreq/roomid@=" + room_id + "/";

            int i = 0;
            while (true) {
                if (i == 0) {
                    connect.channel().writeAndFlush(loginCMD);
                    i++;
                } else if (i <= 10) {

                } else {
//            等待直到客户端关闭：
                    connect.channel().closeFuture().sync();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}