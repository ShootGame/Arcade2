package pl.themolka.arcade.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.EmptyElement;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Bukkit's {@link ChatColor} and {@link DyeColor} representations.
 */
public enum Color {
    /* colors */
    BLACK("black", ChatColor.BLACK, DyeColor.BLACK),
    DARK_BLUE("dark_blue", ChatColor.DARK_BLUE, DyeColor.BLUE), // A duplicate (dye)
    DARK_GREEN("dark_green", ChatColor.DARK_GREEN, DyeColor.GREEN),
    DARK_AQUA("dark_aqua", ChatColor.DARK_AQUA, DyeColor.CYAN),
    DARK_RED("dark_red", ChatColor.DARK_RED, DyeColor.RED), // B duplicate (dye)
    DARK_PURPLE("dark_purple", ChatColor.DARK_PURPLE, DyeColor.PURPLE),
    GOLD("gold", ChatColor.GOLD, DyeColor.ORANGE),
    GRAY("gray", ChatColor.GRAY, DyeColor.SILVER),
    DARK_GRAY("dark_gray", ChatColor.DARK_GRAY, DyeColor.GRAY), // C duplicate (chat)
    BLUE("blue", ChatColor.BLUE, DyeColor.BLUE), // A duplicate
    GREEN("green", ChatColor.GREEN, DyeColor.LIME),
    AQUA("aqua", ChatColor.AQUA, DyeColor.LIGHT_BLUE),
    RED("red", ChatColor.RED, DyeColor.RED), // B duplicate
    LIGHT_PURPLE("light_purple", ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA), // D duplicate (chat)
    YELLOW("yellow", ChatColor.YELLOW, DyeColor.YELLOW),
    WHITE("white", ChatColor.WHITE, DyeColor.WHITE),

    /* dye colors */
    BROWN("dark_gray", ChatColor.DARK_GRAY, DyeColor.BROWN), // C duplicate
    PINK("light_purple", ChatColor.LIGHT_PURPLE, DyeColor.PINK), // D duplicate

    /* formatting */
    MAGIC("obfuscated", ChatColor.MAGIC),
    BOLD("bold", ChatColor.BOLD),
    STRIKETHROUGH("strikethrough", ChatColor.STRIKETHROUGH),
    UNDERLINE("underline", ChatColor.UNDERLINE),
    ITALIC("italic", ChatColor.ITALIC),

    /* reset */
    RESET("reset", ChatColor.RESET);

    /** Indexed by the color ID. */
    private static final Map<String, Color> byId = new HashMap<>();
    /** Indexed by {@link ChatColor}. */
    private static final Map<ChatColor, Color> byChat = new HashMap<>();
    /** Indexed by component {@link net.md_5.bungee.api.ChatColor}. */
    private static final Map<net.md_5.bungee.api.ChatColor, Color> byComponent = new HashMap<>();
    /** Indexed by {@link DyeColor} */
    private static final Map<DyeColor, Color> byDye = new HashMap<>();

    /**
     * Index values of this enum
     */
    static {
        for (Color value : values()) {
            // ID
            String id = value.getId();
            if (id != null && !id.isEmpty() && !byId.containsKey(id)) {
                byId.put(id, value);
            }

            // chat
            ChatColor chat = value.toChat();
            if (chat != null && !byChat.containsKey(chat)) {
                byChat.put(chat, value);
            }

            // component
            net.md_5.bungee.api.ChatColor component = value.toComponent();
            if (component != null && !byComponent.containsKey(component)) {
                byComponent.put(component, value);
            }

            // dye
            DyeColor dye = value.toDye();
            if (dye != null && !byDye.containsKey(dye)) {
                byDye.put(dye, value);
            }
        }
    }

    /** The unique ID of this color to be represented in chat components */
    private final String id;
    /** Component {@link net.md_5.bungee.api.ChatColor} representation */
    private final net.md_5.bungee.api.ChatColor component;
    /** {@link ChatColor} representation */
    private final ChatColor chat;
    /** {@link DyeColor} representation */
    private final DyeColor dye;

    Color(String id, ChatColor color) {
        this(id, color, null);
    }

