package pl.themolka.arcade.command;

import org.bukkit.ChatColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.map.Author;
import pl.themolka.arcade.map.MapContainer;
import pl.themolka.arcade.map.OfflineMap;
import pl.themolka.arcade.util.pagination.DynamicPagination;

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
    public void mapInfo(Sender sender, CommandContext context) {
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

    public List<String> mapInfoCompleter(Sender sender, CommandContext context) {
        String request = context.getParams(0);
        if (request == null) {
            request = "";
        }

        List<String> array = this.mapCompleter();
        array.addAll(Arrays.asList("-current", "-next"));
        Collections.sort(array);

        List<String> results = this.mapCompleter();
        for (String item : array) {
            if (item.toLowerCase().startsWith(request.toLowerCase())) {
                results.add(item);
            }
        }

        return results;
    }

    private void mapInfoDescribe(Sender sender, OfflineMap map) {
        CommandUtils.sendTitleMessage(sender, map.getName(), "v" + map.getVersion().toString());
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

    @CommandInfo(name = {"maplist", "maps", "ml"},
            description = "Show all loaded maps",
            usage = "[# page]",
            permission = "arcade.command.maplist")
    public void mapList(Sender sender, CommandContext context) {
        int paramPage = context.getParamInt(0, 1);

        MapContainer container = this.plugin.getMaps().getContainer();
        DynamicPagination pagination = new DynamicPagination.Builder()
                .description(ChatColor.GOLD + "Next page: /" + context.getLabel() + " " + (paramPage + 1))
                .items(new ArrayList<>(container.getMaps()))
                .title("Map List")
                .build();

        if (paramPage < 1 || paramPage > pagination.getPages()) {
            throw new CommandException("Page #" + paramPage + " not found.");
        }

        pagination.display(sender, paramPage);
    }

    //
    // /nextmap command
    //

    @CommandInfo(name = {"nextmap", "mapnext", "nm", "mn", "next"},
            description = "Describe next map",
            permission = "arcade.command.nextmap")
    public void nextMap(Sender sender, CommandContext context) {
        if (this.plugin.getGames().isNextRestart()) {
            throw new CommandException(this.plugin.getServerName() + " will be restarted.");
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
    public void setNext(Sender sender, CommandContext context) {
        boolean paramAfter = context.hasFlag("a") || context.hasFlag("after");
        boolean paramCurrent = context.hasFlag("c") || context.hasFlag("current");
        boolean paramRestart = context.hasFlag("r") || context.hasFlag("restart");
        String paramMap = context.getParams(0);

        if (paramRestart) {
            this.plugin.getGames().setNextRestart(true);
            throw new CommandException(this.plugin.getServerName() + " will be restarted after this game.");
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

    public List<String> setNextCompleter(Sender sender, CommandContext context) {
        String request = context.getParams(0);
        if (request == null) {
            request = "";
        }

        List<String> array = this.mapCompleter();
        array.addAll(Arrays.asList("-add", "-current", "-restart"));
        Collections.sort(array);

        List<String> results = this.mapCompleter();
        for (String item : array) {
            if (item.toLowerCase().startsWith(request.toLowerCase())) {
                results.add(item);
            }
        }

        return results;
    }

    private void setNextMap(Sender sender, OfflineMap map, boolean next) {
        GameManager games = this.plugin.getGames();

        games.setNextRestart(false);
        if (next) {
            games.getQueue().setNextMap(map);
            sender.sendSuccess(map.getName() + " has been set to next in the queue.");
        } else {
            games.getQueue().addMap(map);
            sender.sendSuccess(map.getName() + " has been added to the queue.");
        }
    }
}
