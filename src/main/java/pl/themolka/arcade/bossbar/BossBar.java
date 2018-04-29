package pl.themolka.arcade.bossbar;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.craftbukkit.boss.CraftBossBar;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.util.FinitePercentage;
import pl.themolka.arcade.util.Percentage;

import java.util.Objects;

/**
 * Represents a boss bar.
 */
public class BossBar {
    // protected: BossBarFacet must access the Bukkit class.
    protected final CraftBossBar bukkit;

    public BossBar() {
        this(new TextComponent(), BarColor.WHITE, BarStyle.SOLID);
    }

    public BossBar(BarColor color, BarStyle style, BarFlag... flags) {
        this(new TextComponent(), color, style, flags);
    }

    public BossBar(BaseComponent[] text, BarColor color, BarStyle style, BarFlag... flags) {
        this(new TextComponent(text), color, style, flags);
    }

    public BossBar(BaseComponent title, BarColor color, BarStyle style, BarFlag... flags) {
        this.bukkit = new CraftBossBar(title, color, style, flags);
    }

    public void addFlag(BarFlag flag) {
        this.bukkit.addFlag(flag);
    }

    public void addFlags(BarFlag... flags) {
        for (BarFlag flag : flags) {
            this.addFlag(flag);
        }
    }

    public boolean addPlayer(GamePlayer player) {
        return this.facet(player).addBossBar(this);
    }

    public boolean addPlayer(GamePlayer player, int priority) {
        return this.facet(player).addBossBar(this, priority);
    }

    public BarColor getColor() {
        return this.bukkit.getColor();
    }

    public FinitePercentage getProgress() {
        return Percentage.finite(this.bukkit.getProgress());
    }

    public BarStyle getStyle() {
        return this.bukkit.getStyle();
    }

    public BaseComponent[] getText() {
        return new BaseComponent[] { this.bukkit.getTitle() };
    }

    public boolean hasFlag(BarFlag flag) {
        return this.bukkit.hasFlag(flag);
    }

    public boolean removeFlag(BarFlag flag) {
        if (this.bukkit.hasFlag(flag)) {
            this.bukkit.removeFlag(flag);
            return true;
        }

        return false;
    }

    public void removeFlags(BarFlag... flags) {
        for (BarFlag flag : flags) {
            this.removeFlag(flag);
        }
    }

    public boolean removePlayer(GamePlayer player) {
        return this.facet(player).removeBossBar(this);
    }

    public void setColor(BarColor color) {
        this.bukkit.setColor(color);
    }

    public boolean setPriority(GamePlayer player, int priority) {
        return this.facet(player).setPriority(this, priority);
    }

    public void setProgress(FinitePercentage progress) {
        this.bukkit.setProgress(progress.trim().getValue());
    }

    public void setStyle(BarStyle style) {
        this.bukkit.setStyle(style);
    }

    public void setText(BaseComponent... text) {
        this.bukkit.setTitle(new TextComponent(text));
    }

    public boolean unsetPriority(GamePlayer player) {
        return this.facet(player).unsetPriority(this);
    }

    protected BossBarFacet facet(GamePlayer player) {
        return Objects.requireNonNull(player, "player cannot be null").getBossBarFacet();
    }
}
