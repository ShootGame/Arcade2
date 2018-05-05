package pl.themolka.arcade.objective.wool;

import com.google.common.collect.ImmutableSet;
import org.bukkit.DyeColor;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.Objective;
import pl.themolka.arcade.objective.ObjectiveManifest;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.UnionRegion;

import java.util.Set;

public class WoolManifest extends ObjectiveManifest {
    @Override
    public void onEnable(Game game, Set<Objective> objectives, Set<Object> listeners) {
        for (Objective objective : objectives) {
            if (objective instanceof Wool) {
                listeners.add(new WoolChestTracker(game));
                break;
            }
        }
    }

    //
    // Definitions
    //

    @Override
    public Set<String> defineCategory() {
        return ImmutableSet.of("capture");
    }

    @Override
    public Set<String> defineObjective() {
        return ImmutableSet.of("wools", "wool");
    }

    @Override
    public ObjectiveParser<? extends Objective.Config> defineParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(WoolParser.class);
    }

    //
    // Parser
    //

    @NestedParserName("wool")
    @Produces(Wool.Config.class)
    public static class WoolParser extends ObjectiveParser<Wool.Config>
                                   implements InstallableParser {
        private Parser<DyeColor> colorParser;
        private Parser<Boolean> craftableParser;
        private Parser<UnionRegion.Config> monumentParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.colorParser = context.type(DyeColor.class);
            this.craftableParser = context.type(Boolean.class);
            this.monumentParser = context.type(UnionRegion.Config.class);
        }

        @Override
        public String expectType() {
            return "wool";
        }

        @Override
        protected ParserResult<Wool.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseRequiredId(node);
            DyeColor color = this.colorParser.parse(node.property("color", "of")).orFail();
            boolean craftable = this.craftableParser.parse(node.property("craftable")).orDefault(Wool.Config.DEFAULT_IS_CRAFTABLE);
            UnionRegion.Config monument = this.monumentParser.parse(node.firstChild("monument")).orFail();
            String woolName = this.parseName(node).orDefaultNull();
            boolean objective = this.parseObjective(node).orDefault(Wool.Config.DEFAULT_IS_OBJECTIVE);
            Ref<Participator.Config<?>> owner = this.parseOwner(node).orFail();

            return ParserResult.fine(node, name, value, new Wool.Config() {
                public String id() { return id; }
                public DyeColor color() { return color; }
                public boolean craftable() { return craftable; }
                public Ref<AbstractRegion.Config<?>> monument() { return Ref.ofProvided(monument); }
                public String name() { return woolName; }
                public boolean objective() { return objective; }
                public Ref<Participator.Config<?>> owner() { return owner; }
            });
        }
    }
}
