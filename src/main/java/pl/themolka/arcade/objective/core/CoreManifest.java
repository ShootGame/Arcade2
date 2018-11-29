package pl.themolka.arcade.objective.core;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Material;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Property;
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
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.UnionRegion;

import java.util.HashSet;
import java.util.Set;

public class CoreManifest extends ObjectiveManifest {
    @Override
    public void onEnable(Game game, Set<Objective> objectives, Set<Object> listeners) {
    }

    //
    // Definitions
    //

    @Override
    public Set<String> defineCategory() {
        return ImmutableSet.of("leak", "destroy");
    }

    @Override
    public Set<String> defineObjective() {
        return ImmutableSet.of("cores", "core");
    }

    @Override
    public ObjectiveParser<? extends Objective.Config<?>> defineParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(CoreParser.class);
    }

    //
    // Parser
    //

    @NestedParserName("core")
    @Produces(Core.Config.class)
    public static class CoreParser extends ObjectiveParser<Core.Config>
                                   implements InstallableParser {
        private Parser<Integer> detectorLevelParser;
        private Parser<Liquid> liquidParser;
        private Parser<Material> materialParser;
        private Parser<String> materialTextParser;
        private Parser<UnionRegion.Config> regionParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.detectorLevelParser = context.type(Integer.class);
            this.liquidParser = context.type(Liquid.class);
            this.materialParser = context.type(Material.class);
            this.materialTextParser = context.text();
            this.regionParser = context.type(UnionRegion.Config.class);
        }

        @Override
        public String expectType() {
            return "core";
        }

        @Override
        protected Result<Core.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseRequiredId(node);
            int detectorLevel = this.detectorLevelParser.parse(node.property("detector-level", "detectorlevel", "detector")).orDefault(Core.Config.DEFAULT_DETECTOR_LEVEL);
            Liquid liquid = this.liquidParser.parse(node.property("liquid", "type", "or")).orDefault(Core.Config.DEFAULT_LIQUID);

            Set<Material> material = new HashSet<>();
            Property materialProperty = node.property("material", "type", "of");
            for (String maybeMaterial : ParserUtils.array(this.materialTextParser.parse(materialProperty).orFail())) {
                material.add(this.materialParser.parseWithValue(materialProperty, maybeMaterial).orFail());
            }

            if (ParserUtils.ensureNotEmpty(material)) {
                material = Core.Config.DEFAULT_MATERIAL;
            }
            Set<Material> finalMaterial = material;

            String coreName = this.parseName(node).orDefaultNull();
            boolean objective = this.parseObjective(node).orDefault(Core.Config.DEFAULT_IS_OBJECTIVE);
            Ref<Participator.Config<?>> owner = this.parseOwner(node).orDefault(Ref.empty());
            UnionRegion.Config region = this.regionParser.parse(node.firstChild("region")).orFail();

            return Result.fine(node, name, value, new Core.Config() {
                public String id() { return id; }
                public Ref<Integer> detectorLevel() { return Ref.ofProvided(detectorLevel); }
                public Ref<Liquid> liquid() { return Ref.ofProvided(liquid); }
                public Ref<Set<Material>> material() { return Ref.ofProvided(finalMaterial); }
                public Ref<String> name() { return Ref.ofProvided(coreName); }
                public Ref<Boolean> objective() { return Ref.ofProvided(objective); }
                public Ref<Participator.Config<?>> owner() { return owner; }
                public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
            });
        }
    }
}
