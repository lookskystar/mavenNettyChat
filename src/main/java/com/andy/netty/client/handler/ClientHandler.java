package com.andy.netty.client.handler;

import com.andy.netty.client.Client;
import com.andy.netty.im.common.IMConfig;
import com.andy.netty.im.common.IMMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 客户端消息处理类ClientHandler
 * @author Andy
 * @create 2018-04-06-22:36
 */

public class ClientHandler extends ChannelInboundHandlerAdapter implements IMConfig{
    private ChannelHandlerContext ctx;

    /**
     * tcp链路简历成功后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("成功连接服务器");
        this.ctx=ctx;
        IMMessage message=new IMMessage(
                APP_IM,
                CLIENT_VERSION,
                Client.UID,
                TYPE_CONNECT,
                SERVER_ID,
                MSG_EMPTY
        );
        sendMsg(message);
    }

    /**
     * 发送消息
     * @param msg
     * @return
     * @throws Exception
     */
    public boolean sendMsg(IMMessage msg) throws Exception{
        System.out.println("client："+msg);
        ctx.channel().writeAndFlush(msg);
        return msg.getMsg().equals("q")?false:true;
    }

    /**
     * 收到消息后调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMMessage m=(IMMessage)msg;
        System.out.println(m.getUid()+":"+m.getMsg());
    }

    /**
     * 发生异常时调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("与服务器断开连接："+cause.getMessage());
        ctx.close();
    }
}
