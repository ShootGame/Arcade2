package pl.themolka.arcade.bossbar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

/**
 * A single {@link BossBar} player view used in {@link BossBarContext}.
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
