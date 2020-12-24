package Chat.Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final LinkedList<User> users = new LinkedList<User>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        users.add(new User(ctx.channel()));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel incoming = ctx.channel();
        User RemovedUser = null;
        for (User user : users) {
            if (user.getChannel() == incoming) {
                RemovedUser = user;
                break;
            }
        }
        users.remove(RemovedUser);
        for (User user : users) {
            assert RemovedUser != null;
            user.getChannel().writeAndFlush("[SERVER] - " + RemovedUser.getUsername() + " has left!\n");
        }

    }

    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        Channel incoming = ctx.channel();
        String senderName = null;
        boolean newUser = true;

        for (User user : users) { // Проверка на наличие пользователя в активных подключениях
            if (user.getChannel() == incoming && user.getUsername() != null) {
                newUser = false;
                senderName = user.getUsername();
                break;
            }
        }

        if (!newUser) { // Если пользователь авторизовался, то отправляем его сообщение всем
            for (User user : users) {
                if (user.getChannel() != incoming) {
                    user.getChannel().writeAndFlush(
                            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                                    " " + senderName + ": " + msg + "\n");
                }
            }
        } else { // Если пользователь новый получаем его username и оповещаем всех о его подключении
            for (User user : users) {
                if (user.getChannel() == incoming) {
                    user.setUsername(msg);
                    break;
                }
            }
            for (User user : users) {
                if (user.getChannel() != incoming)
                    user.getChannel().writeAndFlush("[SERVER] - " + msg + " has joined!\n");
                else
                    user.getChannel().writeAndFlush("[SERVER] - welcome to chat, " + msg + "\n");
            }
        }
    }
}
