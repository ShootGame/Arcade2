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

import net.minecraft.server.v1_13_R2.AttributeInstance;
import net.minecraft.server.v1_13_R2.AttributeMapBase;

import java.util.Objects;

public class AttributeMap implements Attributable {
    private final AttributeMapBase mojang;

    public AttributeMap(AttributeMapBase mojang) {
        this.mojang = Objects.requireNonNull(mojang, "mojang cannot be null");
    }

    @Override
    public Attribute getAttribute(AttributeKey key) {
        Objects.requireNonNull(key, "key cannot be null");
        AttributeInstance nms = this.mojang.a(key.key());
        return nms != null ? new Attribute(nms, key) : null;
    }
}
