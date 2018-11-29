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

package pl.themolka.arcade.attribute;

import java.util.Objects;

public abstract class AttributeKey {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AttributeKey && this.key().equals(((AttributeKey) obj).key());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key());
    }

    public abstract String key();

    @Override
    public String toString() {
        return this.key();
    }
}
