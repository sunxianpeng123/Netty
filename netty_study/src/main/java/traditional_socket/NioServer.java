package traditional_socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: sunxianpeng
 * \* Date: 2019/9/29
 * \* Time: 16:35
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class NioServer {
    //通道通信
    private Selector selector;
    /**
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
     * @param port 绑定的端口号
     * @throws IOException
     */
    public void initServer(int port) throws IOException {
        //获得一个ServerSocket通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置通道为非阻塞
        serverSocketChannel.configureBlocking(false);
        //将通道对应的ServerSocket绑定到port端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //获得一个通道管理器
        this.selector = Selector.open();
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，注册该事件后，
        //当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    /**
     * 采用轮询的方式监听selector是否有需要处理的事件，如果有，则进行处理
     * @throws IOException
     */
    public void listen() throws IOException {
        System.out.println("server start !!");
        //轮询访问selector
        while (true){
            //当注册的事件到达时，方法返回；否则，该方法会一直阻塞
            selector.select();
            //获得selector中选中的项的迭代器，选中的项为注册的事件
            Iterator<?> iterator = this.selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = (SelectionKey) iterator.next();
                //删除已选的key，以防重复处理
                iterator.remove();
                handler(key);
            }
        }
    }
    /**
     *  处理请求
     * @param key
     */
    private void handler(SelectionKey key) throws IOException {
        if (key.isAcceptable()){//客户端请求连接事件
            handlerAccept(key);
        }else if (key.isReadable()){//获得了可读的事件
            handlerRead(key);
        }
    }
    /**
     * 处理连接请求
     * @param key
     * @throws IOException
     */
    private void handlerAccept(SelectionKey key) throws IOException {
        //获得ServerSocket
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        //获得和客户端连接的通道
        SocketChannel channel = serverSocketChannel.accept();
        //设置成非阻塞
        channel.configureBlocking(false);
        System.out.println("new client connect");
        //服务端发给客户端的确认信息
        channel.write(ByteBuffer.wrap("server success to create connect".getBytes()));
        //在和客户端连接成功后，为了可以接收到客户端的信息，需要给通道设置读的权限
        channel.register(this.selector,SelectionKey.OP_READ);
    }
    /**
     * 处理可读的事件
     * @param key
     * @throws IOException
     */
    private void handlerRead(SelectionKey key) throws IOException {
        //获得ServerSocket
        SocketChannel channel = (SocketChannel) key.channel();
        //创建读取的缓冲区(每次读1000个字节)
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        channel.read(buffer);
        byte[] data = buffer.array();
        String msg = new String(data).trim();
        System.out.println("server get info :"+ msg);
        ByteBuffer outBuffer = ByteBuffer.wrap(("服务端收到信息："+msg).getBytes());
        //将消息送回给客户端
        channel.write(outBuffer);
    }
    /**
     * 启动服务测试
     * @param args
     */
    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.initServer(10101);
        nioServer.listen();
    }

}