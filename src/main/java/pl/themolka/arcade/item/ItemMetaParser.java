package pl.themolka.arcade.item;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// No registration - no @Produces production.
class ItemMetaParser implements InstallableParser {
    private static final ImmutableSet<Class<? extends Nested>> types = ImmutableSet.<Class<? extends Nested>>builder()
            .add(BannerMetaParser.class)
            .add(BookMetaParser.class)
            .add(EnchantmentStorageMetaParser.class)
            .add(FireworkMetaParser.class)
            .add(FireworkEffectMetaParser.class)
            .add(LeatherArmorMetaParser.class)
            .add(MapMetaParser.class)
            .add(PotionMetaParser.class)
            .add(SkullMetaParser.class)
            .add(SpawnEggMetaParser.class)
            .build();

    private Map<Class<? extends ItemMeta>, Nested<?>> parsers;

    @Override
    public void install(ParserContext context) {
        this.parsers = new HashMap<>();
        for (Class<? extends Nested> clazz : types) {
            Produces produces = clazz.getDeclaredAnnotation(Produces.class);
            if (produces == null) {
                continue;
            }

            Class<?> metaType = produces.value();
            if (metaType == null || !ItemMeta.class.isAssignableFrom(metaType)) {
                continue;
            }

            try {
                this.parsers.put((Class<? extends ItemMeta>) metaType, clazz.newInstance());
            } catch (ReflectiveOperationException ignored) {
            }
        }

        for (Nested<?> parser : this.parsers.values()) {
            parser.install(context);
        }
    }

    ItemMeta parse(Node node, ItemStack itemStack, ItemMeta itemMeta) throws ParserException {
        Nested parser = this.parsers.get(itemMeta.getClass());
        if (parser != null) {
            itemMeta = parser.parse(node, itemStack, itemMeta);
        }

        return itemMeta;
    }

    /**
     * Base interface for all {@link ItemMeta} parsers.
     */
    interface Nested<T extends ItemMeta> extends InstallableParser {
        T parse(Node root, ItemStack itemStack, T itemMeta) throws ParserException;
    }
}

//
// BannerMeta
//

@Produces(BannerMeta.class)
class BannerMetaParser implements ItemMetaParser.Nested<BannerMeta> {
    private Parser<DyeColor> baseColorParser;
    private Parser<Pattern> patternParser;

    @Override
    public void install(ParserContext context) {
        this.baseColorParser = context.enumType(DyeColor.class);
        this.patternParser = context.type(Pattern.class);
    }

    @Override
    public BannerMeta parse(Node root, ItemStack itemStack, BannerMeta itemMeta) throws ParserException {
        Node node = root.child("banner", "flag");
        if (node != null) {
            Property baseColor = node.property("base-color", "basecolor", "color");
            if (baseColor != null) {
                itemMeta.setBaseColor(this.baseColorParser.parse(baseColor).orFail());
            }

            for (Node pattern : node.children("pattern")) {
                itemMeta.addPattern(this.patternParser.parse(pattern).orFail());
            }
        }

        return itemMeta;
    }
}

//
// BookMeta
//

@Produces(BookMeta.class)
class BookMetaParser implements ItemMetaParser.Nested<BookMeta> {
    private Parser<String> authorParser;
    private Parser<BookMeta.Generation> generationParser;
    private Parser<String> titleParser;
    private Parser<String> pageParser;

    @Override
    public void install(ParserContext context) {
        this.authorParser = context.type(String.class);
        this.generationParser = context.enumType(BookMeta.Generation.class);
        this.titleParser = context.type(String.class);
        this.pageParser = context.type(String.class);
    }

    @Override
    public BookMeta parse(Node root, ItemStack itemStack, BookMeta itemMeta) throws ParserException {
        Node node = root.firstChild("book");
        if (node != null) {
            Property author = root.property("author", "signed", "signed-as");
            if (author != null) {
                itemMeta.setAuthor(this.authorParser.parse(author).orFail());
            }

            Property generation = root.property("generation");
            if (generation != null) {
                itemMeta.setGeneration(this.generationParser.parse(generation).orFail());
            }

            Property title = root.property("title", "name");
            if (title != null) {
                itemMeta.setTitle(this.titleParser.parse(title).orFail());
            }

            List<String> pages = new ArrayList<>();
            for (Node page : node.children("page")) {
                pages.add(this.pageParser.parse(page).orFail());
            }
            itemMeta.setPages(pages);
        }

        return itemMeta;
    }
}

