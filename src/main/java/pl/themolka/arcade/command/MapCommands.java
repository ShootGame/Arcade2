package pl.themolka.arcade.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.map.Author;
import pl.themolka.arcade.map.MapContainer;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.command.CommandUsageException;
import pl.themolka.commons.session.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
            flags = {"c", "current",
                    "n", "next"},
            usage = "[-current|-next|<map...>]",
            permission = "arcade.command.mapinfo",
            completer = "mapInfoCompleter")
    public void mapInfo(Session<ArcadePlayer> sender, CommandContext context) {
        boolean paramCurrent = context.hasFlag("c") || context.hasFlag("current");
        boolean paramNext = context.hasFlag("n") || context.hasFlag("next");
        String paramMap = context.getParams(0);

        List<OfflineMap> results = new ArrayList<>();
        if (paramCurrent || context.getArgs().length == 0) {
            Game game = this.plugin.getGames().getCurrentGame();
            if (game == null) {
                throw new CommandException("No game running right now.");
            }

            results.add(game.getMap().getMapInfo());
        } else if (paramNext) {
            OfflineMap next = this.plugin.getGames().getQueue().getNextMap();
            if (next == null) {
                throw new CommandException("The map queue is empty.");
            }

            results.add(next);
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

    public List<String> mapInfoCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        List<String> results = this.mapCompleter();
        results.addAll(Arrays.asList("-current", "-next"));

        Collections.sort(results);
        return results;
    }

    private void mapInfoDescribe(Session<ArcadePlayer> sender, OfflineMap map) {
        Commands.sendTitleMessage(sender, map.getName(), map.getVersion().toString());
        if (map.hasDescription() && !map.getDescription().isEmpty()) {
            sender.send(ChatColor.GOLD + map.getDescription());
        }
        sender.send(ChatColor.GRAY + ChatColor.BOLD.toString() + "Version: " + ChatColor.RESET + ChatColor.GOLD + map.getVersion());

        if (map.hasAuthors()) {
            sender.send(ChatColor.GRAY + ChatColor.BOLD.toString() + "Authors:");

            for (Author author : map.getAuthors()) {
                if (!author.hasUsername()) {
                    continue;
                }

                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.GRAY).append(" - ");

                builder.append(ChatColor.GOLD).append(author.getUsername());
                if (author.hasDescription()) {
                    builder.append(ChatColor.GRAY).append(ChatColor.ITALIC).append(" (").append(author.getDescription()).append(")");
                }

                sender.send(builder.toString());
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
    public void mapList(Session<ArcadePlayer> sender, CommandContext context) {
        MapContainer container = this.plugin.getMaps().getContainer();
        if (this.mapListResult == null) {
            this.mapListResult = StringUtils.join(container.getMaps(), ChatColor.GRAY + ", ");
        }

        Commands.sendTitleMessage(sender, "Map List", Integer.toString(container.getMaps().size()));
        sender.send(this.mapListResult);
    }

    //
    // /nextmap command
    //

    @CommandInfo(name = {"nextmap", "mapnext", "nm", "mn", "next"},
            description = "Describe next map",
            permission = "arcade.command.nextmap")
    public void nextMap(Session<ArcadePlayer> sender, CommandContext context) {
        if (this.plugin.getGames().isNextRestart()) {
            throw new CommandException("Server will be restarted.");
        } else {
            OfflineMap next = this.plugin.getGames().getQueue().getNextMap();
            if (next == null) {
                throw new CommandException("The map queue is empty.");
            }

            this.mapInfoDescribe(sender, next);
        }
    }

    //
    // /setnext command
    //

    @CommandInfo(name = {"setnext", "sn"},
            description = "Set next map in the queue",
            flags = {"a", "after",
                    "c", "current",
                    "r", "restart"},
            usage = "[-after] [-current|-restart|<map...>]",
            permission = "arcade.command.setnext",
            completer = "setNextCompleter")
    public void setNext(Session<ArcadePlayer> sender, CommandContext context) {
        boolean paramAfter = context.hasFlag("a") || context.hasFlag("after");
        boolean paramCurrent = context.hasFlag("c") || context.hasFlag("current");
        boolean paramRestart = context.hasFlag("r") || context.hasFlag("restart");
        String paramMap = context.getParams(0);

        this.plugin.getGames().setNextRestart(paramRestart);
        if (paramRestart) {
            throw new CommandException("Server will be restarted after this game.");
        }

        List<OfflineMap> results = new ArrayList<>();
        if (paramCurrent) {
            Game game = this.plugin.getGames().getCurrentGame();
            if (game == null) {
                throw new CommandException("No game running right now.");
            }

            results.add(game.getMap().getMapInfo());
        } else if (paramMap != null) {
            results.addAll(this.plugin.getMaps().findMap(paramMap));
        } else {
            throw new CommandUsageException("You need to type map name, use -current or -restart flag");
        }

        if (results.isEmpty()) {
            throw new CommandException("No results found.");
        } else if (results.size() > 1) {
            sender.sendError("Found " + results.size() + " results. Setting next the best one...");
        }

        this.setNextMap(sender, results.get(0), !paramAfter);
    }

    public List<String> setNextCompleter(Session<ArcadePlayer> sender, CommandContext context) {
        List<String> results = this.mapCompleter();
        results.addAll(Arrays.asList("-add", "-current", "-restart"));

        Collections.sort(results);
        return results;
    }

    private void setNextMap(Session<ArcadePlayer> sender, OfflineMap map, boolean setNext) {
        GameManager games = this.plugin.getGames();

        if (setNext) {
            games.getQueue().setNextMap(map);
            sender.sendSuccess(map.getName() + " has been set to next in the queue.");
        } else {
            games.getQueue().addMap(map);
            sender.sendSuccess(map.getName() + " has been added to the queue.");
        }
    }
}
