package pl.themolka.arcade.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.session.ArcadeSession;
import pl.themolka.arcade.xml.ManifestFile;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;

public class GeneralCommands {
    private final ArcadePlugin plugin;

    public GeneralCommands(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @CommandInfo(name = "arcade", description = "Arcade version and authors")
    public void arcade(ArcadeSession sender, CommandContext context) {
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
}
