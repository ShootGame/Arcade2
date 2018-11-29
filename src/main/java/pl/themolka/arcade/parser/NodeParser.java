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

package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.dom.Node;

/**
 * Simple and easy {@link Node} parsing.
 */
public abstract class NodeParser<T> extends AbstractParser<T> {
    public NodeParser() {
    }

    @Override
    protected Result<T> parse(Element element, String name, String value) throws ParserException {
        if (element instanceof Node) {
            return this.parseNode((Node) element, name, value);
        }

        throw this.fail(element, name, value, "Not a node");
    }

    protected Result<T> parseNode(Node node, String name, String value) throws ParserException {
        if (node.isPrimitive()) {
            return this.parsePrimitive(node, name, value);
        } else if (node.isTree()) {
            return this.parseTree(node, name);
        }

        // ^ None of these - try anyway

        if (value != null) {
            Result<T> primitive = this.parsePrimitive(node, name, value);
            if (primitive != null) {
                return primitive;
            }
        }

        Result<T> tree = this.parseTree(node, name);
        if (tree != null) {
            return tree;
        }

        throw this.fail(node, name, value, "Parser not implemented correctly");
    }

    /**
     * Primitive <b>or trimmed</b> {@link Node} type.
     * NOTE: {@code value} cannot be {@code null}.
     */
    protected Result<T> parsePrimitive(Node node, String name, String value) throws ParserException {
        return this.parseTree(node, name);
    }

    /**
     * Tree {@link Node} type.
     */
    protected Result<T> parseTree(Node node, String name) throws ParserException {
        throw this.fail(node, name, "Node is not primitive type");
    }
}
