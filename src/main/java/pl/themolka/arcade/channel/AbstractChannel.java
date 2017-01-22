package pl.themolka.arcade.channel;

import pl.themolka.arcade.command.Sender;

public abstract class AbstractChannel implements Channel {
    private final String id;

    public AbstractChannel(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getPermission() {
        return this.getId().toLowerCase();
    }

    @Override
    public void send(String message) {
        for (Sender member : this.getMembers()) {
            member.send(message);
        }
    }

    @Override
    public void sendAction(String action) {
        for (Sender member : this.getMembers()) {
            member.sendAction(action);
        }
    }

    @Override
    public void sendChat(String chat) {
        for (Sender member : this.getMembers()) {
            member.sendChat(chat);
        }
    }
}
