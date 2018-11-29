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

    public DOMException(Content content) {
        this.content = content;
    }

    public DOMException(Content content, String message) {
        super(message);
        this.content = content;
    }

    public DOMException(Content content, String message, Throwable cause) {
        super(message, cause);
        this.content = content;
    }

    public DOMException(Content content, Throwable cause) {
        super(cause);
        this.content = content;
    }

    public Content getContent() {
        return this.content;
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

        if (this.content != null) {
            if (this.content.isSelectable()) {
                Selection selection = this.content.select();
                if (selection != null) {
                    builder.append(" at line ").append(selection);
                }
            }

            String near = this.content.toShortString();
            if (near != null) {
                builder.append(" in '").append(near).append("'");
            }
        }

        return builder.append(".").toString();
    }
}
