package pl.themolka.arcade.channel;

import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.util.StringId;

import java.util.List;

public interface Channel extends Messageable, StringId {
    boolean addMember(Sender member);

    List<Sender> getMembers();

    String getPermission();

    boolean hasMember(Sender member);

    boolean removeMember(Sender member);

    int sendMessage(String message);

    int sendActionMessage(String action);

    int sendChatMessage(String chat);

    int sendChatMessage(Sender author, String chat);

    int sendErrorMessage(String error);

    int sendInfoMessage(String info);

    int sendSuccessMessage(String success);

    int sendTipMessage(String tip);
}
