package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.kit.content.ClearInventoryContent;
import pl.themolka.arcade.kit.content.EffectContent;
import pl.themolka.arcade.kit.content.GameModeContent;
import pl.themolka.arcade.kit.content.HeldSlotContent;
import pl.themolka.arcade.kit.content.ItemStackContent;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.potion.PotionEffectBuilder;

public class ObserversKit extends Kit {
    public static final String OBSERVERS_KIT_ID = "_observers-kit";

    public static final GameMode GAME_MODE = GameMode.CREATIVE;

    // items
    public static final ItemStack NAVIGATION = new ItemStackBuilder()
            .type(Material.COMPASS)
            .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Navigation")
            .description(ChatColor.YELLOW + "Left click to teleport to point",
                    ChatColor.YELLOW + "Right click to teleport to highest point")
            .build();
    public static final ItemStack PLAY = new ItemStackBuilder()
            .type(Material.NETHER_STAR)
            .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Join the game")
            .description(ChatColor.YELLOW + "Click to join the game")
            .build();

    // effects
    public static final PotionEffect SPEED = new PotionEffectBuilder()
            .type(PotionEffectType.SPEED)
            .duration(PotionEffectBuilder.DURATION_INFINITY)
            .amplifier(1)
            .particles(false)
            .build();
    public static final PotionEffect NIGHT_VISION = new PotionEffectBuilder()
            .type(PotionEffectType.NIGHT_VISION)
            .duration(PotionEffectBuilder.DURATION_INFINITY)
            .amplifier(0)
            .particles(false)
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

        this.addContent(new EffectContent(SPEED));
        this.addContent(new EffectContent(NIGHT_VISION));
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
