package Chat.Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static  final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();
        for (Channel channel : channels){
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + "has joined!\n");
        }
        channels.add(incoming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();
        for (Channel channel : channels){
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + "has left!\n");
        }
        channels.remove(incoming);
    }

    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        Channel incoming = ctx.channel();
        for(Channel channel : channels){
            if(channel != incoming){
                channel.writeAndFlush(msg + "\n");
            }
        }
    }
}
