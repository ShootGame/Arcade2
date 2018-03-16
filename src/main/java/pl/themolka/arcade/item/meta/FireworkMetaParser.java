package pl.themolka.arcade.item.meta;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Produces;

@Produces(FireworkMeta.class)
class FireworkMetaParser extends ItemMetaParser.Nested<FireworkMeta> {
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
                int powerInt = this.powerParser.parse(power).orFail();
                if (powerInt > 127) {
                    throw this.fail(power, "Power cannot be greater than 127");
                } else if (powerInt <= 0) {
                    throw this.fail(power, "Power cannot be smaller than 0");
                }

                itemMeta.setPower(this.powerParser.parse(power).orFail());
            }

            for (Node effect : node.children("effect", "firework-effect", "fireworkeffect")) {
                itemMeta.addEffect(this.fireworkEffectParser.parse(effect).orFail());
            }
        }

        return itemMeta;
    }
}
