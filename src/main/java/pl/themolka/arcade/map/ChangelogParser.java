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

package pl.themolka.arcade.map;

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
import pl.themolka.arcade.util.versioning.SemanticVersion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Produces(Changelog.class)
public class ChangelogParser extends NodeParser<Changelog>
                             implements InstallableParser {
    private Parser<SemanticVersion> versionParser;
    private Parser<LocalDate> releaseParser;
    private Parser<String> logParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.versionParser = library.type(SemanticVersion.class);
        this.releaseParser = library.type(LocalDate.class);
        this.logParser = library.text();
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a changelog");
    }

    @Override
    protected Result<Changelog> parseTree(Context context, Node node, String name) throws ParserException {
        SemanticVersion version = this.versionParser.parse(context, node.property("version")).orFail();
        LocalDate release = this.releaseParser.parse(context, node.property("release", "date")).orDefaultNull();

        Changelog<SemanticVersion> result = new Changelog<>(version, release);
        result.addAll(this.parseLogs(context, node));
        return Result.fine(node, name, result);
    }

    private List<String> parseLogs(Context context, Node node) throws ParserException {
        List<String> logs = new ArrayList<>();
        for (Node log : node.children()) {
            String text = this.logParser.parse(context, log).orDefaultNull();

            if (text != null) {
                logs.add(text);
            }
        }

        return logs;
    }
}
