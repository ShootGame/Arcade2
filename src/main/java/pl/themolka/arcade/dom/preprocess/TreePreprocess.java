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

package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Parent;
import pl.themolka.arcade.dom.Property;

import java.util.List;
import java.util.Objects;

public class TreePreprocess extends NodePreprocess
                            implements TreePreprocessHandler {
    private final TreePreprocessHandler handler;

    public TreePreprocess(TreePreprocessHandler handler) {
        this.handler = Objects.requireNonNull(handler, "handler cannot be null");
    }

    @Override
    public final List<Node> define(Parent<Node> parent) {
        return parent.children();
    }

    @Override
    public final void invoke(Node node) throws PreprocessException {
        List<Node> nodeDefinition = this.defineNode(node);
        if (nodeDefinition != null) {
            for (Node definedChild : nodeDefinition) {
                this.invokeNode(definedChild);

                List<Property> propertyDefinition = this.defineProperty(definedChild);
                if (propertyDefinition != null) {
                    for (Property property : propertyDefinition) {
                        this.invokeProperty(property);
                    }
                }
            }
        }

        for (Node child : this.define(node)) {
            this.process(child);
        }
    }

    //
    // TreePreprocessHandler
    //


    @Override
    public List<Node> defineNode(Node parent) {
        return this.handler.defineNode(parent);
    }

    @Override
    public List<Property> defineProperty(Node parent) {
        return this.handler.defineProperty(parent);
    }

    @Override
    public void invokeNode(Node node) throws PreprocessException {
        this.handler.invokeNode(node);
    }

    @Override
    public void invokeProperty(Property property) throws PreprocessException {
        this.handler.invokeProperty(property);
    }

    public TreePreprocessHandler getHandler() {
        return this.handler;
    }
}
