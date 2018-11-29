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

package pl.themolka.arcade.dom.engine;

import org.jdom2.Namespace;
import org.jdom2.located.LocatedElement;
import pl.themolka.arcade.dom.Cursor;

/**
 * Because JDOM doesn't attach start and end points by default...
 */
public class JDOMElement extends LocatedElement {
    private Cursor startCursor;
    private Cursor endCursor;

    public JDOMElement(String name, Namespace namespace) {
        super(name, namespace);
    }

    public JDOMElement(String name) {
        super(name);
    }

    public JDOMElement(String name, String uri) {
        super(name, uri);
    }

    public JDOMElement(String name, String prefix, String uri) {
        super(name, prefix, uri);
    }

    @Override
    public int getLine() {
        return this.endCursor != null ? this.endCursor.getLine() : -1;
    }

    @Override
    public int getColumn() {
        return this.endCursor != null ? this.endCursor.getColumn() : -1;
    }

    @Override
    public void setLine(int line) {
    }

    @Override
    public void setColumn(int col) {
    }

    public Cursor getStartCursor() {
        return this.startCursor;
    }

    public Cursor getEndCursor() {
        return this.endCursor;
    }

    public void setStartCursor(Cursor startCursor) {
        this.startCursor = startCursor;
    }

    public void setEndCursor(Cursor endCursor) {
        this.endCursor = endCursor;
    }
}
