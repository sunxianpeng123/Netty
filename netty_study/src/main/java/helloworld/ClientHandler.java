package helloworld;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;


/**
 * \* Created with IntelliJ IDEA.
 * \* User: sunxianpeng
 * \* Date: 2019/9/30
 * \* Time: 11:28
 * \* To change this template use File | Settings | File Templates.
 * \* Description: based on netty 5.0.0.Alpha1
 * \
 */

public class ClientHandler extends SimpleChannelInboundHandler {
	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object  msg) throws Exception {
		ByteBuf in = (ByteBuf) msg;
		try {
			while (in.isReadable()) { // (1)
				System.out.println(in.toString(CharsetUtil.UTF_8));
//				System.out.print((char) in.readByte());
				System.out.flush();
			}
		} finally {
			ReferenceCountUtil.release(msg); // (2)
		}

		System.out.println("客户端收到消息:"+msg);
	}

}