//
// EnchantmentStorageMeta
//

@Produces(EnchantmentStorageMeta.class)
class EnchantmentStorageMetaParser implements ItemMetaParser.Nested<EnchantmentStorageMeta> {
    private Parser<ItemEnchantment> enchantmentParser;

    @Override
    public void install(ParserContext context) {
        this.enchantmentParser = context.type(ItemEnchantment.class);
    }

    @Override
    public EnchantmentStorageMeta parse(Node root, ItemStack itemStack, EnchantmentStorageMeta itemMeta) throws ParserException {
        Node node = root.firstChild("enchanted-book");
        if (node != null) {
            for (Node enchantment : node.children("enchantment")) {
                ItemEnchantment value = this.enchantmentParser.parse(enchantment).orFail();
                itemMeta.addStoredEnchant(value.getType(), value.getLevel(), true);
            }
        }

        return itemMeta;
    }
}

//
// FireworkMeta
//

@Produces(FireworkMeta.class)
class FireworkMetaParser implements ItemMetaParser.Nested<FireworkMeta> {
    private Parser<Integer> powerParser;
    private Parser<FireworkEffect> fireworkEffectParser;

    @Override
    public void install(ParserContext context) {
        this.powerParser = context.type(Integer.class);
        this.fireworkEffectParser = context.type(FireworkEffect.class);
    }

    @Override
    public FireworkMeta parse(Node root, ItemStack itemStack, FireworkMeta itemMeta) throws ParserException {
        Node node = root.firstChild("firework", "firework");
        if (node != null) {
            Property power = node.property("power");
            if (power != null) {
                itemMeta.setPower(this.powerParser.parse(power).orFail());
            }

            for (Node effect : node.children("effect", "firework-effect", "fireworkeffect")) {
                itemMeta.addEffect(this.fireworkEffectParser.parse(effect).orFail());
            }
        }

        return itemMeta;
    }
}

//
// FireworkEffectMeta
//

@Produces(FireworkEffectMeta.class)
class FireworkEffectMetaParser implements ItemMetaParser.Nested<FireworkEffectMeta> {
    private Parser<FireworkEffect> fireworkEffectParser;

    @Override
    public void install(ParserContext context) {
        this.fireworkEffectParser = context.type(FireworkEffect.class);
    }

    @Override
    public FireworkEffectMeta parse(Node root, ItemStack itemStack, FireworkEffectMeta itemMeta) throws ParserException {
        Node node = root.firstChild("firework-effect", "fireworkeffect");
        if (node != null) {
            itemMeta.setEffect(this.fireworkEffectParser.parse(node).orFail());
        }

        return itemMeta;
    }
}

//
// LeatherArmorMeta
//

@Produces(LeatherArmorMeta.class)
class LeatherArmorMetaParser implements ItemMetaParser.Nested<LeatherArmorMeta> {
    private Parser<Color> colorParser;

    @Override
    public void install(ParserContext context) {
        this.colorParser = context.type(Color.class);
    }

    @Override
    public LeatherArmorMeta parse(Node root, ItemStack itemStack, LeatherArmorMeta itemMeta) throws ParserException {
        Node node = root.firstChild("leather-armor", "leather", "armor");
        if (node != null) {
            Property color = root.property("color");
            if (color != null) {
                itemMeta.setColor(this.colorParser.parse(color).orFail());
            }
        }

        return itemMeta;
    }
}

//
// MapMeta
//

@Produces(MapMeta.class)
class MapMetaParser implements ItemMetaParser.Nested<MapMeta> {
    private Parser<Boolean> scalingParser;
    private Parser<String> locationNameParser;
    private Parser<Color> colorParser;

