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

package pl.themolka.arcade.dom;

/**
 * Representation of a comment in a DOM document.
 */
public class Comment implements Locatable, NestedContent<Content> {
    private String text;

    private Selection location;
    private Content parent;

    @Override
    public boolean detach() {
        return this.setParent(null) != null;
    }

    @Override
    public Content getParent() {
        return this.parent;
    }

    @Override
    public boolean hasParent() {
        return this.parent != null;
    }

    @Override
    public boolean isSelectable() {
        return this.location != null || (this.parent != null && this.parent.isSelectable());
    }

    @Override
    public void locate(Selection selection) {
        this.location = selection;
    }

    @Override
    public Selection select() {
        if (this.location != null) {
            return this.location;
        } else if (this.hasParent()) {
            Selectable parent = this.getParent();
            if (parent.isSelectable()) {
                return parent.select();
            }
        }

        return null;
    }

    @Override
    public Content setParent(Content parent) {
        Content oldParent = this.parent;
        this.parent = parent;

        return oldParent;
    }

    public String getText() {
        return this.text;
    }

    public String setText(String text) {
        String oldText = this.text;
        this.text = text;

        return oldText;
    }

    @Override
    public String toShortString() {
        return "#" + (this.text != null ? " " + this.text : "");
    }
}
