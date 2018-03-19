package pl.themolka.arcade.item.meta;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;

@Produces(SpawnEggMeta.class)
class SpawnEggMetaParser extends ItemMetaParser.Nested<SpawnEggMeta> {
    private Parser<EntityType> spawnedTypeParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
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
