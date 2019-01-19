/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.UnionRegion;
import pl.themolka.arcade.util.material.Fluid;

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
    public ObjectiveParser<? extends Objective.Config<?>> defineParser(ParserLibrary library) throws ParserNotSupportedException {
        return library.of(CoreParser.class);
    }

    //
    // Parser
    //

    @NestedParserName("core")
    @Produces(Core.Config.class)
    public static class CoreParser extends ObjectiveParser<Core.Config>
                                   implements InstallableParser {
        private Parser<Integer> detectorLevelParser;
        private Parser<Fluid> fluidParser;
        private Parser<Material> materialParser;
        private Parser<String> materialTextParser;
        private Parser<UnionRegion.Config> regionParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.detectorLevelParser = library.type(Integer.class);
            this.fluidParser = library.type(Fluid.class);
            this.materialParser = library.type(Material.class);
            this.materialTextParser = library.text();
            this.regionParser = library.type(UnionRegion.Config.class);
        }

        @Override
        public String expectType() {
            return "core";
        }

        @Override
        protected Result<Core.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseRequiredId(context, node);
            int detectorLevel = this.detectorLevelParser.parse(context, node.property("detector-level", "detectorlevel", "detector")).orDefault(Core.Config.DEFAULT_DETECTOR_LEVEL);
            Fluid fluid = this.fluidParser.parse(context, node.property("fluid", "liquid", "type", "or")).orDefault(Core.Config.DEFAULT_FLUID);

            Set<Material> material = new HashSet<>();
            Property materialProperty = node.property("material", "type", "of");
            if (materialProperty != null) {
                for (String maybeMaterial : ParserUtils.array(this.materialTextParser.parse(context, materialProperty).orFail())) {
                    material.add(this.materialParser.parseWithValue(context, materialProperty, maybeMaterial).orFail());
                }
            }

            if (ParserUtils.ensureNotEmpty(material)) {
                material = Core.Config.DEFAULT_MATERIAL;
            }
            Set<Material> finalMaterial = material;

            String coreName = this.parseName(context, node).orDefaultNull();
            boolean objective = this.parseObjective(context, node).orDefault(Core.Config.DEFAULT_IS_OBJECTIVE);
            Ref<Participator.Config<?>> owner = this.parseOwner(context, node).orDefault(Ref.empty());
            UnionRegion.Config region = this.regionParser.parse(context, node.firstChild("region")).orFail();

            return Result.fine(node, name, value, new Core.Config() {
                public String id() { return id; }
                public Ref<Integer> detectorLevel() { return Ref.ofProvided(detectorLevel); }
                public Ref<Fluid> fluid() { return Ref.ofProvided(fluid); }
                public Ref<Set<Material>> material() { return Ref.ofProvided(finalMaterial); }
                public Ref<String> name() { return Ref.ofProvided(coreName); }
                public Ref<Boolean> objective() { return Ref.ofProvided(objective); }
                public Ref<Participator.Config<?>> owner() { return owner; }
                public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
            });
        }
    }
}
