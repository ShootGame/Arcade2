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

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.dom.Element;

import java.util.Set;

public class ParserValidation {
    /**
     * Too many items in {@link #expect()} would make it unreadable.
     */
    public static final int EXPECT_ARRAY_LIMIT = 25;

    public Set<Object> expect() {
        return null;
    }

    public Set<Object> expectCompact() {
        return this.expect();
    }

    public int expectCompactWaypoint() {
        return EXPECT_ARRAY_LIMIT;
    }

    protected ParserException fail(Element element, String fail) {
        return this.fail(element, element.getName(), element.getValue(), fail);
    }

    protected ParserException fail(Element element, Throwable cause) {
        return this.fail(element, element.getName(), element.getValue(), cause);
    }

    protected ParserException fail(Element element, String fail, Throwable cause) {
        return this.fail(element, element.getName(), element.getValue(), fail, cause);
    }

    protected ParserException fail(Element element, String name, String value) {
        return this.fail(element, name, value, null, null);
    }

    protected ParserException fail(Element element, String name, String value, String fail) {
        return this.fail(element, name, value, fail, null);
    }

    protected ParserException fail(Element element, String name, String value, Throwable cause) {
        return this.fail(element, name, value, null, cause);
    }

    protected ParserException fail(Element element, String name, String value, String fail, Throwable cause) {
        Set<Object> expect = this.expect();
        if (expect != null && expect.size() > this.expectCompactWaypoint()) {
            expect = this.expectCompact();
        }

        String expected = expect != null && !expect.isEmpty() ? " (" + StringUtils.join(expect, "/") + " expected)" : "";

        String invalidString;
        if (value != null) {
            invalidString = "Invalid value \"" + value + "\"";
        } else {
            invalidString = "Missing value";
        }

        String whereString = name != null ? " in \"" + name + "\"" : "";

        return new ParserException(element, invalidString + whereString +
                (fail != null ? ": " + fail : "") + expected, cause);
    }
}
