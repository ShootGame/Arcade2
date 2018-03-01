package pl.themolka.arcade.bossbar;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.craftbukkit.boss.CraftBossBar;
import org.bukkit.entity.Player;
import pl.themolka.arcade.util.Percentage;

/**
 * Wrapped Bukkit's boss bar.
 * Player rendering should be done using the {@link BossBarContext} class. Using
 * any player methods, such as {@link #addPlayer(Player)} or
 * {@link #removePlayer(Player)} may cause bugs.
 */
public class BossBar extends CraftBossBar implements org.bukkit.boss.BossBar {
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
        super(title, color, style, flags);
    }

    public BaseComponent[] getText() {
        return new BaseComponent[] { this.getTitle() };
    }

    public void setProgress(Percentage progress) {
        this.setProgress(progress.trim().getValue());
    }

    public void setText(BaseComponent... text) {
        this.setTitle(new TextComponent(text));
    }
}
