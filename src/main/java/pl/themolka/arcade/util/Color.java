package pl.themolka.arcade.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.xml.XMLChatColor;
import pl.themolka.arcade.xml.XMLDyeColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Bukkit's {@link ChatColor} and {@link DyeColor} representations.
 */
public enum Color {
    /* colors */
    BLACK(ChatColor.BLACK, DyeColor.BLACK),
    DARK_BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN),
    DARK_AQUA(ChatColor.DARK_AQUA, DyeColor.CYAN),
    DARK_RED(ChatColor.DARK_RED, DyeColor.RED),
    DARK_PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE),
    GOLD(ChatColor.GOLD, DyeColor.ORANGE),
    GRAY(ChatColor.GRAY, DyeColor.SILVER),
    DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY),
    BLUE(ChatColor.BLUE, DyeColor.LIGHT_BLUE),
    GREEN(ChatColor.GRAY, DyeColor.LIME),
    AQUA(ChatColor.AQUA, DyeColor.LIGHT_BLUE),
    RED(ChatColor.RED, DyeColor.RED),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW),
    WHITE(ChatColor.WHITE, DyeColor.WHITE),

    /* formatting */
    MAGIC(ChatColor.MAGIC),
    BOLD(ChatColor.BOLD),
    STRIKETHROUGH(ChatColor.STRIKETHROUGH),
    UNDERLINE(ChatColor.UNDERLINE),
    ITALIC(ChatColor.ITALIC),

    /* reset */
    RESET(ChatColor.RESET);

    /** Indexed by {@link ChatColor}. */
    private static final Map<ChatColor, Color> byChat = new HashMap<>();
    /** Indexed by {@link DyeColor} */
    private static final Map<DyeColor, Color> byDye = new HashMap<>();

    /**
     * Index values of this enum
     */
    static {
        for (Color value : values()) {
            // chat
            if (value.toChat() != null) {
                byChat.put(value.toChat(), value);
            }

            // dye
            if (value.toDye() != null) {
                byDye.put(value.toDye(), value);
            }
        }
    }

    /** {@link ChatColor} representation */
    private final ChatColor chat;
    /** {@link DyeColor} representation */
    private final DyeColor dye;

    Color(ChatColor color) {
        this(color, null);
    }

    Color(ChatColor chat, DyeColor dye) {
        this.chat = chat;
        this.dye = dye;
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
        if (this.isFormat()) {
            throw new IllegalArgumentException(this.name() + " is a format.");
        }

        return this.toDye().getDyeData();
    }

    /**
     * Get {@link org.bukkit.material.Wool} data of this color
     */
    public byte getWoolData() {
        if (this.isFormat()) {
            throw new IllegalArgumentException(this.name() + " is a format.");
        }

        return this.toDye().getWoolData();
    }

    /**
     * Get {@link Color} of this color
     */
    public org.bukkit.Color getColor() {
        if (this.isFormat()) {
            throw new IllegalArgumentException(this.name() + " is a format.");
        }

        return this.toDye().getColor();
    }

    /**
     * Is it a real color?
     */
    public boolean isColor() {
        return this.toChat().isColor();
    }

    /**
     * Is it a formatting code?
     */
    public boolean isFormat() {
        return this.toChat().isFormat() || this.isReset();
    }

    /**
     * Is it a reset formatting code?
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
     * Convert this color to a {@link DyeColor}
     */
    public DyeColor toDye() {
        return this.dye;
    }

    /**
     * Convert this color a the string representation
     */
    @Override
    public String toString() {
        return this.toChat().toString();
    }

    //
    // Converting
    //

    /**
     * Convert a {@link ChatColor} to a color
     * @param chat {@link ChatColor} representation of a color
     */
    public static Color ofChat(ChatColor chat) {
        return byChat.get(chat);
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
     * Generate a random {@link ChatColor} value
     */
    public static ChatColor randomChat() {
        ChatColor[] values = chatValues();
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
     * Parse a {@link ChatColor} from the given query
     * @param chat Query to searach
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
        return XMLChatColor.parse(chat, def);
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
        return XMLDyeColor.parse(dye, def);
    }

    //
    // Values
    //

    /**
     * Return values of all {@link ChatColor}s of this enum
     */
    public static ChatColor[] chatValues() {
        Set<ChatColor> values = byChat.keySet();
        return values.toArray(new ChatColor[values.size()]);
    }

    /**
     * Return values of all {@link DyeColor}s of this enum
     */
    public static DyeColor[] dyeValues() {
        Set<DyeColor> values = byDye.keySet();
        return values.toArray(new DyeColor[values.size()]);
    }
}
