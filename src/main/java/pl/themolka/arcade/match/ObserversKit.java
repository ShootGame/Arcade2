package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.kit.content.BaseModeContent;
import pl.themolka.arcade.kit.content.CanFlyContent;
import pl.themolka.arcade.kit.content.ClearInventoryContent;
import pl.themolka.arcade.kit.content.EffectContent;
import pl.themolka.arcade.kit.content.FlyContent;
import pl.themolka.arcade.kit.content.FlySpeedContent;
import pl.themolka.arcade.kit.content.GameModeContent;
import pl.themolka.arcade.kit.content.HeldSlotContent;
import pl.themolka.arcade.kit.content.ItemStackContent;
import pl.themolka.arcade.kit.content.ResetContent;
import pl.themolka.arcade.kit.content.WalkSpeedContent;
import pl.themolka.arcade.potion.PotionEffectBuilder;

public class ObserversKit extends Kit {
    public static final String OBSERVERS_KIT_ID = "_observers-kit";

    //
    // Content
    //

    public static final ResetContent RESET = new ResetContent.Config() {
    }.create(null);

    // clear inventory
    public static final ClearInventoryContent CLEAR_INVENTORY = new ClearInventoryContent.Config() {
        public Ref<Boolean> result() { return Ref.ofProvided(true); }
    }.create(null);

    // game mode
    public static final GameModeContent GAME_MODE = new GameModeContent.Config() {
        public Ref<GameMode> result() { return Ref.ofProvided(GameMode.CREATIVE); }
    }.create(null);

    // navigation item
    public static final ItemStackContent NAVIGATION_ITEM = new ItemStackContent.Config() {
        public Ref<ItemStack> result() { return Ref.ofProvided(new ItemStackBuilder()
                .type(Material.COMPASS)
                .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Navigation")
                .description(ChatColor.YELLOW + "Left click to teleport to highest point",
                             ChatColor.YELLOW + "Right click to teleport to point")
                .build()); }
        public BaseModeContent.Mode mode() { return BaseModeContent.Mode.GIVE; }
        public Integer slot() { return 0; }
    }.create(null);

    // play item
    public static final ItemStackContent PLAY_ITEM = new ItemStackContent.Config() {
        public Ref<ItemStack> result() { return Ref.ofProvided(new ItemStackBuilder()
                .type(Material.NETHER_STAR)
                .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Join the game")
                .description(ChatColor.YELLOW + "Right click to join the game")
                .build()); }
        public BaseModeContent.Mode mode() { return BaseModeContent.Mode.GIVE; }
        public Integer slot() { return 2; }
    }.create(null);

    // held slot
    public static final HeldSlotContent HELD_SLOT = new HeldSlotContent.Config() {
        public Ref<Integer> result() { return Ref.ofProvided(0); }
    }.create(null);

    // night vision effect
    public static final EffectContent NIGHT_VISION_EFFECT = new EffectContent.Config() {
        public Ref<PotionEffect> result() { return Ref.ofProvided(new PotionEffectBuilder()
                .type(PotionEffectType.NIGHT_VISION)
                .duration(PotionEffectBuilder.DURATION_INFINITY)
                .amplifier(0)
                .particles(false)
                .build()); }
        public BaseModeContent.Mode mode() { return BaseModeContent.Mode.GIVE; }
    }.create(null);

    // can fly
    public static final CanFlyContent CAN_FLY = new CanFlyContent.Config() {
        public Ref<Boolean> result() { return Ref.ofProvided(true); }
        public boolean force() { return true; }
    }.create(null);

    // fly
    public static final FlyContent FLY = new FlyContent.Config() {
        public Ref<Boolean> result() { return Ref.ofProvided(true); }
    }.create(null);

    // fly speed
    public static final FlySpeedContent FLY_SPEED = new FlySpeedContent.Config() {
        public Ref<Float> result() { return Ref.ofProvided(FlySpeedContent.Config.DEFAULT_SPEED * 1.35F); }
    }.create(null);

    // walk speed
    public static final WalkSpeedContent WALK_SPEED = new WalkSpeedContent.Config() {
        public Ref<Float> result() { return Ref.ofProvided(WalkSpeedContent.Config.DEFAULT_SPEED * 1.85F); }
    }.create(null);

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
                        CAN_FLY,
                        FLY,
                        FLY_SPEED,
                        WALK_SPEED);
    }
}
