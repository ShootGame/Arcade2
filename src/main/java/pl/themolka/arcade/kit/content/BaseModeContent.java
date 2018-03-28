package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

public interface BaseModeContent {
    enum Mode {
        GIVE(true),
        TAKE(false);

        private final boolean mode;

        Mode(boolean mode) {
            this.mode = mode;
        }

        public boolean toBoolean() {
            return this.mode;
        }

        @Override
        public String toString() {
            return Boolean.toString(this.mode);
        }

        public static Mode fromBoolean(boolean mode) {
            return mode ? GIVE : TAKE;
        }

        public static Mode getDefault() {
            return GIVE;
        }
    }

    boolean give();

    boolean take();

    @Produces(Mode.class)
    class ModeParser extends NodeParser<Mode>
                     implements InstallableParser {
        private Parser<Boolean> modeParser;

        @Override
        public Set<Object> expect() {
            return Collections.singleton("kit content mode");
        }

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            this.modeParser = context.type(Boolean.class);
        }

        @Override
        protected ParserResult<Mode> parseNode(Node node, String name, String value) throws ParserException {
            boolean defTake = !Config.DEFAULT_MODE.toBoolean();
            boolean take = this.modeParser.parse(node.property("take", "remove")).orDefault(defTake);

            return ParserResult.fine(node, name, value, Mode.fromBoolean(!take));
        }
    }

    interface Config {
        Mode DEFAULT_MODE = Mode.getDefault();

        default Mode mode() { return DEFAULT_MODE; }
    }
}
