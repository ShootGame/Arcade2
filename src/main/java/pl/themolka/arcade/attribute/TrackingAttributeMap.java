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

import net.minecraft.server.AttributeMapBase;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class TrackingAttributeMap extends AttributeMap {
    private final Set<AttributeKey> tracking = new LinkedHashSet<>();

    public TrackingAttributeMap(AttributeMapBase mojang) {
        super(mojang);
    }

    @Override
    public Attribute getAttribute(AttributeKey key) {
        Attribute attribute = super.getAttribute(key);
        if (attribute != null) {
            this.subscribe(key);
        }

        return attribute;
    }

    public Set<AttributeKey> getTracking() {
        return new LinkedHashSet<>(this.tracking);
    }

    public boolean isSubcribed(AttributeKey key) {
        return this.tracking.contains(requireKey(key));
    }

    public void subscribe(AttributeKey key) {
        this.tracking.add(requireKey(key));
    }

    public void unsubscribe(AttributeKey key) {
        this.tracking.remove(requireKey(key));
    }

    public void unsubscribeAll() {
        this.tracking.clear();
    }

    private static AttributeKey requireKey(AttributeKey key) {
        return Objects.requireNonNull(key, "key cannot be null");
    }
}
