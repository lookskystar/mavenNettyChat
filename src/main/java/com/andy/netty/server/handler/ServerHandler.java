package com.andy.netty.server.handler;

import com.andy.netty.im.common.IMConfig;
import com.andy.netty.im.common.IMMessage;
import com.andy.netty.im.common.OnlineUser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.IOException;

/**
 * 服务端消息处理类ServerHandler
 * @author Andy
 * @create 2018-04-06-18:06
 */
public class ServerHandler extends ChannelInboundHandlerAdapter implements IMConfig{
    private ChannelHandlerContext ctx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器Handler创建...");
        super.handlerAdded(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive.....");
        super.handlerAdded(ctx);
    }

    /**
     * tcp链路建立成功后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
        System.out.println("有客户端链接："+ctx.channel().remoteAddress().toString());
    }

    /**
     * 发送消息
     */
    public boolean sendMsg(IMMessage msg) throws IOException{
        System.err.println("服务器推送消息："+msg);
        ctx.writeAndFlush(msg);
        return msg.getMsg().equals("q")?false:true;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务器接收到消息："+msg);
        IMMessage message=(IMMessage) msg;
        if (OnlineUser.get(message.getReceiveId())==null){
            OnlineUser.put(message.getUid(),ctx);
        }
        ChannelHandlerContext c=OnlineUser.get(message.getReceiveId());
        if (c == null) {
            message.setMsg("对方不在线！");
            OnlineUser.get(message.getUid()).writeAndFlush(message);
        }else {
            c.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("与客户端断开连接："+cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
