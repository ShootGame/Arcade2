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

package pl.themolka.arcade.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RefContainer implements Iterable<Ref<?>> {
    private final Map<String, Ref<?>> byId = new HashMap<>();

    @Override
    public Iterator<Ref<?>> iterator() {
        return new ArrayList<>(this.byId.values()).iterator();
    }

    public int clear() {
        int size = this.byId.size();
        this.byId.clear();
        return size;
    }

    public boolean contains(String id) {
        return this.byId.containsKey(this.validateId(id));
    }

    public boolean contains(Ref<?> ref) {
        return this.contains(this.validateRef(ref).getId());
    }

    public boolean register(Ref<?> ref) {
        if (ref.isEmpty()) {
            return false;
        }

        return this.byId.putIfAbsent(this.validateRef(ref).getId(), ref) == null;
    }


    public List<Ref<?>> registerMany(Iterable<Ref<?>> refs) {
        List<Ref<?>> failed = new ArrayList<>();
        for (Ref<?> ref : refs) {
            if (!ref.isEmpty() && ref.isProvided()) {
                if (this.contains(ref.getId())) {
                    failed.add(ref);
                } else {
                    this.register(ref);
                }
            }
        }

        return failed;
    }

    public void registerMany(Ref<?>... refs) {
        this.registerMany(Arrays.asList(refs));
    }

    public boolean unregister(String id) {
        return this.byId.remove(this.validateId(id)) != null;
    }

    public boolean unregister(Ref<?> ref) {
        return this.unregister(this.validateRef(ref).getId());
    }

    public Ref<?> query(String id) {
        return this.query(id, null);
    }

    public Ref<?> query(String id, Ref<?> def) {
        return this.byId.getOrDefault(this.validateId(id), def);
    }

    //
    // Validating Inputs
    //

    private String validateId(String id) {
        return Objects.requireNonNull(id, "id cannot be null");
    }

    private <T> Ref<T> validateRef(Ref<T> ref) {
        return Objects.requireNonNull(ref, "ref cannot be null");
    }
}