    @Override
    public void install(ParserContext context) {
        this.scalingParser = context.type(Boolean.class);
        this.locationNameParser = context.type(String.class);
        this.colorParser = context.type(Color.class);
    }

    @Override
    public MapMeta parse(Node root, ItemStack itemStack, MapMeta itemMeta) throws ParserException {
        Node node = root.firstChild("map");
        if (node != null) {
            Property scaling = node.property("scaling");
            if (scaling != null) {
                itemMeta.setScaling(this.scalingParser.parse(scaling).orFail());
            }

            Property locationName = node.property("location-name");
            if (locationName != null) {
                itemMeta.setLocationName(this.locationNameParser.parse(locationName).orFail());
            }

            Property color = node.property("color");
            if (color != null) {
                itemMeta.setColor(this.colorParser.parse(color).orFail());
            }
        }

        return itemMeta;
    }
}

//
// PotionMeta
//

@Produces(PotionMeta.class)
class PotionMetaParser implements ItemMetaParser.Nested<PotionMeta> {
    private Parser<PotionType> typeParser;
    private Parser<Boolean> extendedParser;
    private Parser<Boolean> upgradedParser;
    private Parser<Color> colorParser;
    private Parser<PotionEffect> potionEffectParser;

    @Override
    public void install(ParserContext context) {
        this.typeParser = context.enumType(PotionType.class);
        this.extendedParser = context.type(Boolean.class);
        this.upgradedParser = context.type(Boolean.class);
        this.colorParser = context.type(Color.class);
        this.potionEffectParser = context.type(PotionEffect.class);
    }

    @Override
    public PotionMeta parse(Node root, ItemStack itemStack, PotionMeta itemMeta) throws ParserException {
        Node node = root.firstChild("potion");
        if (node != null) {
            Property type = node.property("type", "of");
            if (type != null) {
                PotionType value = this.typeParser.parse(type).orFail();
                boolean extended = this.extendedParser.parse(node.property("extended")).orDefault(false);
                boolean upgraded = this.upgradedParser.parse(node.property("upgraded")).orDefault(false);
                itemMeta.setBasePotionData(new PotionData(value, extended, upgraded));
            }

            Property color = node.property("color");
            if (color != null) {
                itemMeta.setColor(this.colorParser.parse(color).orFail());
            }

            for (Node effect : node.children("effect", "potion-effect", "potioneffect")) {
                PotionEffect value = this.potionEffectParser.parse(effect).orFail();
                if (!itemMeta.hasCustomEffect(value.getType())) {
                    itemMeta.addCustomEffect(value, false);
                }
            }
        }

        return itemMeta;
    }
}

//
// SkullMeta
//

@Produces(SkullMeta.class)
class SkullMetaParser implements ItemMetaParser.Nested<SkullMeta> {
    private Parser<String> ownerParser;

    @Override
    public void install(ParserContext context) {
        this.ownerParser = context.text();
    }

    @Override
    public SkullMeta parse(Node root, ItemStack itemStack, SkullMeta itemMeta) throws ParserException {
        Node node = root.firstChild("skull", "head");
        if (node != null) {
            Property owner = node.property("owner", "of");
            if (owner != null) {
                itemMeta.setOwner(this.ownerParser.parse(owner).orFail());
            }
        }

        return itemMeta;
    }
}

//
// SpawnEggMeta
//

@Produces(SpawnEggMeta.class)
class SpawnEggMetaParser implements ItemMetaParser.Nested<SpawnEggMeta> {
    private Parser<EntityType> spawnedTypeParser;

    @Override
    public void install(ParserContext context) {
        this.spawnedTypeParser = context.type(EntityType.class);
    }

    public SpawnEggMeta parse(Node root, ItemStack itemStack, SpawnEggMeta itemMeta) throws ParserException {
        Node node = root.firstChild("spawner-egg");
        if (node != null) {
            Property spawnedType = node.property("spawned-type", "entity-type", "mob-type");
            if (spawnedType != null) {
                itemMeta.setSpawnedType(this.spawnedTypeParser.parse(spawnedType).orFail());
            }
        }

        return itemMeta;
    }
}
