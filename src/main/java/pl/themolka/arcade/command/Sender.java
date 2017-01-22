package pl.themolka.arcade.command;

import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.UUID;

public interface Sender extends Messageable {
    default GamePlayer getGamePlayer() {
        if (this.isConsole()) {
            return this.getPlayer().getGamePlayer();
        } else {
            return null;
        }
    }

    ArcadePlayer getPlayer();

    String getUsername();

    UUID getUuid();

    boolean hasPermission(String permission);

    boolean isConsole();
}
