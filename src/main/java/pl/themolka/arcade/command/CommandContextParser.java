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

import pl.themolka.arcade.ArcadePlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContextParser implements CommandContext.IContextParser {
    public static final char FLAG_PREFIX = '-';
    public static final char FLAG_QUOTE = '\"';

    private Map<String, String> flags = new HashMap<>();
    private List<String> params = new ArrayList<>();

    @Override
    public CommandContext parse(ArcadePlugin plugin, Command command, String label, String[] args) {
        if (args == null) {
            args = new String[0];
        }

        String flag = null;
        StringBuilder flagValue = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (flag != null && flagValue == null && arg.startsWith(String.valueOf(FLAG_QUOTE))) {
                if (arg.endsWith(String.valueOf(FLAG_QUOTE))) {
                    this.flags.put(flag, arg.substring(1, arg.length() - 1));
                    flag = null;
                    flagValue = null;
                    continue;
                }

                flagValue = new StringBuilder().append(arg.substring(1));
                continue;
            }

            if (flagValue != null) {
                flagValue.append(" ");

                if (arg.endsWith(String.valueOf(FLAG_QUOTE))) {
                    flagValue.append(arg.substring(0, arg.length() - 1));

                    this.flags.put(flag, flagValue.toString());
                    flag = null;
                    flagValue = null;
                    continue;
                }

                flagValue.append(arg);
                continue;
            }

            if (arg.startsWith(String.valueOf(FLAG_PREFIX))) {
                flag = arg.substring(1);

                if (command.hasFlag(flag)) {
                    String value = null;
                    String[] split = flag.split("=", 2);
                    if (split.length > 1) {
                        value = split[1];
                    }

                    this.flags.put(flag, value);
                    continue;
                }
            }

            flag = null;
            this.params.add(arg);
        }

        return new CommandContext(plugin, args, command, this.flags, label, this.params);
    }

    public Map<String, String> getFlags() {
        return this.flags;
    }

    public List<String> getParams() {
        return this.params;
    }
}