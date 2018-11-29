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

package pl.themolka.arcade.map.queue;

import pl.themolka.arcade.map.OfflineMap;

import java.util.Collection;
import java.util.LinkedList;

public class MapQueue extends LinkedList<OfflineMap> {
    public boolean addMap(OfflineMap map) {
        return this.offerLast(map);
    }

    public OfflineMap getNextMap() {
        return this.peek();
    }

    public boolean hasNextMap() {
        return !this.isEmpty();
    }

    public OfflineMap takeNextMap() {
        return this.poll();
    }

    public void replace(Collection<? extends OfflineMap> queue) {
        this.clear();
        this.addAll(queue);
    }

    public OfflineMap setNextMap(OfflineMap map) {
        if (this.hasNextMap()) {
            return this.set(0, map); // replace
        }

        this.offerFirst(map);
        return null;
    }
}
