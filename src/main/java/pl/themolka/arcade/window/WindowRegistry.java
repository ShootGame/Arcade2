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

package pl.themolka.arcade.window;

import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WindowRegistry {
    private final Map<Inventory, Window> windows = new HashMap<>();

    public void addWindow(Window window) {
        this.windows.put(window.getContainer(), window);
    }

    public Window getWindow(Inventory view) {
        return this.windows.get(view);
    }

    public Collection<Window> getWindows() {
        return this.windows.values();
    }

    public boolean hasView(Inventory view) {
        return this.windows.containsKey(view);
    }

    public void removeAll() {
        this.windows.clear();
    }

    public void removeWindow(Inventory view) {
        this.windows.remove(view);
    }

    public void removeWindow(Window window) {
        this.removeWindow(window.getContainer());
    }
}
