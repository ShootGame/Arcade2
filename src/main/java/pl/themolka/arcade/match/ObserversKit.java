package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.kit.ItemStackContent;
import pl.themolka.arcade.kit.Kit;

public class ObserversKit extends Kit {
    public static final String OBSERVERS_KIT_ID = "_observers-kit";

    public ObserversKit(ArcadePlugin plugin) {
        super(plugin, OBSERVERS_KIT_ID);

        this.buildKit();
    }

    public void buildKit() {
        this.addContent(this.createCompass()); // 0
        this.addContent(this.createJoinItem()); // 2
    }

    public ItemStackContent createCompass() {
        ItemStackContent content = new ItemStackContent(new ItemStackBuilder()
                .type(Material.COMPASS)
                .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Teleporter")
                .build());

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