    Color(String id, ChatColor chat, DyeColor dye) {
        this.id = id;
        this.component = net.md_5.bungee.api.ChatColor.getByChar(chat.getChar());
        this.chat = chat;
        this.dye = dye;
    }

    /**
     * Get unique ID of this color.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get color of the fireworks
     */
    public org.bukkit.Color getFireworkColor() {
        if (this.isFormat()) {
            throw new IllegalArgumentException(this.name() + " is a format.");
        }

        return this.toDye().getFireworkColor();
    }

    /**
     * Get unique chat identifier of this color
     */
    public char getChar() {
        return this.toChat().getChar();
    }

    /**
     * Get dye data of this this color
     */
    public byte getDyeData() {
        this.requireNonFormat();
        return this.toDye().getDyeData();
    }

    /**
     * Get {@link org.bukkit.material.Wool} data of this color
     */
    public byte getWoolData() {
        this.requireNonFormat();
        return this.toDye().getWoolData();
    }

    /**
     * Get {@link Color} of this color
     */
    public org.bukkit.Color getColor() {
        this.requireNonFormat();
        return this.toDye().getColor();
    }

    /**
     * Is this a real color?
     */
    public boolean isColor() {
        return this.toChat().isColor() && this.id != null;
    }

    /**
     * Is this a formatting code?
     */
    public boolean isFormat() {
        return this.toChat().isFormat() || this.isReset();
    }

    /**
     * Is this a reset formatting code?
     */
    public boolean isReset() {
        return this.equals(RESET);
    }

    /**
     * Convert this color to a {@link ChatColor}
     */
    public ChatColor toChat() {
        return this.chat;
    }

    /**
     * Convert this color to a component {@link net.md_5.bungee.api.ChatColor}
     */
    public net.md_5.bungee.api.ChatColor toComponent() {
        return this.component;
    }

    /**
     * Convert this color to a {@link DyeColor}
     */
    public DyeColor toDye() {
        return this.dye;
    }

    /**
     * Convert this color to the string representation
     */
    @Override
    public String toString() {
        return this.toChat().toString();
    }

    private void requireNonFormat() {
        if (this.isFormat()) {
            throw new IllegalArgumentException(this.name() + " is a format.");
        }
    }

    //
    // Converting
    //

    /**
     * Convert an ID to a color
     * @param id ID representation of a color
     */
    public static Color ofId(String id) {
        return byId.get(id);
    }

    /**
     * Convert a {@link ChatColor} to a color
     * @param chat {@link ChatColor} representation of a color
     */
    public static Color ofChat(ChatColor chat) {
        return byChat.get(chat);
    }

    /**
     * Convert a component {@link net.md_5.bungee.api.ChatColor} to a color
     * @param component Component {@link net.md_5.bungee.api.ChatColor} representation of a color
     */
    public static Color ofComponent(net.md_5.bungee.api.ChatColor component) {
        return byComponent.get(component);
    }

    /**
     * Convert a {@link DyeColor} to a color
     * @param dye {@link DyeColor} representation of a color
     */
    public static Color ofDye(DyeColor dye) {
        return byDye.get(dye);
    }

    //
    // Generating
    //

    /** Local random pseudo-generator */
    private static final Random random = new Random();

    /**
     * Generate a random {@link Color} value
     */
    public static Color random() {
        Color[] values = values();
        return values[random.nextInt(values.length)];
    }

    /**
     * Generate a random ID value
     */
    public static String randomId() {
        String[] values = idValues();
        return values[random.nextInt(values.length)];
    }

    /**
     * Generate a random {@link ChatColor} value
     */
    public static ChatColor randomChat() {
        ChatColor[] values = chatValues();
        return values[random.nextInt(values.length)];
    }

    /**
     * Generate a random component {@link net.minecraft.server.ChatClickable} value
     */
    public static net.md_5.bungee.api.ChatColor randomComponent() {
        net.md_5.bungee.api.ChatColor[] values = componentValues();
        return values[random.nextInt(values.length)];
    }

    /**
     * Generate a random {@link DyeColor} value
     */
    public static DyeColor randomDye() {
        DyeColor[] values = dyeValues();
        return values[random.nextInt(values.length)];
    }

