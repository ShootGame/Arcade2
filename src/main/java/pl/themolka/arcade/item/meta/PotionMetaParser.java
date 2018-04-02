package pl.themolka.arcade.item.meta;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(PotionMeta.class)
class PotionMetaParser extends ItemMetaParser.Nested<PotionMeta>
                       implements InstallableParser {
    private Parser<PotionType> typeParser;
    private Parser<Boolean> extendedParser;
    private Parser<Boolean> upgradedParser;
    private Parser<Color> colorParser;
    private Parser<PotionEffect> potionEffectParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.typeParser = context.type(PotionType.class);
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

                if (extended && !value.isUpgradeable()) {
                    throw this.fail(type, "Potion is not extendable");
                } else if (upgraded && !value.isUpgradeable()) {
                    throw this.fail(type, "Potion is not upgradeable");
                } else if (extended && upgraded) {
                    throw this.fail(type, "Potion cannot be both extended and upgraded");
                }

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
