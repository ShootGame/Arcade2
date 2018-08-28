package pl.themolka.arcade.objective.flag;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.Objective;
import pl.themolka.arcade.objective.ObjectiveManifest;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.HashSet;
import java.util.Set;

public class FlagManifest extends ObjectiveManifest {
    @Override
    public void onEnable(Game game, Set<Objective> objectives, Set<Object> listeners) {
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
        return ImmutableSet.of("flags", "flag");
    }

    @Override
    public ObjectiveParser<? extends Objective.Config<?>> defineParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(FlagParser.class);
    }

    //
    // Parser
    //

    @Produces(Flag.Config.class)
    public static class FlagParser extends ObjectiveParser<Flag.Config>
                                   implements InstallableParser {
        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
        }

        @Override
        public String expectType() {
            return "flag";
        }

        @Override
        protected ParserResult<Flag.Config> parseNode(Node node, String name, String value) throws ParserException {
            String id = this.parseRequiredId(node);

            Set<Capture.Config> captures = new HashSet<>();

            String flagName = this.parseName(node).orDefaultNull();
            boolean objective = this.parseObjective(node).orDefault(Flag.Config.DEFAULT_IS_OBJECTIVE);
            Ref<Participator.Config<?>> owner = this.parseOwner(node).orDefault(Ref.empty());

            return ParserResult.fine(node, name, value, new Flag.Config() {
                public String id() { return id; }
                public Ref<Set<Capture.Config>> captures() { return Ref.ofProvided(captures); }
                public Ref<String> name() { return Ref.ofProvided(flagName); }
                public Ref<Boolean> objective() { return Ref.ofProvided(objective); }
                public Ref<Participator.Config<?>> owner() { return owner; }
            });
        }
    }
}