    //
    // Parsing
    //

    /**
     * Parse a {@link Color} from the given query
     * @param color Query to search
     */
    public static Color parse(String color) {
        return parse(color, null);
    }

    /**
     * Parse a {@link Color} from the given query
     * @param color Query to search
     * @param def Default {@link Color} value, if the result gives <code>null</code>
     */
    public static Color parse(String color, Color def) {
        if (color != null) {
            Color value = Parsers.color.parseWithValue(Parsers.element, color).orNull();
            if (value != null) {
                return value;
            }

            ChatColor chat = parseChat(color);
            if (chat != null) {
                return ofChat(chat);
            }

            net.md_5.bungee.api.ChatColor component = parseComponent(color);
            if (component != null) {
                return ofComponent(component);
            }

            DyeColor dye = parseDye(color);
            if (dye != null) {
                return ofDye(dye);
            }
        }

        return def;
    }

    /**
     * Parse a {@link ChatColor} from the given query
     * @param chat Query to search
     */
    public static ChatColor parseChat(String chat) {
        return parseChat(chat, null);
    }

    /**
     * Parse a {@link ChatColor} from the given query
     * @param chat Query to search
     * @param def Default {@link ChatColor} value, if the result gives <code>null</code>
     */
    public static ChatColor parseChat(String chat, ChatColor def) {
        return Parsers.chat.parseWithValue(Parsers.element, chat).or(def);
    }

    /**
     * Parse a component {@link net.md_5.bungee.api.ChatColor} from the given query
     * @param component Query to search
     */
    public static net.md_5.bungee.api.ChatColor parseComponent(String component) {
        return parseComponent(component, null);
    }

    /**
     * Parse a component {@link net.md_5.bungee.api.ChatColor} from the given query
     * @param component Query to search
     * @param def Default component {@link net.md_5.bungee.api.ChatColor} value, if the result gives <code>null</code>
     */
    public static net.md_5.bungee.api.ChatColor parseComponent(String component, net.md_5.bungee.api.ChatColor def) {
        return Parsers.component.parseWithValue(Parsers.element, component).or(def);
    }

    /**
     * Parse a {@link DyeColor} from the given query
     * @param dye Query to searach
     */
    public static DyeColor parseDye(String dye) {
        return parseDye(dye, null);
    }

    /**
     * Parse a {@link DyeColor} from the given query
     * @param dye Query to search
     * @param def Default {@link DyeColor} value, if the result gives <code>null</code>
     */
    public static DyeColor parseDye(String dye, DyeColor def) {
        return Parsers.dye.parseWithValue(Parsers.element, dye).or(def);
    }

    /** Internal parsers used to parse the color objects */
    private static class Parsers {
        static Parser<Color> color = new EnumParser<>(Color.class);
        static Parser<ChatColor> chat = new EnumParser<>(ChatColor.class);
        static Parser<net.md_5.bungee.api.ChatColor> component = new EnumParser<>(net.md_5.bungee.api.ChatColor.class);
        static Parser<DyeColor> dye = new EnumParser<>(DyeColor.class);

        static Element element = EmptyElement.empty();
    }

    //
    // Values
    //

    /**
     * Return values of all IDs of this enum
     */
    public static String[] idValues() {
        Set<String> values = byId.keySet();
        return values.toArray(new String[values.size()]);
    }

    /**
     * Return values of all {@link ChatColor}s of this enum
     */
    public static ChatColor[] chatValues() {
        Set<ChatColor> values = byChat.keySet();
        return values.toArray(new ChatColor[values.size()]);
    }

    /**
     * Return values of all component {@link net.md_5.bungee.api.ChatColor}s of this enum
     */
    public static net.md_5.bungee.api.ChatColor[] componentValues() {
        Set<net.md_5.bungee.api.ChatColor> values = byComponent.keySet();
        return values.toArray(new net.md_5.bungee.api.ChatColor[values.size()]);
    }

    /**
     * Return values of all {@link DyeColor}s of this enum
     */
    public static DyeColor[] dyeValues() {
        Set<DyeColor> values = byDye.keySet();
        return values.toArray(new DyeColor[values.size()]);
    }
}
