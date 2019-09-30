package douyu;

import java.util.concurrent.CountDownLatch;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;;

public class Client {//编写客户端单例模式方便系统调用
    String host = "openbarrage.douyutv.com"; //"127.0.0.1"; //
    int port = 8601; //10101; //

    private static class SingletonHolder {
        static final Client instance = new Client();
    }

    public static Client getInstance() {
        return SingletonHolder.instance;
    }

    private EventLoopGroup group;
    private Bootstrap b;
    private ChannelFuture cf;
    private ClientInitializer clientInitializer;
    private CountDownLatch lathc;

    private Client() {
        lathc = new CountDownLatch(0);
        clientInitializer = new ClientInitializer(lathc);
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(clientInitializer);
    }

    public void connect() {
        //192.168.43.51测试端口8766 192.168.43.102 线上端口8765
        try {
            this.cf = b.connect(host, port).sync();
            System.out.println("远程服务器已经连接, 可以进行数据交换...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public ChannelFuture getChannelFuture() {
        if (this.cf == null) {
            this.connect();
        }
        if (!this.cf.channel().isActive()) {
            this.connect();
        }
        return this.cf;
    }

    public void close() {
        try {
            this.cf.channel().closeFuture().sync();
            this.group.shutdownGracefully();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) throws InterruptedException {
        ChannelFuture cf = getInstance().getChannelFuture();//单例模式获取ChannelFuture对象
        cf.channel().writeAndFlush(msg);
        //发送数据控制门闩加一
        lathc = new CountDownLatch(1);
        clientInitializer.resetLathc(lathc);
        lathc.await();//开始等待结果返回后执行下面的代码
        return clientInitializer.getServerResult();
    }

    public void readMessage() {
//		  ChannelFuture cf =getInstance().getChannelFuture();//单例模式获取ChannelFuture对象
//		  cf.channel().read();
//		  //发送数据控制门闩加一
//		  lathc = new CountDownLatch(1);
//		  clientInitializer.resetLathc(lathc);
//		  lathc.await();//开始等待结果返回后执行下面的代码
//		  return clientInitializer.getServerResult();
    }

    public static void main(String[] args) throws Exception {
        String room_id = "263824";
        //发送登录请求(登入9999房间)
        String loginCMD = "type@=loginreq/roomid@=" + room_id + "/";

        System.out.println(Client.getInstance().sendMessage(loginCMD));//测试等待数据返回
        //加入弹幕分组开始接收弹幕
        String joinGroupCMD = "type@=joingroup/rid@=" + room_id + "/gid@=-9999/";
        int i = 0;
        while (true) {
            System.out.println("===================== " + i);
            i++;
            String s = Client.getInstance().sendMessage(joinGroupCMD);
            System.out.println(s);

        }


    }
}