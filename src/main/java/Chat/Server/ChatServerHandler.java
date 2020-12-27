package Chat.Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final LinkedList<User> users = new LinkedList<>();

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

        if (msg.startsWith("!")) {
            CommandHandler(incoming, msg);
            return;
        }

        String senderName = FindUsername(incoming);

        if (senderName != null) { // Если пользователь авторизовался, то отправляем его сообщение всем
            for (User user : users) {
                if (user.getChannel() != incoming) {
                    user.getChannel().writeAndFlush(
                            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                                    " " + senderName + ": " + msg + "\n");
                }
            }
        } else { // Если пользователь новый получаем его username и оповещаем всех о его подключении
            FindAndSetUsername(incoming, msg);

            for (User user : users) {
                if (user.getChannel() != incoming)
                    user.getChannel().writeAndFlush("[SERVER] - " + msg + " has joined!\n");
                else {   // Приветствие нового пользователя
                    user.getChannel().writeAndFlush("[SERVER] - welcome to chat, " + msg + "\n");
                    user.getChannel().writeAndFlush("Type !help to see chat commands.\n");
                }

            }
        }
    }

    // Обработчик пользовательских команд
    // По-хорошему, нужно добавлять для каждой команды отдельный метод и уже в этом методе реализовывать логику команды
    // Но с данным количеством команд можно и не заморачиваться
    // P.s. команды с большим функционалом рекомендуется все же выносить в отдельные методы
    private void CommandHandler(Channel ctx, String msg) {
        String[] split_msg = msg.split(" ");

        switch (split_msg[0]) {

            case "!help":
                String commandList = "Available commands:\n" +
                        "!onlineList - see who is online\n" +
                        "!changeUsername <NewUsername> - changes your username\n";
                ctx.writeAndFlush(commandList);
                break;

            case "!onlineList":
                ctx.writeAndFlush("Online users:\n");
                for (User user : users) {
                    ctx.writeAndFlush(user.getUsername() + " is online\n");
                }
                break;

            case "!changeUsername":
                String oldUsername = FindUsername(ctx);
                FindAndSetUsername(ctx, split_msg[1]);
                SendMessageFromServerToAll(oldUsername + " changed username to " + split_msg[1] + "\n");
                break;

            // Тут можно добавить новые команды и их обработчики

            default:
                ctx.writeAndFlush("Incorrect command!\n");
        }
    }

    // Поиск имени пользователя по каналу. Возвращает Null, если пользователь еще не авторизовался
    public String FindUsername(Channel ctx) {
        for (User user : users) {
            if (user.getChannel() == ctx && user.getUsername() != null) {
                return user.getUsername();
            }
        }
        return null;
    }

    // Поиск и изменение Username
    public void FindAndSetUsername(Channel ctx, String username) {
        for (User user : users) {
            if (user.getChannel() == ctx) {
                user.setUsername(username);
                break;
            }
        }
    }

    // Отправка сообщения всем пользователям от имени сервера
    public void SendMessageFromServerToAll(String msg) {
        for (User user : users) {
            user.getChannel().writeAndFlush("[SERVER] - " + msg);
        }
    }
}
