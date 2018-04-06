package com.andy.netty.client.handler;

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
    }
}
