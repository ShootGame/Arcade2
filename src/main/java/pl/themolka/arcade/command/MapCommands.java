package pl.themolka.arcade.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.map.Author;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadeSession;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapCommands {
    private final ArcadePlugin plugin;

    public MapCommands(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    //
    // Utility
    //

    public List<String> mapCompleter() {
        List<String> results = new ArrayList<>();
        for (OfflineMap map : this.plugin.getMaps().getContainer().getMaps()) {
            results.add(map.getName());
        }

        return results;
    }

    //
    // /mapinfo command
    //

    @CommandInfo(name = {"mapinfo", "map"},
            description = "Describe a map",
            flags = {"c", "current", "n", "next"},
            usage = "[-current|-next|<map...>]",
            permission = "arcade.command.mapinfo",
            completer = "mapInfoCompleter")
    public void mapInfo(ArcadeSession sender, CommandContext context) {
        boolean paramCurrent = context.hasFlag("c") || context.hasFlag("current");
        boolean paramNext = context.hasFlag("n") || context.hasFlag("next");
        String paramMap = context.getParams(0);

        List<OfflineMap> results = new ArrayList<>();
        if (paramCurrent || context.getArgs().length == 0) {
            results.add(this.plugin.getGames().getCurrentGame().getMap().getMapInfo());
        } else if (paramNext) {
            results.add(this.plugin.getGames().getCurrentGame().getMap().getMapInfo());
        } else {
            results.addAll(this.plugin.getMaps().findMap(paramMap));
        }

        if (results.isEmpty()) {
            throw new CommandException("No results found.");
        } else if (results.size() > 1) {
            sender.sendError("Found " + results.size() + " results. Displaying the best one...");
        }

        this.mapInfoDescribe(sender, results.get(0));
    }

    public List<String> mapInfoCompleter(ArcadeSession sender, CommandContext context) {
        List<String> results = this.mapCompleter();
        results.addAll(Arrays.asList("-current", "-next"));
        return results;
    }

    private void mapInfoDescribe(ArcadeSession sender, OfflineMap map) {
        sender.sendTitleMessage(map.getName(), map.getVersion().toString());
        sender.send(ChatColor.GOLD + map.getDescription());
        sender.send(ChatColor.GRAY + "Version: " + ChatColor.GOLD + map.getVersion());

        if (!map.getAuthors().isEmpty()) {
            sender.send(ChatColor.GRAY + "By:");
            for (Author author : map.getAuthors()) {
                sender.send(author.toString());
            }
        }
    }

    //
    // /maplist command
    //

    private String mapListResult;

    @CommandInfo(name = {"maplist", "maps", "ml"},
            description = "Show all loaded maps",
            permission = "arcade.command.maplist")
    public void mapList(ArcadeSession sender, CommandContext context) {
        if (this.mapListResult == null) {
            this.mapListResult = StringUtils.join(this.plugin.getMaps().getContainer().getMaps(), ChatColor.GRAY + ", ");
        }

        sender.send(this.mapListResult);
    }

    //
    // /nextmap command
    //

    @CommandInfo(name = {"nextmap", "mapnext", "nm", "mn", "next"},
            description = "Describe next map",
            permission = "arcade.command.nextmap")
    public void nextMap(ArcadeSession sender, CommandContext context) {
        if (this.plugin.getGames().isNextRestart()) {
            sender.sendError("Server will be restarted.");
        } else {
            OfflineMap next = this.plugin.getGames().getQueue().getNextMap();
            this.mapInfoDescribe(sender, next);
        }
    }

    //
    // /setnext command
    //

    @CommandInfo(name = {"setnext", "sn"},
            description = "Set next map in the queue",
            min = 1,
            flags = {"c", "current", "r", "restart"},
            usage = "[-add] [-current|-restart|<map...>]",
            permission = "arcade.command.setnext",
            completer = "setNextCompleter")
    public void setNext(ArcadeSession sender, CommandContext context) {
        boolean paramAdd = context.hasFlag("a") || context.hasFlag("add");
        boolean paramCurrent = context.hasFlag("c") || context.hasFlag("current");
        boolean paramRestart = context.hasFlag("r") || context.hasFlag("restart");
        String paramMap = context.getParams(0);

        if (paramRestart) {
            this.plugin.getGames().setNextRestart(true);
            sender.sendError("Server will be restarted after thus game ends.");
            return;
        }

        List<OfflineMap> results = new ArrayList<>();
        if (paramCurrent || context.getArgs().length == 0) {
            results.add(this.plugin.getGames().getCurrentGame().getMap().getMapInfo());
        } else {
            results.addAll(this.plugin.getMaps().findMap(paramMap));
        }

        if (results.isEmpty()) {
            throw new CommandException("No results found.");
        } else if (results.size() > 1) {
            sender.sendError("Found " + results.size() + " results. Setting next the best one...");
        }

        this.setNextMap(sender, results.get(0), paramAdd);
    }

    public List<String> setNextCompleter(ArcadeSession sender, CommandContext context) {
        List<String> results = this.mapCompleter();
        results.addAll(Arrays.asList("-add", "-current", "-restart"));
        return results;
    }

    private void setNextMap(ArcadeSession sender, OfflineMap map, boolean add) {
        GameManager games = this.plugin.getGames();

        if (add) {
            games.getQueue().addMap(map);
            sender.sendSuccess(map.getName() + " has been added to the queue.");
        } else {
            games.getQueue().setNextMap(map);
            sender.sendSuccess(map.getName() + " has been set to next in the queue.");
        }
    }
}
