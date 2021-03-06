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

package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.Context;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserLibrary;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

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
        public void install(ParserLibrary library) throws ParserNotSupportedException {
            this.modeParser = library.type(Boolean.class);
        }

        @Override
        protected Result<Mode> parseNode(Context context, Node node, String name, String value) throws ParserException {
            boolean defTake = !Config.DEFAULT_MODE.toBoolean();
            boolean take = this.modeParser.parse(context, node.property("take", "remove")).orDefault(defTake);

            return Result.fine(node, name, value, Mode.fromBoolean(!take));
        }
    }

    interface Config {
        Mode DEFAULT_MODE = Mode.getDefault();

        default Mode mode() { return DEFAULT_MODE; }
    }
}
