package Chat.Server;

import io.netty.channel.Channel;

public class User {
    private final Channel channel;
    private String username;

    public User(Channel channel) {
        this.channel = channel;
        this.username = null;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
