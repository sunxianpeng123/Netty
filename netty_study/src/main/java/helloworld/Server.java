package helloworld;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: sunxianpeng
 * \* Date: 2019/9/29
 * \* Time: 18:32
 * \* To change this template use File | Settings | File Templates.
 * \* Description:based on netty 5.0.0.Alpha1
 * \
 */
public class Server {
    public static void main(String[] args) {
        //服务类
        ServerBootstrap bootstrap = new ServerBootstrap();
        //boss和worker
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //设置线程池
            bootstrap.group(boss, worker);
            //设置socket工厂
            bootstrap.channel(NioServerSocketChannel.class);
            //设置管道工厂
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new ServerHandler());
                }
            });
            //netty3中对应设置如下
            //bootstrap.setOption("backlog", 1024);
            //bootstrap.setOption("tcpNoDelay", true);
            //bootstrap.setOption("keepAlive", true);
            //设置参数，TCP参数
            //serverSocketchannel的设置，链接缓冲池的大小
            bootstrap.option(ChannelOption.SO_BACKLOG, 2048);
            //socketchannel的设置,维持链接的活跃，清除死链接
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //socketchannel的设置,关闭延迟发送
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            //绑定端口
            ChannelFuture future = bootstrap.bind(10101);
            System.out.println("start");
            //等待服务端关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            //释放资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}