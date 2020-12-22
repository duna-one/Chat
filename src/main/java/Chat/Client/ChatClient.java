package Chat.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClient {

    private final String username;

    private final String host;
    private final int port;

    public ChatClient(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }

    public static void main(String[] args) throws Exception {
        String Username;
        System.out.println("Enter Username: ");
        Username = new BufferedReader(new InputStreamReader(System.in)).readLine();

        new ChatClient("localhost", 8888, Username).run();
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                channel.writeAndFlush(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) +
                        " " + username + ": " + in.readLine() + "\r\n");
            }

        } catch (ConnectException e) {
            System.out.println("Can't reach server.\nTry again later.");
        } finally {
            group.shutdownGracefully();
        }
    }
}
