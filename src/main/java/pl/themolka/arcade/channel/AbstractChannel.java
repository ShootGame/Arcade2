package pl.themolka.arcade.channel;

import pl.themolka.arcade.command.Sender;

import java.util.List;

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
        this.sendMessage(message);
    }

    @Override
    public int sendMessage(String message) {
        List<Sender> members = this.getMembers();
        for (Sender member : members) {
            member.send(message);
        }

        return members.size();
    }

    @Override
    public void sendAction(String action) {
        this.sendActionMessage(action);
    }

    @Override
    public int sendActionMessage(String action) {
        List<Sender> members = this.getMembers();
        for (Sender member : members) {
            member.sendAction(action);
        }

        return members.size();
    }

    @Override
    public void sendChat(String chat) {
        this.sendChatMessage(chat);
    }

    @Override
    public int sendChatMessage(String chat) {
        List<Sender> members = this.getMembers();
        for (Sender member : members) {
            member.sendChat(chat);
        }

        return members.size();
    }

    @Override
    public void sendError(String error) {
        this.sendErrorMessage(error);
    }

    @Override
    public int sendErrorMessage(String error) {
        List<Sender> members = this.getMembers();
        for (Sender member : members) {
            member.sendError(error);
        }

        return members.size();
    }

    @Override
    public void sendInfo(String info) {
        this.sendInfoMessage(info);
    }

    @Override
    public int sendInfoMessage(String info) {
        List<Sender> members = this.getMembers();
        for (Sender member : members) {
            member.sendInfo(info);
        }

        return members.size();
    }

    @Override
    public void sendSuccess(String success) {
        this.sendSuccessMessage(success);
    }

    @Override
    public int sendSuccessMessage(String success) {
        List<Sender> members = this.getMembers();
        for (Sender member : members) {
            member.sendSuccess(success);
        }

        return members.size();
    }

    @Override
    public void sendTip(String tip) {
        this.sendTipMessage(tip);
    }

    @Override
    public int sendTipMessage(String tip) {
        List<Sender> members = this.getMembers();
        for (Sender member : members) {
            member.sendTip(tip);
        }

        return members.size();
    }
}
