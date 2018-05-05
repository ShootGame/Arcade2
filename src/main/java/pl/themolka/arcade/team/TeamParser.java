package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.util.Color;

import java.util.Collections;
import java.util.Set;

@Produces(Team.Config.class)
public class TeamParser extends ConfigParser<Team.Config>
                        implements InstallableParser {
    private Parser<ChatColor> chatColorParser;
    private Parser<DyeColor> dyeColorParser;
    private Parser<Boolean> friendlyFireParser;
    private Parser<Integer> minPlayers;
    private Parser<Integer> maxPlayers;
    private Parser<String> nameParser;
    private Parser<Integer> slotsParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.chatColorParser = context.type(ChatColor.class);
        this.dyeColorParser = context.type(DyeColor.class);
        this.friendlyFireParser = context.type(Boolean.class);
        this.minPlayers = context.type(Integer.class);
        this.maxPlayers = context.type(Integer.class);
        this.nameParser = context.type(String.class);
        this.slotsParser = context.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("team");
    }

    @Override
    protected ParserResult<Team.Config> parseNode(Node node, String name, String value) throws ParserException {
        String id = this.parseRequiredId(node);
        ChatColor chatColor = this.chatColorParser.parse(node.property("color", "chat-color", "chatcolor", "chat")).orFail();
        DyeColor dyeColor = this.parseDyeColor(chatColor, node.property("dye-color", "dyecolor", "dye"));
        boolean friendlyFire = this.friendlyFireParser.parse(node.property("friendly-fire", "friendlyfire", "friendly"))
                .orDefault(Team.Config.DEFAULT_IS_FRIENDLY_FIRE);
        int minPlayers = this.parseMinPlayers(node.property("min-players", "minplayers", "min"));
        int maxPlayers = this.parseMaxPlayers(node.property("max-players", "maxplayers", "max", "overfill"));
        String teamName = this.nameParser.parse(node.property("name", "title")).orFail();
        int slots = this.slotsParser.parse(node.property("slots")).orFail();

        return ParserResult.fine(node, name, value, new Team.Config() {
            public String id() { return id; }
            public ChatColor chatColor() { return chatColor; }
            public DyeColor dyeColor() { return dyeColor; }
            public boolean friendlyFire() { return friendlyFire; }
            public int minPlayers() { return minPlayers; }
            public int maxPlayers() { return maxPlayers; }
            public String name() { return teamName; }
            public int slots() { return slots; }
        });
    }

    protected DyeColor parseDyeColor(ChatColor chatColor, Property property) throws ParserException {
        DyeColor dyeColor = this.dyeColorParser.parse(property).orDefaultNull();
        if (dyeColor != null) {
            return dyeColor;
        }

        Color localColor = Color.ofChat(chatColor);
        if (localColor == null) {
            throw this.fail(property, property.getName(), property.getValue(), "Unknown chat color type");
        }

        dyeColor = localColor.toDye();
        if (dyeColor == null) {
            throw this.fail(property, property.getName(), property.getValue(), "Given chat color type cannot be converted into a dye color");
        }

        return dyeColor;
    }

    protected int parseMinPlayers(Property property) throws ParserException {
        int minPlayers = this.minPlayers.parse(property).orDefault(Team.Config.DEFAULT_MIN_PLAYERS);
        if (minPlayers < 0) {
            throw this.fail(property, property.getName(), property.getValue(), "Minimal player count cannot be negative (smaller than 0)");
        }

        return minPlayers;
    }

    protected int parseMaxPlayers(Property property) throws ParserException {
        int maxPlayers = this.maxPlayers.parse(property).orDefault(Team.Config.DEFAULT_MAX_PLAYERS);
        if (maxPlayers < 0) {
            throw this.fail(property, property.getName(), property.getValue(), "Maximal player count cannot be negative (smaller than 0)");
        }

        return maxPlayers;
    }
}
