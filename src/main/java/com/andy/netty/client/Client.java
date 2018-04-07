package com.andy.netty.client;

import com.andy.netty.client.handler.ClientHandler;
import com.andy.netty.im.common.IMConfig;
import com.andy.netty.im.common.IMMessage;
import com.andy.netty.im.common.MsgPackDecode;
import com.andy.netty.im.common.MsgPackEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.io.IOException;
import java.util.Scanner;

/**
 * 客户端Client
 * @author Andy
 * @create 2018-04-07-16:01
 */

public class Client implements Runnable,IMConfig{
    public static int UID=8888;
    private ClientHandler clientHandler=new ClientHandler();

    public static void main(String[] args) throws Exception{
        new Client().start();
    }

    public void start() throws Exception{
        new Thread(this).start();
        runServerCMD();
    }

    public void sendMsg(IMMessage msg) throws Exception{
        clientHandler.sendMsg(msg);
    }

    private void runServerCMD() throws Exception{
        IMMessage message=new IMMessage(
                APP_IM,
                CLIENT_VERSION,
                UID,
                TYPE_MSG_TEXT,
                UID,
                MSG_EMPTY
        );
        @SuppressWarnings("resource")
        Scanner scanner=new Scanner(System.in);
        do {
            message.setMsg(scanner.nextLine());
        }while(clientHandler.sendMsg(message));
    }

    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65536, 0, 2, 0, 2));
                    ch.pipeline().addLast("msgpack decoder",new MsgPackDecode());
                    ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                    ch.pipeline().addLast("msgpack encoder",new MsgPackEncode());
                    ch.pipeline().addLast(clientHandler);
                }
            });
            ChannelFuture f = b.connect(SERVER_HOST, SERVER_PORT).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
