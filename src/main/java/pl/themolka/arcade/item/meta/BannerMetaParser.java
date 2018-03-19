package pl.themolka.arcade.item.meta;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(BannerMeta.class)
class BannerMetaParser extends ItemMetaParser.Nested<BannerMeta> {
    private Parser<DyeColor> baseColorParser;
    private Parser<Pattern> patternParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
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
