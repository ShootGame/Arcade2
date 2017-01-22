package pl.themolka.arcade.channel;

import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.util.StringId;

import java.util.List;

public interface Channel extends Messageable, StringId {
    boolean addMember(Sender member);

    List<Sender> getMembers();

    String getPermission();

    boolean removeMember(Sender member);

    void sendChat(Sender author, String chat);
}
