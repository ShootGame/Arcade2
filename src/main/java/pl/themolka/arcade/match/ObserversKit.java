package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.kit.ClearInventoryContent;
import pl.themolka.arcade.kit.GameModeContent;
import pl.themolka.arcade.kit.ItemStackContent;
import pl.themolka.arcade.kit.Kit;

public class ObserversKit extends Kit {
    public static final String OBSERVERS_KIT_ID = "_observers-kit";

    public static final GameMode GAME_MODE = GameMode.CREATIVE;
    public static final String TELEPORTER_NAME = ChatColor.AQUA + ChatColor.BOLD.toString() + "Teleporter";
    public static final ItemStack TELEPORTER = new ItemStackBuilder()
            .type(Material.COMPASS)
            .displayName(TELEPORTER_NAME)
            .build();

    public ObserversKit(ArcadePlugin plugin) {
        super(plugin, OBSERVERS_KIT_ID);

        this.buildKit();
    }

    public void buildKit() {
        this.addContent(new ClearInventoryContent(true));
        this.addContent(new GameModeContent(GAME_MODE));

        this.addContent(this.createTeleporter()); // 0
        this.addContent(this.createJoinItem()); // 2
    }

    public ItemStackContent createTeleporter() {
        ItemStackContent content = new ItemStackContent(TELEPORTER);
        content.setSlot(0);
        return content;
    }

    public ItemStackContent createJoinItem() {
        ItemStackContent content = new ItemStackContent(new ItemStackBuilder()
                .type(Material.NETHER_STAR)
                .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Join the game")
                .build());

        content.setSlot(2);
        return content;
    }
}
