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

public class DOMException extends Exception {
    private final Content content;
    private final Selection selection;

    //
    // Content
    //

    public DOMException(Content content) {
        this(content, (String) null);
    }

    public DOMException(Content content, String message) {
        this(content, message, null);
    }

    public DOMException(Content content, String message, Throwable cause) {
        super(message, cause);
        this.content = content;
        this.selection = null;
    }

    public DOMException(Content content, Throwable cause) {
        this(content, null, cause);
    }

    //
    // Selection
    //

    public DOMException(Selection selection) {
        this(selection, (String) null);
    }

    public DOMException(Selection selection, String message) {
        this(selection, message, null);
    }

    public DOMException(Selection selection, String message, Throwable cause) {
        super(message, cause);
        this.content = null;
        this.selection = selection;
    }

    public DOMException(Selection selection, Throwable cause) {
        this(selection, null, cause);
    }

    public Content getContent() {
        return this.content;
    }

    public Selection getSelection() {
        if (this.selection != null) {
            return this.selection;
        } else if (this.content != null) {
            Content content = this.content;
            if (content.isSelectable()) {
                return content.select();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        String message = this.getMessage();
        if (message != null) {
            builder.append(message);
        } else {
            builder.append("Unknown error");
        }

        Selection selection = this.getSelection();
        if (selection != null) {
            builder.append(" at line " ).append(selection);
        }

        if (this.content != null) {
            String near = this.content.toShortString();
            if (near != null) {
                builder.append(" in '").append(near).append("'");
            }
        }

        return builder.append(".").toString();
    }
}
