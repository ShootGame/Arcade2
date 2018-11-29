/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.settings.Settings;
import pl.themolka.arcade.settings.SettingsReloadEvent;
import pl.themolka.arcade.util.ManifestFile;

import java.io.IOException;

public class ArcadeCommands {
    private final ArcadePlugin plugin;

    public ArcadeCommands(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    //
    // /arcade command
    //

    @CommandInfo(name = "arcade",
            description = "Arcade version and authors",
            usage = "[help|...]")
    public void arcade(Sender sender, CommandContext context) {
        if (context.getParamsLength() > 0) {
            this.params(sender, context);
            return;
        }

        ManifestFile manifest = this.plugin.getManifest();

        CommandUtils.sendTitleMessage(sender, manifest.getFieldName(), manifest.getFieldVersion());
        sender.send(this.arcadeKeyValue("Version", manifest.getFieldVersion()));
        sender.send(this.arcadeKeyValue("Minecraft Version", manifest.getFieldMcVersion()));
        sender.send(this.arcadeKeyValue("Last Commit", manifest.getFieldGitCommit()));
        sender.send(this.arcadeKeyValue("Author(s)", StringUtils.join(ArcadePlugin.COPYRIGHTS, ", ")));
        sender.send(this.arcadeKeyValue("Apache Maven ID", manifest.getFieldGroupId() + ":" + manifest.getFieldArtifactId()));
        sender.send(this.arcadeKeyValue("Website", ArcadePlugin.PROJECT_WEBSITE));
        sender.send(this.arcadeKeyValue("Issues? Bugs?", ArcadePlugin.PROJECT_BUG_TRACKER));
    }

    private String arcadeKeyValue(String key, String value) {
        return ChatColor.GRAY + key + ": " + ChatColor.GOLD + value;
    }

    //
    // '/arcade <?>' command
    //

    public void params(Sender sender, CommandContext context) {
        switch (context.getParam(0).toLowerCase()) {
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
                this.help(sender);
        }
    }

    //
    // '/arcade help' command
    //

    private void help(Sender sender) {
        CommandUtils.sendTitleMessage(sender, "Help");
        sender.send(this.helpItem("reload", "Reload settings file"));
        sender.send(this.helpItem("reset", "Reset settings file to the default state"));
        sender.send(this.helpItem("session", "Reload server session"));
        sender.send(this.helpItem("updater", "Check for updates"));
    }

    private String helpItem(String item, String description) {
        return ChatColor.GOLD + "/arcade " + item + ChatColor.RESET + ChatColor.GRAY + " - " + description;
    }

    //
    // '/arcade reload' command
    //

    private void reload(Sender sender) {
        if (!sender.hasPermission("arcade.command.reload")) {
            throw new CommandPermissionException("arcade.command.reload");
        }

        Settings settings = this.plugin.getSettings();
        sender.sendInfo("Reloading settings file...");

        try {
            settings.setDocument(settings.readSettingsFile());
            this.plugin.getEventBus().publish(new SettingsReloadEvent(this.plugin, settings));

            sender.sendSuccess("Successfully reloaded settings file. Well done!");
        } catch (DOMException | IOException | ParserNotSupportedException ex) {
            ex.printStackTrace();
            throw new CommandException("Could not reload settings file: " + ex.getMessage());
        }
    }

    //
    // '/arcade reset' command
    //

    private void reset(Sender sender) {
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
    // '/arcade updater' command
    //

    private void updater(Sender sender) {
        sender.sendInfo("Work in Progress!");
    }
}
