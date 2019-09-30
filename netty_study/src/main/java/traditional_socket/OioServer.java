package traditional_socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: sunxianpeng
 * \* Date: 2019/9/29
 * \* Time: 15:35
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \传统 Socket 编程
 */
public class OioServer {
    public static void main(String[] args) throws IOException {
        //创建一个缓存线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(10101);
        System.out.println("server start !!");
        while (true){
            //获取一个套接字(阻塞)
            final Socket socket = serverSocket.accept();
            System.out.println("accept new client ");
            //在线程池为新客户端开一个线程
            executorService.execute(new Runnable() {
                public void run() {
                    //业务处理
                    handler(socket);
                }
            });

        }

    }

    public static void handler(Socket socket){
        try {
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            while (true){
                int read = inputStream.read(bytes);
                if(read != -1){
                    System.out.println(new String(bytes, 0,read));
                }else {
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                System.out.println("close socket ");
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}