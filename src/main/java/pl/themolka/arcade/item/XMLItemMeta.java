package pl.themolka.arcade.item;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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
import org.bukkit.potion.PotionEffect;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.potion.XMLPotionEffect;
import pl.themolka.arcade.xml.XMLColor;
import pl.themolka.arcade.xml.XMLDyeColor;
import pl.themolka.arcade.xml.XMLEntity;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated {@link ItemMetaParser}
 */
@Deprecated
public class XMLItemMeta extends XMLParser {
    public static ItemMeta parse(Element xml, ItemMeta source) {
        if (source instanceof BannerMeta) {
            return parseBanner(xml, (BannerMeta) source);
        } else if (source instanceof BookMeta) {
            return parseBook(xml, (BookMeta) source);
        } else if (source instanceof EnchantmentStorageMeta) {
            return parseEnchantmentStorage(xml, (EnchantmentStorageMeta) source);
        } else if (source instanceof FireworkMeta) {
            return parseFirework(xml, (FireworkMeta) source);
        } else if (source instanceof FireworkEffectMeta) {
            return parseFireworkEffect(xml, (FireworkEffectMeta) source);
        } else if (source instanceof LeatherArmorMeta) {
            return parseLeatherArmor(xml, (LeatherArmorMeta) source);
        } else if (source instanceof MapMeta) {
            return parseMap(xml, (MapMeta) source);
        } else if (source instanceof PotionMeta) {
            return parsePotion(xml, (PotionMeta) source);
        } else if (source instanceof SkullMeta) {
            return parseSkull(xml, (SkullMeta) source);
        } else if (source instanceof SpawnEggMeta) {
            return parseSpawnEgg(xml, (SpawnEggMeta) source);
        }

        return source;
    }

    public static BannerMeta parseBanner(Element xml, BannerMeta source) {
        Element baseColor = xml.getChild("base-color");
        if (baseColor != null) {
            DyeColor dyeColor = XMLDyeColor.parse(baseColor.getValue());

            if (dyeColor != null) {
                source.setBaseColor(dyeColor);
            }
        }

        List<Pattern> patterns = new ArrayList<>();
        for (Element patternElement : xml.getChildren("pattern")) {
            Attribute color = patternElement.getAttribute("color");
            if (color == null) {
                continue;
            }

            DyeColor dyeColor = XMLDyeColor.parse(color.getValue());
            if (dyeColor == null) {
                continue;
            }

            try {
                String id = XMLParser.parseEnumValue(patternElement.getValue());
                patterns.add(new Pattern(dyeColor, PatternType.valueOf(id)));
            } catch (IllegalArgumentException ignored) {
            }
        }

        source.setPatterns(patterns);

        return source;
    }

    public static BookMeta parseBook(Element xml, BookMeta source) {
        Element book = xml.getChild("book");
        if (book != null) {
            Attribute author = book.getAttribute("author");
            if (author != null) {
                source.setAuthor(parseMessage(author.getValue()));
            }

            Attribute title = book.getAttribute("title");
            if (title != null) {
                source.setTitle(parseMessage(title.getValue()));
            }

            for (Element page : book.getChildren("page")) {
                source.addPage(parseMessage(page.getValue()));
            }
        }

        return source;
    }

    public static EnchantmentStorageMeta parseEnchantmentStorage(Element xml, EnchantmentStorageMeta source) {
        Element book = xml.getChild("enchanted-book");
        if (book != null) {
            for (Element enchantment : book.getChildren("enchantment")) {
                Enchantment type = Enchantment.getByName(XMLParser.parseEnumValue(enchantment.getValue()));
                int level = 1;

                try {
                    Attribute levelAttribute = enchantment.getAttribute("level");
                    if (levelAttribute != null) {
                        level = levelAttribute.getIntValue();
                    }
                } catch (DataConversionException ignored) {
                }

                source.addStoredEnchant(type, level, false);
            }
        }

        return source;
    }

    public static FireworkMeta parseFirework(Element xml, FireworkMeta source) {
        return source;
    }

    public static FireworkEffectMeta parseFireworkEffect(Element xml, FireworkEffectMeta source) {
        return source;
    }

    public static LeatherArmorMeta parseLeatherArmor(Element xml, LeatherArmorMeta source) {
        Element leatherColor = xml.getChild("leather-color");
        if (leatherColor != null) {
            Color color = XMLColor.parse(leatherColor.getValue());
            if (color != null) {
                source.setColor(color);
            }
        }

        return source;
    }

    public static MapMeta parseMap(Element xml, MapMeta source) {
        return source;
    }

    public static PotionMeta parsePotion(Element xml, PotionMeta source) {
        for (Element effectElement : xml.getChildren("potion-effect")) {
            PotionEffect effect = XMLPotionEffect.parse(effectElement);
            if (effect != null) {
                source.addCustomEffect(effect, false);
            }
        }

        return source;
    }

    public static SkullMeta parseSkull(Element xml, SkullMeta source) {
        Element skull = xml.getChild("skull");
        if (skull != null) {
            source.setOwner(skull.getValue());
        }

        return source;
    }

    public static SpawnEggMeta parseSpawnEgg(Element xml, SpawnEggMeta source) {
        Element spawnerEgg = xml.getChild("spawner-egg");
        if (spawnerEgg != null) {
            EntityType entity = XMLEntity.parse(spawnerEgg.getValue());
            if (entity != null) {
                source.setSpawnedType(entity);
            }
        }

        return source;
    }
}
