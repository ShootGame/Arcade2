package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadePlayer;

import java.util.UUID;
import java.util.logging.Level;

public class ConsoleSender implements Sender {
    public static final String CONSOLE_NAME = "Console";
    public static final UUID CONSOLE_UUID = UUID.nameUUIDFromBytes(("Internal:" + CONSOLE_NAME).getBytes());

    private final ArcadePlugin plugin;

    public ConsoleSender(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ArcadePlayer getPlayer() {
        throw new UnsupportedOperationException("Not supported by the console.");
    }

    @Override
    public String getUsername() {
        return CONSOLE_NAME;
    }

    @Override
    public UUID getUuid() {
        return CONSOLE_UUID;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public void send(String message) {
        this.plugin.getLogger().log(Level.INFO, ChatColor.stripColor(message));
    }
}
