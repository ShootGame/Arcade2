package pl.themolka.arcade.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.jdom2.JDOMException;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.metadata.ManifestFile;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.settings.SettingsReloadEvent;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.command.CommandPermissionException;
import pl.themolka.commons.session.Session;

import java.io.IOException;
import java.util.logging.Level;

public class ArcadeCommand {
    private final ArcadePlugin plugin;

    public ArcadeCommand(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    //
    // /arcade command
    //

    @CommandInfo(name = "arcade",
            description = "Arcade version and authors",
            usage = "[help|...]")
    public void arcade(Session<ArcadePlayer> sender, CommandContext context) {
        if (context.getParamsLength() > 0) {
            this.params(sender, context);
            return;
        }

        String website = "https://github.com/ShootGame/Arcade2";
        String issues = website + "/issues";

        ManifestFile manifest = this.plugin.getManifest();

        Commands.sendTitleMessage(sender, manifest.getFieldName(), manifest.getFieldVersion());
        sender.send(this.arcadeKeyValue("Version", manifest.getFieldVersion()));
        sender.send(this.arcadeKeyValue("Minecraft Version", manifest.getFieldMcVersion()));
        sender.send(this.arcadeKeyValue("Last Commit", manifest.getFieldGitCommit()));
        sender.send(this.arcadeKeyValue("Author(s)", StringUtils.join(ArcadePlugin.COPYRIGHTS, ", ")));
        sender.send(this.arcadeKeyValue("Apache Maven ID", manifest.getFieldGroupId() + ":" + manifest.getFieldArtifactId()));
        sender.send(this.arcadeKeyValue("Website", website));
        sender.send(this.arcadeKeyValue("Issues? Bugs?", issues));
    }

    private String arcadeKeyValue(String key, String value) {
        return ChatColor.GRAY + key + ": " + ChatColor.GOLD + value;
    }

    //
    // '/arcade <?>' command
    //

    public void params(Session<ArcadePlayer> sender, CommandContext context) {
        switch (context.getParam(0).toLowerCase()) {
            case "reload":
            case "rl":
                this.reload(sender);
                break;
            case "reset":
                this.reset(sender);
                break;
            case "session":
                this.session(sender);
                break;
            case "updater":
                this.updater(sender);
                break;
            default:
                this.help(sender);
        }
    }

    //
    // '/arcade help' command
    //

    private void help(Session<ArcadePlayer> sender) {
        Commands.sendTitleMessage(sender, "Help");
        sender.send(this.helpItem("reload", "Reload settings file"));
        sender.send(this.helpItem("reset", "Reset settings file to the default state"));
        sender.send(this.helpItem("session", "Reload server session"));
        sender.send(this.helpItem("updater", "Check for updates"));
    }

    private String helpItem(String item, String description) {
        return ChatColor.GOLD + "/arcade " + item + ChatColor.RESET + " " + ChatColor.GRAY + " - " + description;
    }

    //
    // '/arcade reload' command
    //

    private void reload(Session<ArcadePlayer> sender) {
        if (!sender.hasPermission("arcade.command.reload")) {
            throw new CommandPermissionException("arcade.command.reload");
        }

        Settings settings = this.plugin.getSettings();
        sender.sendInfo("Reloading settings file...");

        try {
            settings.setDocument(settings.readSettingsFile());
            this.plugin.getEventBus().publish(new SettingsReloadEvent(this.plugin, settings));

            sender.sendSuccess("Successfully reloaded settings file. Well done!");
        } catch (IOException io) {
            io.printStackTrace();
            throw new CommandException("Could not reload settings file: " + io.getMessage());
        } catch (JDOMException jdom) {
            jdom.printStackTrace();
            throw new CommandException("Could not reload XML file: " + jdom.getMessage());
        }
    }

    //
    // '/arcade reset' command
    //

    private void reset(Session<ArcadePlayer> sender) {
        if (!sender.hasPermission("arcade.command.reload")) {
            throw new CommandPermissionException("arcade.command.reload");
        }

        try {
            this.plugin.getSettings().copySettingsFile(true);
            this.reload(sender);
        } catch (IOException io) {
            io.printStackTrace();
            throw new CommandException("Could not reset settings file: " + io.getMessage());
        }
    }

    //
    // '/arcade session' command
    //

    private void session(Session<ArcadePlayer> sender) {
        if (!sender.hasPermission("arcade.command.reload")) {
            throw new CommandPermissionException("arcade.command.reload");
        }

        try {
            this.plugin.getServerSession().deserialize();
        } catch (IOException io) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not reload server-session file: " + io.getMessage(), io);
            throw new CommandException("Could not reload server-session file: " + io.getMessage());
        }
    }

    //
    // '/arcade updater' command
    //

    private void updater(Session<ArcadePlayer> sender) {
        sender.sendInfo("Work in Progress!");
    }
}
