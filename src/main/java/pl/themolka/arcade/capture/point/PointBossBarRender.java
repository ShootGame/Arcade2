package pl.themolka.arcade.capture.point;

import net.engio.mbassy.listener.Handler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.Listener;
import pl.themolka.arcade.bossbar.BarPriority;
import pl.themolka.arcade.bossbar.BossBar;
import pl.themolka.arcade.bossbar.BossBarUtils;
import pl.themolka.arcade.capture.CaptureGame;
import pl.themolka.arcade.capture.point.state.PointState;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.team.TeamEditEvent;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.Percentage;

import java.util.Map;
import java.util.WeakHashMap;

public class PointBossBarRender implements Listener {
    public static final BarColor BAR_COLOR = BossBarUtils.color(Point.DEFAULT_NEUTRAL_COLOR);
    public static final BarFlag[] BAR_FLAGS = {};
    public static final int BAR_PRIORITY = BarPriority.LOWER;
    public static final BarStyle BAR_STYLE = BarStyle.SOLID;

    private final CaptureGame game;

    private final Map<Point, BossBar> bossBars = new WeakHashMap<>();

    public PointBossBarRender(CaptureGame game) {
        this.game = game;
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
            this.bossBars.put(point, bossBar = new BossBar(BAR_COLOR, BAR_STYLE, BAR_FLAGS));
        }

        bossBar.setColor(BossBarUtils.color(pointColor, BAR_COLOR));
        bossBar.setProgress(Percentage.finite(progress));
        bossBar.setText(this.createBarTitle(point.getName(), pointColor, progress, progressColor));
        return bossBar;
    }

    public BossBar renderBossBar(Point point, GamePlayer player) {
        return this.renderBossBar(point, point.getState(), player);
    }

    public BossBar renderBossBar(Point point, PointState state, GamePlayer player) {
        return this.renderBossBar(point, state.getColor(), state.getProgress(), player);
    }

    public BossBar renderBossBar(Point point, Color pointColor, double progress, GamePlayer player) {
        return this.renderBossBar(point, pointColor, progress, point.getNeutralColor(), player);
    }

    public BossBar renderBossBar(Point point, Color pointColor, double progress, Color progressColor, GamePlayer player) {
        if (player != null) {
            BossBar bossBar = this.renderBossBar(point, pointColor, progress, progressColor);
            if (bossBar != null) {
                bossBar.addPlayer(player, BAR_PRIORITY);
            }

            return bossBar;
        }

        return null;
    }

    //
    // Removing
    //

    public void removeBossBar(Point point, GamePlayer player) {
        BossBar bossBar = this.bossBars.get(point);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    //
    // Listening
    //

    @Handler(priority = Priority.LAST)
    public void participatorEdit(TeamEditEvent event) {
        if (event.getReason().equals(TeamEditEvent.Reason.PAINT)) {
            for (Point point : this.bossBars.keySet()) {
                this.renderBossBar(point, point.getState());
            }
        }
    }

    //
    // Player Tracking
    //

    @Handler(priority = Priority.LAST)
    public void playerEnter(PointPlayerEnterEvent event) {
        this.renderBossBar(event.getPoint(), event.getPlayer());
    }

    @Handler(priority = Priority.LAST)
    public void playerLeave(PointPlayerLeaveEvent event) {
        this.removeBossBar(event.getPoint(), event.getPlayer());
    }

    //
    // Changing States
    //

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

    private BaseComponent[] createBarTitle(String pointName, Color pointColor, double progress, Color progressColor) {
        String progressPretty = Math.round(progress * 100D) + "%";

        net.md_5.bungee.api.ChatColor realPointColor = pointColor.toComponent();
        net.md_5.bungee.api.ChatColor realDefaultColor = progressColor.toComponent();

        // Create spaces to TRY to center pointName on the boss bar.
        String prefix = CommandUtils.createLine((int) Math.round(progressPretty.length() * 1.5D), " ");

        return new ComponentBuilder(prefix + "   ") // +3 spaces
                .append(pointName).color(realPointColor).bold(true)
                .append("   ") // 3 spaces
                .append(progressPretty).color(realDefaultColor).bold(false)
                .create();
    }
}
