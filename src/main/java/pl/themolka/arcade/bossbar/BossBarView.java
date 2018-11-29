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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

/**
 * A single {@link BossBar} player view used in {@link BossBarFacet}.
 */
class BossBarView implements Comparable<BossBarView> {
    private final BossBar bossBar;
    private int priority = BarPriority.undefined();

    BossBarView(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    @Override
    public int compareTo(BossBarView that) {
        return Integer.compare(that.priority, this.priority);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BossBarView && this.bossBar.equals(((BossBarView) obj).bossBar);
    }

    BossBar getBossBar() {
        return this.bossBar;
    }

    int getPriority() {
        return this.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bossBar);
    }

    void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("bossBar", this.bossBar)
                .append("priority", this.priority)
                .build();
    }
}
