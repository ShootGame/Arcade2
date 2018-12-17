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

package pl.themolka.arcade.parser.type;

import org.bukkit.util.Vector;
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

@Produces(Vector.class)
public class VectorParser extends NodeParser<Vector>
                          implements InstallableParser {
    private Parser<Double> xParser;
    private Parser<Double> yParser;
    private Parser<Double> zParser;

    @Override
    public void install(ParserLibrary library) throws ParserNotSupportedException {
        this.xParser = library.type(Double.class);
        this.yParser = library.type(Double.class);
        this.zParser = library.type(Double.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("a vector");
    }

    @Override
    protected Result<Vector> parseNode(Context context, Node node, String name, String value) throws ParserException {
        double x = this.xParser.parse(context, node.property("x")).orFail();
        double y = this.yParser.parse(context, node.property("y")).orFail();
        double z = this.zParser.parse(context, node.property("z")).orFail();
        return Result.fine(node, name, value, new Vector(x, y, z));
    }
}
