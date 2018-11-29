package pl.themolka.arcade.objective;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.objective.core.CoreManifest;
import pl.themolka.arcade.objective.flag.FlagManifest;
import pl.themolka.arcade.objective.point.PointManifest;
import pl.themolka.arcade.objective.wool.WoolManifest;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

public abstract class ObjectiveManifest {
    protected static final Set<ObjectiveManifest> manifests = ImmutableSet.<ObjectiveManifest>builder()
            .add(new CoreManifest())
            .add(new FlagManifest())
            .add(new PointManifest())
            .add(new WoolManifest())
            .build();

    public abstract void onEnable(Game game, Set<Objective> objectives, Set<Object> listeners);

    //
    // Definitions
    //

    public Node define(Node source) {
        Node objective = source.firstChild("objectives", "objective");
        if (objective != null) {
            return objective;
        }

        objective = source.firstChild(this.defineCategory());
        if (objective != null) {
            return objective;
        }

        return source.firstChild(this.defineObjective());
    }

    public abstract Set<String> defineCategory();

    public abstract Set<String> defineObjective();

    public abstract ObjectiveParser<? extends Objective.Config<?>> defineParser(
            ParserContext context) throws ParserNotSupportedException;

    //
    // Parser
    //

    public abstract static class ObjectiveParser<T extends Objective.Config<?>> extends ConfigParser<T>
                                                                                implements InstallableParser  {
        private Parser<String> nameParser;
        private Parser<Boolean> objectiveParser;
        private Parser<Ref> ownerParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.nameParser = context.type(String.class);
            this.objectiveParser = context.type(Boolean.class);
            this.ownerParser = context.type(Ref.class);
        }

        @Override
        public Set<Object> expect() {
            return Collections.singleton(this.expectType() + " objective");
        }

        public abstract String expectType();

        //
        // Shared Parsing
        //

        protected Result<String> parseName(Node node) throws ParserException {
            return this.nameParser.parse(node.property("name", "title"));
        }

        protected Result<Boolean> parseObjective(Node node) throws ParserException {
            return this.objectiveParser.parse(node.property("objective"));
        }

        protected Result<Ref> parseOwner(Node node) throws ParserException {
            return this.ownerParser.parse(node.property("owner"));
        }
    }
}
