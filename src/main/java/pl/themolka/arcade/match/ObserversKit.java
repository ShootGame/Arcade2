package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.kit.ClearInventoryContent;
import pl.themolka.arcade.kit.GameModeContent;
import pl.themolka.arcade.kit.HeldSlotContent;
import pl.themolka.arcade.kit.ItemStackContent;
import pl.themolka.arcade.kit.Kit;

public class ObserversKit extends Kit {
    public static final String OBSERVERS_KIT_ID = "_observers-kit";

    public static final GameMode GAME_MODE = GameMode.CREATIVE;

    // navigation
    public static final String NAVIGATION_NAME = ChatColor.AQUA + ChatColor.BOLD.toString() + "Navigation";
    public static final ItemStack NAVIGATION = new ItemStackBuilder()
            .type(Material.COMPASS)
            .displayName(NAVIGATION_NAME)
            .description(ChatColor.YELLOW + "Left click to teleport to point",
                    ChatColor.YELLOW + "Right click to teleport to highest point")
            .build();

    // play
    public static final String PLAY_NAME = ChatColor.AQUA + ChatColor.BOLD.toString() + "Join the game";
    public static final ItemStack PLAY = new ItemStackBuilder()
            .type(Material.NETHER_STAR)
            .displayName(PLAY_NAME)
            .description(ChatColor.YELLOW + "Click to join the game")
            .build();

    public ObserversKit(ArcadePlugin plugin) {
        super(plugin, OBSERVERS_KIT_ID);

        this.buildKit();
    }

    public void buildKit() {
        this.addContent(new ClearInventoryContent(true));
        this.addContent(new GameModeContent(GAME_MODE));

        this.addContent(this.createNavigationItem()); // 0
        this.addContent(this.createPlayItem()); // 2

        this.addContent(new HeldSlotContent(0));
    }

    public ItemStackContent createNavigationItem() {
        ItemStackContent content = new ItemStackContent(NAVIGATION);
        content.setSlot(0);
        return content;
    }

    public ItemStackContent createPlayItem() {
        ItemStackContent content = new ItemStackContent(PLAY);
        content.setSlot(2);
        return content;
    }
}
