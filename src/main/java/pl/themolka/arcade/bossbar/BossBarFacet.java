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

package pl.themolka.arcade.bossbar;

import org.bukkit.entity.Player;
import pl.themolka.arcade.game.GamePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Rendering {@link BossBar}s by the given priority.
 */
public class BossBarFacet {
    private final GamePlayer player;
    private final List<BossBarView> views = new ArrayList<>();

    public BossBarFacet(GamePlayer player) {
        this.player = player;
    }

    public boolean addBossBar(BossBar bossBar) {
        return this.addBossBar(bossBar, BarPriority.undefined());
    }

    public boolean addBossBar(BossBar bossBar, int priority) {
        BossBarView view = this.getViewFor(Objects.requireNonNull(bossBar, "bossBar cannot be null"));
        if (view != null) {
            return this.setPriority(view, priority);
        }

        this.views.add(view = new BossBarView(bossBar));
        view.setPriority(priority);

        this.render();
        return true;
    }

    public GamePlayer getPlayer() {
        return this.player;
    }

    public void removeAll() {
        this.cleanViews();
        this.views.clear();
    }

    public boolean removeBossBar(BossBar bossBar) {
        BossBarView view = this.getViewFor(Objects.requireNonNull(bossBar, "bossBar cannot be null"));
        if (view != null && this.views.remove(view)) {
            this.detachBossBar(view);
            return true;
        }

        return false;
    }

    public void render() {
        this.cleanViews();
        Collections.sort(this.views);
        this.renderViews();
    }

    public boolean setPriority(BossBar bossBar, int priority) {
        BossBarView view = this.getViewFor(Objects.requireNonNull(bossBar, "bossBar cannot be null"));
        return view != null && this.setPriority(view, priority);
    }

    public boolean unsetPriority(BossBar bossBar) {
        return this.setPriority(bossBar, BarPriority.undefined());
    }

    //
    // Rendering Views
    //

    /** Clean player views. */
    protected void cleanViews() {
        for (BossBarView view : this.views) {
            this.detachBossBar(view);
        }
    }

    /** Render player views. */
    protected void renderViews() {
        Player bukkit = this.player.getBukkit();
        for (BossBarView view : this.views) {
            view.getBossBar().bukkit.addPlayer(bukkit);
        }
    }

    /** Fast fix for strange Bukkit NPE in CraftBossBar.java:190. */
    protected void detachBossBar(BossBarView view) {
        try {
            view.getBossBar().bukkit.removePlayer(this.player.getBukkit());
        } catch (NullPointerException ignored) {
        }
    }

    /** Get a view, or {@code null} corresponding to the given BossBar */
    protected BossBarView getViewFor(BossBar bossBar) {
        for (BossBarView view : this.views) {
            if (view.getBossBar().equals(bossBar)) {
                return view;
            }
        }

        return null;
    }

    /** Update boss bar's priority and refresh player views if needed. */
    protected boolean setPriority(BossBarView view, int priority) {
        int oldPriority = view.getPriority();
        if (oldPriority == priority) {
            return false;
        }

        view.setPriority(priority);
        this.render();
        return true;
    }
}
