package pl.themolka.arcade.capture.point;

import net.engio.mbassy.listener.Handler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pl.themolka.arcade.bossbar.BossBarUtils;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameStopEvent;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.team.TeamEditEvent;
import pl.themolka.arcade.util.Color;

import java.util.Map;
import java.util.WeakHashMap;

public class PointBossBarRender implements Listener {
    public static final BarColor BAR_COLOR = BossBarUtils.color(Point.DEFAULT_NEUTRAL_COLOR);
    public static final BarFlag[] BAR_FLAGS = {};
    public static final BarStyle BAR_STYLE = BarStyle.SOLID;
    public static final BaseComponent BAR_TEXT = new TextComponent();

    private final CaptureGame game;

    private final Map<Point, BossBar> bossBars = new WeakHashMap<>();

    public PointBossBarRender(CaptureGame game) {
        this.game = game;
    }

    public BossBar createBossBar() {
        return this.game.getServer().createBossBar(BAR_TEXT, BAR_COLOR, BAR_STYLE, BAR_FLAGS);
    }

    //
    // Rendering
    //

    public BossBar renderBossBar(Point point) {
        return this.renderBossBar(point, point.getState());
    }

    public BossBar renderBossBar(Point point, PointState state) {
        return this.renderBossBar(point, state.getColor(), state.getProgress());
    }

    public BossBar renderBossBar(Point point, Color pointColor, double progress) {
        return this.renderBossBar(point, pointColor, progress, point.getNeutralColor());
    }

    public BossBar renderBossBar(Point point, Color pointColor, double progress, Color progressColor) {
        BossBar bossBar = this.bossBars.get(point);
        if (bossBar == null) {
            this.bossBars.put(point, bossBar = this.createBossBar());
        }

        bossBar.setColor(BossBarUtils.color(progressColor, BAR_COLOR));
        bossBar.setProgress(progress);
        bossBar.setTitle(this.createBarTitle(point.getName(), pointColor, progress, progressColor));
        bossBar.setVisible(true);
        return bossBar;
    }

    public BossBar renderBossBar(Point point, Player bukkit) {
        return this.renderBossBar(point, point.getState(), bukkit);
    }

    public BossBar renderBossBar(Point point, PointState state, Player bukkit) {
        return this.renderBossBar(point, state.getColor(), state.getProgress(), bukkit);
    }

    public BossBar renderBossBar(Point point, Color pointColor, double progress, Player bukkit) {
        return this.renderBossBar(point, pointColor, progress, point.getNeutralColor(), bukkit);
    }

    public BossBar renderBossBar(Point point, Color pointColor, double progress, Color progressColor, Player bukkit) {
        if (bukkit != null) {
            BossBar bossBar = this.renderBossBar(point, pointColor, progress, progressColor);
            if (bossBar != null) {
                bossBar.addPlayer(bukkit);
            }

            return bossBar;
        }

        return null;
    }

    //
    // Removing
    //

    public void removeBossBar(Point point, Player bukkit) {
        BossBar bossBar = this.bossBars.get(point);
        if (bossBar != null) {
            try {
                bossBar.removePlayer(bukkit);
            } catch (NullPointerException ignored) {
                // Strange Bukkit bug in CraftBossBar.removePlayer(CraftBossBar.java:190)
            }
        }
    }

    //
    // Listening
    //

    @Handler(priority = Priority.LAST)
    public void gameOver(GameStopEvent event) {
        for (BossBar bossBar : this.bossBars.values()) {
            // We need need to send the "REMOVE" packet to all members
            // of this boss bar to remove it from their views.
            bossBar.removeAll();
        }

        this.bossBars.clear();
    }

    @Handler(priority = Priority.LAST)
    public void participatorEdit(TeamEditEvent event) {
        if (event.getReason().equals(TeamEditEvent.Reason.PAINT)) {
            for (Point point : this.bossBars.keySet()) {
                this.renderBossBar(point, point.getState());
            }
        }
    }

    @Handler(priority = Priority.LAST)
    public void playerEnter(PointPlayerEnterEvent event) {
        this.renderBossBar(event.getPoint(), event.getPlayer().getBukkit());
    }

    @Handler(priority = Priority.LAST)
    public void playerLeave(PointPlayerLeaveEvent event) {
        this.removeBossBar(event.getPoint(), event.getPlayer().getBukkit());
    }

    @Handler(priority = Priority.LAST)
    public void stateChange(PointCapturedEvent event) {
        this.renderBossBar(event.getPoint(), event.getNewOwner().getColor(), event.getNewState().getProgress());
    }

    @Handler(priority = Priority.LAST)
    public void stateChange(PointCapturingEvent event) {
        this.renderBossBar(event.getPoint(), event.getNewState());
    }

    @Handler(priority = Priority.LAST)
    public void stateChange(PointLosingEvent event) {
        this.renderBossBar(event.getPoint(), event.getNewState());
    }

    @Handler(priority = Priority.LAST)
    public void stateChange(PointLostEvent event) {
        this.renderBossBar(event.getPoint(), event.getNewState());
    }

    @Handler(priority = Priority.LAST)
    public void stateProgress(GoalProgressEvent event) {
        Goal goal = event.getGoal();
        if (goal instanceof Point) {
            this.renderBossBar((Point) goal);
        }
    }

    //
    // Helper Methods
    //

    private BaseComponent createBarTitle(String pointName, Color pointColor, double progress, Color progressColor) {
        String progressPretty = Math.round(progress * 100D) + "%";

        net.md_5.bungee.api.ChatColor realPointColor = pointColor.toComponent();
        net.md_5.bungee.api.ChatColor realDefaultColor = progressColor.toComponent();

        // Create spaces to TRY to center pointName on the boss bar.
        String prefix = CommandUtils.createLine((int) Math.round(progressPretty.length() * 1.5D), " ");

        return new TextComponent(new ComponentBuilder(prefix + "   ") // +3 spaces
                .append(pointName).color(realPointColor).bold(true)
                .append("   ") // 3 spaces
                .append(progressPretty).color(realDefaultColor).bold(false)
                .create());
    }
}
