package pl.themolka.arcade.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.jdom2.JDOMException;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadeSession;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.xml.ManifestFile;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.command.CommandPermissionException;
import pl.themolka.commons.command.CommandUsageException;

import java.io.IOException;

public class ArcadeCommand {
    private final ArcadePlugin plugin;

    public ArcadeCommand(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @CommandInfo(name = "arcade",
            description = "Arcade version and authors",
            usage = "[help|...]")
    public void arcade(ArcadeSession sender, CommandContext context) {
        if (context.getParamsLength() > 0) {
            this.params(sender, context);
            return;
        }

        String website = "https://github.com/ShootGame/Arcade2";
        String issues = website + "/issues";

        ManifestFile manifest = this.plugin.getManifest();

        sender.sendTitleMessage(manifest.getFieldName(), manifest.getFieldVersion());
        sender.send(this.arcadeKeyValue("Version", manifest.getFieldVersion()));
        sender.send(this.arcadeKeyValue("Minecraft Version", manifest.getFieldMcVersion()));
        sender.send(this.arcadeKeyValue("Last Commit", manifest.getFieldGitCommit()));
        sender.send(this.arcadeKeyValue("Author(s)", StringUtils.join(ArcadePlugin.COPYRIGHTS, ", ")));
        sender.send(this.arcadeKeyValue("Maven ID", manifest.getFieldGroupId() + ":" + manifest.getFieldArtifactId()));
        sender.send(this.arcadeKeyValue("Website", website));
        sender.send(this.arcadeKeyValue("Issues, Bugs, Questions", issues));
    }

    private String arcadeKeyValue(String key, String value) {
        return ChatColor.GRAY + key + ": " + ChatColor.GOLD + value;
    }

    public void params(ArcadeSession sender, CommandContext context) {
        switch (context.getParam(0)) {
            case "help":
            case "?":
                this.help(sender);
                break;
            case "reload":
            case "rl":
                this.reload(sender);
                break;
            case "reset":
                this.reset(sender);
                break;
            case "updater":
                this.updater(sender);
                break;
            default:
                throw new CommandUsageException();
        }
    }

    private void help(ArcadeSession sender) {
        if (!sender.hasPermission("arcade.command.help")) {
            throw new CommandPermissionException("arcade.command.help");
        }

        sender.sendTitleMessage("Help");
        sender.send(this.helpItem("reload", "Reload settings file"));
        sender.send(this.helpItem("reset", "Reset settings file to the default state"));
        sender.send(this.helpItem("updater", "Check for updates"));
    }

    private String helpItem(String item, String description) {
        return ChatColor.GOLD + "/arcade " + item + ChatColor.RESET + " " + ChatColor.GRAY + " - " + description;
    }

    private void reload(ArcadeSession sender) {
        if (!sender.hasPermission("arcade.command.reload")) {
            throw new CommandPermissionException("arcade.command.reload");
        }

        Settings settings = this.plugin.getSettings();
        sender.sendInfo("Reloading settings file...");

        try {
            settings.readSettingsFile();
            sender.sendSuccess("Successfully reloaded settings file. Well done!");
        } catch (IOException io) {
            sender.sendError("Could not reload settings file - see console.");
            io.printStackTrace();

            sender.sendError(io.getClass().getName() + ": " + io.getLocalizedMessage());
        } catch (JDOMException jdom) {
            sender.sendError("Could not reload XML file - see console.");
            jdom.printStackTrace();

            sender.sendError(jdom.getClass().getName() + ": " + jdom.getLocalizedMessage());
        }
    }

    private void reset(ArcadeSession sender) {
        if (!sender.hasPermission("arcade.command.reload")) {
            throw new CommandPermissionException("arcade.command.reload");
        }

        try {
            this.plugin.getSettings().copySettingsFile(true);
            this.reload(sender);
        } catch (IOException io) {
            sender.sendError("Could not reset settings file - see console.");
            io.printStackTrace();

            sender.sendError(io.getClass().getName() + ": " + io.getLocalizedMessage());
        }
    }

    private void updater(ArcadeSession sender) {
        if (!sender.hasPermission("arcade.command.updater")) {
            throw new CommandPermissionException("arcade.command.updater");
        }

        sender.sendInfo("Work in Progress!");
    }
}
