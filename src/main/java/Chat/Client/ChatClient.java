package Chat.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;

public class ChatClient {

    private final String username;
    private final String host; //Host to connect
    private final int port; //Poet to connect

    public ChatClient(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    public static void main(String[] args) throws Exception {
        String Username;
        System.out.println("Type '!Exit' to exit program");
        System.out.println("Enter username:");
        Username = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (Username.equals("!Exit")) return;

        new ChatClient("localhost", 8888, Username).run();
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        String input;

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            channel.writeAndFlush(username + "\n");

            while (true) {
                input = in.readLine();
                if (input.equals("!Exit")) break;
                else
                    channel.writeAndFlush(input + "\n");
            }

        } catch (ConnectException e) {
            System.out.println("Can't reach server.\nTry again later.");
        } finally {
            group.shutdownGracefully();
        }
    }
}
