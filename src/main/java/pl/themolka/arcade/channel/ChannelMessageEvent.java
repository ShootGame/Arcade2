package pl.themolka.arcade.channel;

import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.event.Cancelable;

public class ChannelMessageEvent extends ChannelEvent implements Cancelable {
    private final Sender author;
    private String authorName;
    private boolean cancel;
    private String message;

    public ChannelMessageEvent(ArcadePlugin plugin,
                               Channel channel,
                               Sender author,
                               String authorName,
                               String message) {
        super(plugin, channel);

        this.author = author;
        this.authorName = authorName;
        this.message = message;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Sender getAuthor() {
        return this.author;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
