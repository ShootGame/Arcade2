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

package pl.themolka.arcade.game;

import org.bukkit.inventory.Inventory;
import pl.themolka.arcade.window.Window;
import pl.themolka.arcade.window.WindowRegistry;

public class GameWindowRegistry extends WindowRegistry {
    private final Game game;

    public GameWindowRegistry(Game game) {
        this.game = game;
    }

    @Override
    public void addWindow(Window window) {
        super.addWindow(window);
        this.getParent().addWindow(window);
    }

    @Override
    public void removeAll() {
        for (Window window : this.getWindows()) {
            this.getParent().removeWindow(window);
        }

        super.removeAll();
    }

    @Override
    public void removeWindow(Inventory view) {
        this.getParent().removeWindow(view);
        super.removeWindow(view);
    }

    private WindowRegistry getParent() {
        return this.game.getPlugin().getWindowRegistry();
    }
}
