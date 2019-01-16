package pl.themolka.arcade.item.meta;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(KnowledgeBookMeta.class)
public class KnowledgeBookMetaParser extends ItemMetaParser.Nested<KnowledgeBookMeta>
                                     implements InstallableParser {
    private Parser<NamespacedKey> recipeParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.recipeParser = library.type(NamespacedKey.class);
    }

    @Override
    public KnowledgeBookMeta parse(Context context, Node root, ItemStack itemStack, KnowledgeBookMeta itemMeta) throws ParserException {
        Node node = root.firstChild("knowledge-book", "knowledgebook", "recipe-book", "recipebook", "recipes");
        if (node != null) {
            for (Node recipe : node.children("recipe", "discover")) {
                itemMeta.addRecipe(this.recipeParser.parse(context, recipe).orFail());
            }
        }

        return itemMeta;
    }
}
