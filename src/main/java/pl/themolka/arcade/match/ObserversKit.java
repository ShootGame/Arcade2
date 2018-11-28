package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
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
import pl.themolka.arcade.kit.content.KitContent;
import pl.themolka.arcade.kit.content.ResetContent;
import pl.themolka.arcade.kit.content.WalkSpeedContent;
import pl.themolka.arcade.potion.PotionEffectBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ObserversKit extends Kit {
    public static final String OBSERVERS_KIT_ID = "_observers-kit";

    //
    // Content
    //

    // reset
    public static final ResetContent RESET = create(new ResetContent.Config() {});

    // clear inventory
    public static final ClearInventoryContent CLEAR_INVENTORY = create(new ClearInventoryContent.Config() {
        public Ref<Boolean> result() { return Ref.ofProvided(true); }
    });

    // game mode
    public static final GameModeContent GAME_MODE = create(new GameModeContent.Config() {
        public Ref<GameMode> result() { return Ref.ofProvided(GameMode.CREATIVE); }
    });

    // navigation item
    public static final ItemStackContent NAVIGATION_ITEM = create(new ItemStackContent.Config() {
        public Ref<ItemStack> result() { return Ref.ofProvided(new ItemStackBuilder()
                .type(Material.COMPASS)
                .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Navigation")
                .description(ChatColor.YELLOW + "Left click to teleport to highest point",
                             ChatColor.YELLOW + "Right click to teleport to point")
                .build()); }
        public BaseModeContent.Mode mode() { return BaseModeContent.Mode.GIVE; }
        public Ref<Integer> slot() { return Ref.ofProvided(0); }
    });

    // play item
    public static final ItemStackContent PLAY_ITEM = create(new ItemStackContent.Config() {
        public Ref<ItemStack> result() { return Ref.ofProvided(new ItemStackBuilder()
                .type(Material.NETHER_STAR)
                .displayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Join the game")
                .description(ChatColor.YELLOW + "Right click to join the game")
                .build()); }
        public BaseModeContent.Mode mode() { return BaseModeContent.Mode.GIVE; }
        public Ref<Integer> slot() { return Ref.ofProvided(2); }
    });

    // held slot
    public static final HeldSlotContent HELD_SLOT = create(new HeldSlotContent.Config() {
        public Ref<Integer> result() { return Ref.ofProvided(0); }
    });

    // night vision effect
    public static final EffectContent NIGHT_VISION_EFFECT = create(new EffectContent.Config() {
        public Ref<PotionEffect> result() { return Ref.ofProvided(new PotionEffectBuilder()
                .type(PotionEffectType.NIGHT_VISION)
                .duration(PotionEffectBuilder.DURATION_INFINITY)
                .amplifier(0)
                .particles(false)
                .build()); }
        public BaseModeContent.Mode mode() { return BaseModeContent.Mode.GIVE; }
    });

    // can fly
    public static final CanFlyContent CAN_FLY = create(new CanFlyContent.Config() {
        public Ref<Boolean> result() { return Ref.ofProvided(true); }
        public Ref<Boolean> force() { return Ref.ofProvided(true); }
    });

    // fly
    public static final FlyContent FLY = create(new FlyContent.Config() {
        public Ref<Boolean> result() { return Ref.ofProvided(true); }
    });

    // fly speed
    public static final FlySpeedContent FLY_SPEED = create(new FlySpeedContent.Config() {
        public Ref<Float> result() { return Ref.ofProvided(FlySpeedContent.Config.DEFAULT_SPEED * 1.15F); }
    });

    // walk speed
    public static final WalkSpeedContent WALK_SPEED = create(new WalkSpeedContent.Config() {
        public Ref<Float> result() { return Ref.ofProvided(WalkSpeedContent.Config.DEFAULT_SPEED * 1.65F); }
    });

    //
    // Object
    //

    protected ObserversKit(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        this.buildKit();
    }

    public void buildKit() {
        this.addContent(CLEAR_INVENTORY,
                        GAME_MODE,
                        NAVIGATION_ITEM, // slot 0
                        PLAY_ITEM, // slot 2
                        HELD_SLOT,
                        NIGHT_VISION_EFFECT,
                        CAN_FLY,
                        FLY,
                        FLY_SPEED,
                        WALK_SPEED);
    }

    private static <T extends KitContent<?>> T create(KitContent.Config<?, ?> t) {
        return (T) t.create(null, IGameConfig.Library.EMPTY);
    }

    public interface Config extends Kit.Config {
        default String id() { return OBSERVERS_KIT_ID; }
        default Ref<List<KitContent.Config<?, ?>>> contents() { return Ref.ofProvided(Collections.emptyList()); }
        default Ref<Set<String>> inherit() { return Ref.ofProvided(Collections.emptySet()); }

        @Override
        default ObserversKit create(Game game, Library library) {
            return new ObserversKit(game, library, this);
        }
    }
}
