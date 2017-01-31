package pl.themolka.arcade.channel;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.CommandInfo;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

@ModuleInfo(id = "channels")
public class ChannelsModule extends Module<ChannelsGame> {
    @Override
    public ChannelsGame buildGameModule(Element xml, Game game) throws JDOMException {
        return new ChannelsGame(new GlobalChatChannel(this.getPlugin(), GlobalChatChannel.GLOBAL_FORMAT));
    }

    @CommandInfo(name = {"global", "g", "!"},
            description = "Send a message to the global channel",
            min = 1,
            flags = {"c", "console"},
            usage = "[-console] <message...>",
            permission = "arcade.channel.global")
    public void global(Sender sender, CommandContext context) {
        if (!this.isGameModuleEnabled()) {
            throw new CommandException("Channels module is not enabled in this game.");
        }

        boolean console = context.hasFlag("c") || context.hasFlag("console");
        if (console && sender.hasPermission("arcade.channel.global.console")) {
            sender = this.getPlugin().getConsole();
        }

        this.getGameModule().getGlobalChannel().sendChat(sender, context.getParams(0));
    }
}
