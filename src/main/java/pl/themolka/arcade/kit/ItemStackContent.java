package pl.themolka.arcade.kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.xml.XMLMaterial;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackContent implements KitContent<ItemStack> {
    public static final int SLOT_NULL = -1;

    private final ItemStack result;
    private int slot;

    public ItemStackContent(ItemStack result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        PlayerInventory inventory = player.getPlayer().getBukkit().getInventory();

        if (this.hasSlot()) {
            inventory.setItem(this.getSlot(), this.getResult());
        } else {
            inventory.addItem(this.getResult());
        }
    }

    @Override
    public ItemStack getResult() {
        return this.result;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean hasSlot() {
        return this.slot != SLOT_NULL;
    }

    public void resetSlot() {
        this.setSlot(SLOT_NULL);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public static class Parser implements IKitContentParser<ItemStackContent> {
        @Override
        public KitContent<ItemStackContent> parse(Element xml) {
            ItemStackBuilder builder = new ItemStackBuilder();
            builder.amount(this.parseAmount(xml));
            builder.description(this.parseDescription(xml));
            builder.displayName(this.parseDisplayName(xml));
            builder.durability(this.parseDurability(xml));
            builder.enchantments(this.parseEnchantments(xml));
            builder.type(this.parseType(xml));
            builder.unbreakable(this.parseUnbreakable(xml));

            return null;
        }

        private int parseAmount(Element xml) {
            Attribute attribute = xml.getAttribute("amount");
            if (attribute != null) {
                try {
                    return attribute.getIntValue();
                } catch (DataConversionException ignored) {
                }
            }

            return 1;
        }

        private List<String> parseDescription(Element xml) {
            List<String> description = new ArrayList<>();

            Element element = xml.getChild("description");
            if (element != null) {
                for (Element line : element.getChildren("line")) {
                    description.add(XMLParser.parseMessage(line.getTextNormalize()));
                }
            }

            return description;
        }

        private String parseDisplayName(Element xml) {
            Element element = xml.getChild("name");
            if (element != null) {
                return XMLParser.parseMessage(element.getTextNormalize());
            }

            return null;
        }

        private short parseDurability(Element xml) {
            Element element = xml.getChild("durability");
            if (element != null) {
                try {
                    return Short.parseShort(element.getTextNormalize());
                } catch (NumberFormatException ignored) {
                }
            }

            return 0;
        }

        private Map<Enchantment, Integer> parseEnchantments(Element xml) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();

            Element element = xml.getChild("enchantments");
            if (element != null) {
                for (Element enchantment : element.getChildren("enchantment")) {
                    Enchantment type = Enchantment.getByName(XMLParser.parseEnumValue(enchantment.getTextNormalize()));
                    int level = 1;

                    try {
                        Attribute levelAttribute = enchantment.getAttribute("level");
                        if (levelAttribute != null) {
                            level = levelAttribute.getIntValue();
                        }
                    } catch (DataConversionException ignored) {
                    }

                    enchantments.put(type, level);
                }
            }

            return enchantments;
        }

        private Material parseType(Element xml) {
            return XMLMaterial.parse(xml);
        }

        private boolean parseUnbreakable(Element xml) {
            Element element = xml.getChild("unbreakable");
            return element != null && XMLParser.parseBoolean(element.getTextNormalize());
        }
    }
}
