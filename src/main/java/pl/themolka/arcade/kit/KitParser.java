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

package pl.themolka.arcade.kit;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.kit.content.KitContent;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Produces(Kit.Config.class)
public class KitParser extends ConfigParser<Kit.Config>
                       implements InstallableParser {
    private Parser<KitContent.Config> contentParser;

    @Override
    public Set<Object> expect() {
        return Collections.singleton("kit");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.contentParser = context.type(KitContent.Config.class);
    }

    @Override
    protected Result<Kit.Config> parseTree(Node node, String name) throws ParserException {
        String id = this.parseRequiredId(node);

        List<KitContent.Config<?, ?>> contents = new ArrayList<>();
        for (Node contentNode : node.children()) {
            contents.add(this.contentParser.parse(contentNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(contents)) {
            throw this.fail(node, name, null, "No kit contents defined");
        }

        Set<String> inherit = new LinkedHashSet<>();
        String inheritValue = node.propertyValue("inherit", "parent", "parents");
        if (inheritValue != null) {
            inherit.addAll(ParserUtils.array(inheritValue));
        }

        return Result.fine(node, name, new Kit.Config() {
            public String id() { return id; }
            public Ref<List<KitContent.Config<?, ?>>> contents() { return Ref.ofProvided(contents); }
            public Ref<Set<String>> inherit() { return Ref.ofProvided(inherit); }
        });
    }
}
