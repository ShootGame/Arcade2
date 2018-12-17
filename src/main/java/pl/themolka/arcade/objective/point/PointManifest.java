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

package pl.themolka.arcade.objective.point;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dominator.Dominator;
import pl.themolka.arcade.filter.Filter;
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
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.UnionRegion;
import pl.themolka.arcade.task.Task;
import pl.themolka.arcade.task.TaskManager;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.Nulls;

import java.util.LinkedHashSet;
import java.util.Set;

public class PointManifest extends ObjectiveManifest {
    @Override
    public void onEnable(Game game, Set<Objective> objectives, Set<Object> listeners) {
        for (Objective objective : objectives) {
            if (objective instanceof Point) {
                listeners.add(new PointBossBarRender());
                listeners.add(new PointRegionRender(objectives));

                game.addSyncTask(new TickTask(game.getPlugin().getTasks(), objectives));
                break;
            }
        }
    }

    private class TickTask extends Task {
        final Set<Point> points;

        public TickTask(TaskManager tasks, Set<Objective> objectives) {
            super(tasks);

            this.points = new LinkedHashSet<>(objectives.size());
            for (Objective objective : objectives) {
                if (objective instanceof Point) {
                    this.points.add((Point) objective);
                }
            }
        }

        @Override
        public void onTick(long ticks) {
            for (Point point : this.points) {
                if (ticks % point.getTickInterval().toTicks() == 0) {
                    point.onTick(ticks);
                }
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
        return ImmutableSet.of("points", "point");
    }

    @Override
    public ObjectiveParser<? extends Objective.Config<?>> defineParser(ParserLibrary library) throws ParserNotSupportedException {
        return library.of(PointParser.class);
    }

    //
    // Parser
    //

    @NestedParserName("point")
    @Produces(Point.Config.class)
    public static class PointParser extends ObjectiveParser<Point.Config>
                                    implements InstallableParser {
        private Parser<Capture.Config> captureParser;
        private Parser<Time> captureTimeParser;
        private Parser<Ref> dominateFilterParser;
        private Parser<Dominator> dominatorStrategyParser;
        private Parser<Time> loseTimeParser;
        private Parser<Color> neutralColorParser;
        private Parser<Boolean> permanentParser;
        private Parser<Double> pointRewardParser;
        private Parser<UnionRegion.Config> stateRegionParser;

        @Override
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            super.install(library);
            this.captureParser = library.type(Capture.Config.class);
            this.captureTimeParser = library.type(Time.class);
            this.dominateFilterParser = library.type(Ref.class);
            this.dominatorStrategyParser = library.type(Dominator.class);
            this.loseTimeParser = library.type(Time.class);
            this.neutralColorParser = library.type(Color.class);
            this.permanentParser = library.type(Boolean.class);
            this.pointRewardParser = library.type(Double.class);
            this.stateRegionParser = library.type(UnionRegion.Config.class);
        }

        @Override
        public String expectType() {
            return "point";
        }

        @Override
        protected Result<Point.Config> parseNode(Context context, Node node, String name, String value) throws ParserException {
            String id = this.parseRequiredId(context, node);
            Capture.Config capture = this.captureParser.parse(context, node.firstChild("capture")).orFail();
            Time captureTime = this.captureTimeParser.parse(context, node.property("capture-time", "capturetime")).orDefault(Point.Config.DEFAULT_CAPTURE_TIME);
            Ref<Filter.Config<?>> dominateFilter = this.dominateFilterParser.parse(context, node.property("filter")).orDefault(Ref.empty());
            Dominator<Participator> dominatorStrategy = this.dominatorStrategyParser.parse(context, node.firstChild(
                    "dominator-strategy", "dominatorstrategy", "dominator")).orDefault(Point.Config.DEFAULT_DOMINATOR_STRATEGY);
            Time loseTime = this.loseTimeParser.parse(context, node.property("lose-time", "losetime")).orDefault(Point.Config.DEFAULT_LOSE_TIME);
            String pointName = this.parseName(context, node).orDefaultNull();
            Color neutralColor = this.neutralColorParser.parse(context, node.firstChild("color")).orDefault(Point.Config.DEFAULT_NEUTRAL_COLOR);
            boolean objective = this.parseObjective(context, node).orDefault(Point.Config.DEFAULT_IS_OBJECTIVE);
            Ref<Participator.Config<?>> owner = this.parseOwner(context, node).orDefault(Ref.empty());
            boolean permanent = this.permanentParser.parse(context, node.property("permanent")).orDefault(Point.Config.DEFAULT_IS_PERMANENT);
            double pointReward = this.pointRewardParser.parse(context, node.property("point-reward", "pointreward")).orDefault(Point.Config.DEFAULT_POINT_REWARD);
            AbstractRegion.Config<?> stateRegion = Nulls.defaults(this.stateRegionParser.parse(context, node.firstChild("state")).orDefaultNull(),
                    capture.region().get());

            return Result.fine(node, name, value, new Point.Config() {
                public String id() { return id; }
                public Ref<Capture.Config> capture() { return Ref.ofProvided(capture); }
                public Ref<Time> captureTime() { return Ref.ofProvided(captureTime); }
                public Ref<Filter.Config<?>> dominateFilter() { return dominateFilter; }
                public Ref<Dominator<Participator>> dominatorStrategy() { return Ref.ofProvided(dominatorStrategy); }
                public Ref<Time> loseTime() { return Ref.ofProvided(loseTime); }
                public Ref<String> name() { return Ref.ofProvided(pointName); }
                public Ref<Color> neutralColor() { return Ref.ofProvided(neutralColor); }
                public Ref<Boolean> objective() { return Ref.ofProvided(objective); }
                public Ref<Participator.Config<?>> owner() { return owner; }
                public Ref<Boolean> permanent() { return Ref.ofProvided(permanent); }
                public Ref<Double> pointReward() { return Ref.ofProvided(pointReward); }
                public Ref<AbstractRegion.Config<?>> stateRegion() { return Ref.ofProvided(stateRegion); }
            });
        }
    }
}
