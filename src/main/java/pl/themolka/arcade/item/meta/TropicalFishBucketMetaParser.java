package pl.themolka.arcade.item.meta;

import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(TropicalFishBucketMeta.class)
public class TropicalFishBucketMetaParser extends ItemMetaParser.Nested<TropicalFishBucketMeta>
                                          implements InstallableParser {
    private Parser<DyeColor> bodyColorParser;
    private Parser<TropicalFish.Pattern> patternParser;
    private Parser<DyeColor> patternColorParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.bodyColorParser = library.type(DyeColor.class);
        this.patternParser = library.type(TropicalFish.Pattern.class);
        this.patternColorParser = library.type(DyeColor.class);
    }

    @Override
    public TropicalFishBucketMeta parse(Context context, Node root, ItemStack itemStack, TropicalFishBucketMeta itemMeta) throws ParserException {
        Node node = root.firstChild("tropical-fish-bucket", "tropical-fish", "tropicalfishbucket", "tropicalfish");
        if (node != null) {
            DyeColor bodyColor = this.bodyColorParser.parse(context, node.firstChild("body-color", "bodycolor", "body", "color")).orDefaultNull();
            if (bodyColor != null) {
                itemMeta.setBodyColor(bodyColor);
            }

            TropicalFish.Pattern pattern = this.patternParser.parse(context, node.firstChild("pattern", "variant")).orDefaultNull();
            if (pattern != null) {
                itemMeta.setPattern(pattern);
            }

            DyeColor patternColor = this.patternColorParser.parse(context, node.firstChild(
                    "pattern-color", "variant-color", "patterncolor", "variantcolor")).orDefaultNull();
            if (patternColor != null) {
                itemMeta.setPatternColor(patternColor);
            }
        }

        return itemMeta;
    }
}
