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

package pl.themolka.arcade.util.versioning;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.parser.ElementParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(SemanticVersion.class)
public class SemanticVersionParser extends ElementParser<SemanticVersion>
                                   implements InstallableParser {
    private static final Set<String> preWhitelist = ImmutableSet.of("pre", "alpha", "beta", "snapshot", "stage");

    private Parser<Integer> majorParser;
    private Parser<Integer> minorParser;
    private Parser<Integer> patchParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.majorParser = context.type(Integer.class);
        this.minorParser = context.type(Integer.class);
        this.patchParser = context.type(Integer.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("semantic version (read " + SemanticVersion.MANUAL + ")");
    }

    @Override
    protected Result<SemanticVersion> parseElement(Element element, String name, String value) throws ParserException {
        String[] sections = value.split("-", 2);

        String[] parts = sections[0].split("\\.", 3);
        if (parts.length < 2) {
            throw this.fail(element, name, value, "Semantic version does not contain minor (1.X)");
        }

        int major = this.majorParser.parseWithDefinition(element, name, parts[0]).orFail();
        if (major < 0) {
            throw this.fail(element, name, value, "Major (X.0.0) cannot be negative");
        }

        int minor = this.minorParser.parseWithDefinition(element, name, parts[1]).orFail();
        if (minor < 0) {
            throw this.fail(element, name, value, "Minor (1.X.0) cannot be negative");
        }

        String patchValue = parts.length > 2 ? parts[2] : "";
        int patch = this.patchParser.parseWithDefinition(element, name, patchValue).orDefault(SemanticVersion.DEFAULT.getPatch());
        if (patch < 0) {
            throw this.fail(element, name, value, "Patch (1.0.X) cannot be negative");
        }

        boolean pre = false;
        if (sections.length > 1) {
            pre = this.isPre(sections[1].toLowerCase());
        }

        return Result.fine(element, name, value, new SemanticVersion(major, minor, patch, pre));
    }

    private boolean isPre(String input) {
        for (String whitelisted : preWhitelist) {
            if (input.startsWith(whitelisted)) {
                return true;
            }
        }

        return false;
    }
}
