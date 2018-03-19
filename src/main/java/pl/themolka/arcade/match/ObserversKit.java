package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.kit.content.BaseModeContent;
import pl.themolka.arcade.kit.content.ClearInventoryContent;
import pl.themolka.arcade.kit.content.EffectContent;
import pl.themolka.arcade.kit.content.FlyContent;
import pl.themolka.arcade.kit.content.FlySpeedContent;
import pl.themolka.arcade.kit.content.GameModeContent;
import pl.themolka.arcade.kit.content.HeldSlotContent;
import pl.themolka.arcade.kit.content.ItemStackContent;
import pl.themolka.arcade.kit.content.WalkSpeedContent;
import pl.themolka.arcade.potion.PotionEffectBuilder;

public class ObserversKit extends Kit {
    public static final String OBSERVERS_KIT_ID = "_observers-kit";

    //
    // Content
    //

    public static final ClearInventoryContent CLEAR_INVENTORY = new ClearInventoryContent(true);

    public static final GameModeContent GAME_MODE = new GameModeContent(GameMode.CREATIVE);

    public static final ItemStackContent NAVIGATION_ITEM = new ItemStackContent(new ItemStackBuilder()
            .type(Material.COMPASS)
            .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Navigation")
            .description(ChatColor.YELLOW + "Left click to teleport to highest point",
                    ChatColor.YELLOW + "Right click to teleport to point")
            .build(), BaseModeContent.Mode.GIVE).setSlot(0);

    public static final ItemStackContent PLAY_ITEM = new ItemStackContent(new ItemStackBuilder()
            .type(Material.NETHER_STAR)
            .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Join the game")
            .description(ChatColor.YELLOW + "Right click to join the game")
            .build(), BaseModeContent.Mode.GIVE).setSlot(2);

    public static final HeldSlotContent HELD_SLOT = new HeldSlotContent(0);

    public static final EffectContent NIGHT_VISION_EFFECT = new EffectContent(new PotionEffectBuilder()
            .type(PotionEffectType.NIGHT_VISION)
            .duration(PotionEffectBuilder.DURATION_INFINITY)
            .amplifier(0)
            .particles(false)
            .build(), BaseModeContent.Mode.GIVE);
    public static final FlyContent FLY = new FlyContent(true);

    public static final FlySpeedContent FLY_SPEED = new FlySpeedContent(FlySpeedContent.DEFAULT_SPEED * 1.35F);

    public static final WalkSpeedContent WALK_SPEED = new WalkSpeedContent(WalkSpeedContent.DEFAULT_SPEED * 1.85F);

    //
    // Object
    //

    public ObserversKit(ArcadePlugin plugin) {
        super(plugin, OBSERVERS_KIT_ID);

        this.buildKit();
    }

    public void buildKit() {
        this.addContent(CLEAR_INVENTORY,
                        GAME_MODE,
                        NAVIGATION_ITEM, // slot 0
                        PLAY_ITEM, // slot 1
                        HELD_SLOT,
                        NIGHT_VISION_EFFECT,
                        FLY,
                        FLY_SPEED,
                        WALK_SPEED);
    }
}
