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

package pl.themolka.arcade.command;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.EmptyElement;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.type.BooleanParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandContext {
    private static final Parser<Boolean> booleanParser = new BooleanParser();

    private final String[] args;
    private final Command command;
    private final Map<String, String> flags;
    private final String label;
    private final List<String> params;

    public CommandContext(String[] args, Command command, Map<String, String> flags, String label, List<String> params) {
        this.args = args;
        this.command = command;
        this.flags = flags;
        this.label = label;
        this.params = params;
    }

    public String[] getArgs() {
        return this.args;
    }

    public Command getCommand() {
        return this.command;
    }

    public int getCompleterCursor() {
        return this.args.length - 1;
    }

    // Flags - Strings
    public String getFlag(String flag) {
        return this.getFlag(flag, null);
    }

    public String getFlag(String flag, String def) {
        if (this.flags.containsKey(flag)) {
            return this.flags.get(flag);
        } else {
            return def;
        }
    }

    // Flags = Booleans
    public boolean getFlagBoolean(String flag) {
        return this.getFlagBoolean(flag, false);
    }

    public boolean getFlagBoolean(String flag, boolean def) {
        return this.parseBoolean(this.getFlag(flag, String.valueOf(def)), def);
    }

    // Flags - Doubles
    public double getFlagDouble(String flag) {
        return this.getFlagDouble(flag, 0.0);
    }

    public double getFlagDouble(String flag, double def) {
        return Double.parseDouble(this.getFlag(flag, String.valueOf(def)));
    }

    // Flags - Integers
    public int getFlagInt(String flag) {
        return this.getFlagInt(flag, 0);
    }

    public int getFlagInt(String flag, int def) {
        return Integer.parseInt(this.getFlag(flag, String.valueOf(def)));
    }

    // Flags
    public Set<String> getFlags() {
        return this.flags.keySet();
    }

    // Label
    public String getLabel() {
        return this.label;
    }

    // Params - Strings
    public String getParam(int index) {
        return this.getParam(index, null);
    }

    public String getParam(int index, String def) {
        if (this.params.size() <= index) {
            return def;
        }

        return this.params.get(index);
    }

    public String getParams(int from) {
        return this.getParams(from, this.params.size());
    }

    public String getParams(int from, int to) {
        if (this.params.size() <= from) {
            return null;
        } else if (this.params.size() < to) {
            to = this.params.size();
        }

        return StringUtils.join(this.params.subList(from, to), " ");
    }

    // Params = Booleans
    public boolean getParamBoolean(int index) {
        return this.getParamBoolean(index, false);
    }

    public boolean getParamBoolean(int index, boolean def) {
        return this.parseBoolean(this.getParam(index, String.valueOf(def)), def);
    }

    // Params - Doubles
    public double getParamDouble(int index) {
        return this.getParamDouble(index, 0.0);
    }

    public double getParamDouble(int index, double def) {
        return Double.parseDouble(this.getParam(index, String.valueOf(def)));
    }

    // Params - Integers
    public int getParamInt(int index) {
        return this.getParamInt(index, 0);
    }

    public int getParamInt(int index, int def) {
        return Integer.parseInt(this.getParam(index, String.valueOf(def)));
    }

    // Params
    public int getParamsLength() {
        return this.params.size();
    }

    // Flags
    public boolean hasFlag(String flag) {
        return this.flags.containsKey(flag);
    }

    public boolean hasFlagValue(String flag) {
        return this.getFlag(flag) != null;
    }

    protected boolean parseBoolean(String bool, boolean def) {
        try {
            return booleanParser.parseWithValue(EmptyElement.empty(), bool).orDefault(def);
        } catch (ParserException e) {
            return def;
        }
    }

    public static CommandContext parse(Command command, String label, String[] args) {
        return parse(command, label, args, null);
    }

    public static CommandContext parse(Command command, String label, String[] args, IContextParser parser) {
        if (parser == null) {
            parser = new CommandContextParser();
        }

        return parser.parse(command, label, args);
    }

    public interface IContextParser {
        CommandContext parse(Command command, String label, String[] args);
    }
}