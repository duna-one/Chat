package Chat.Client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        if (msg.equals("Enter username:"))
            Auth(ctx, msg);
        else System.out.println(msg);
    }

    public void Auth(ChannelHandlerContext ctx, String msg) {
        try {
            System.out.println(msg);
            ctx.writeAndFlush(new BufferedReader(new InputStreamReader(System.in)).readLine());
        } catch (IOException e) {
            System.out.println("Error in username enter\n");
            System.out.println(e.getMessage());
        }
    }
}
