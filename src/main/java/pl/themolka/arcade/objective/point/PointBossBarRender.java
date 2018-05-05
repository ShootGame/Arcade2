package pl.themolka.arcade.objective.point;

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
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.goal.Goal;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.objective.point.state.PointState;
import pl.themolka.arcade.team.TeamEditEvent;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.FinitePercentage;

import java.util.Map;
import java.util.WeakHashMap;

public class PointBossBarRender implements Listener {
    public static final BarColor BAR_COLOR = BossBarUtils.color(Point.Config.DEFAULT_NEUTRAL_COLOR);
    public static final BarFlag[] BAR_FLAGS = {};
    public static final int BAR_PRIORITY = BarPriority.LOWER;
    public static final BarStyle BAR_STYLE = BarStyle.SOLID;

    private final Map<Point, BossBar> bossBars = new WeakHashMap<>();

    //
    // Rendering
    //

    public BossBar renderBossBar(Point point) {
        return this.renderBossBar(point, point.getState());
    }

    public BossBar renderBossBar(Point point, PointState state) {
        return this.renderBossBar(point, state.getColor(), state.getProgress());
    }

    public BossBar renderBossBar(Point point, Color pointColor, FinitePercentage progress) {
        return this.renderBossBar(point, pointColor, progress, point.getNeutralColor());
    }

    public BossBar renderBossBar(Point point, Color pointColor, FinitePercentage progress, Color progressColor) {
        BossBar bossBar = this.bossBars.get(point);
        if (bossBar == null) {
            this.bossBars.put(point, bossBar = new BossBar(BAR_COLOR, BAR_STYLE, BAR_FLAGS));
        }

        bossBar.setColor(BossBarUtils.color(pointColor, BAR_COLOR));
        bossBar.setProgress(progress);
        bossBar.setText(this.createBarTitle(point.getName(), pointColor, progress, progressColor));
        return bossBar;
    }

    public BossBar renderBossBar(Point point, GamePlayer player) {
        return this.renderBossBar(point, point.getState(), player);
    }

    public BossBar renderBossBar(Point point, PointState state, GamePlayer player) {
        return this.renderBossBar(point, state.getColor(), state.getProgress(), player);
    }

    public BossBar renderBossBar(Point point, Color pointColor, FinitePercentage progress, GamePlayer player) {
        return this.renderBossBar(point, pointColor, progress, point.getNeutralColor(), player);
    }

    public BossBar renderBossBar(Point point, Color pointColor, FinitePercentage progress, Color progressColor, GamePlayer player) {
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
    public void playerEnter(CaptureEvent.Enter event) {
        this.renderBossBar(event.getGoal(), event.getPlayer());
    }

    @Handler(priority = Priority.LAST)
    public void playerLeave(CaptureEvent.Leave event) {
        this.removeBossBar(event.getGoal(), event.getPlayer());
    }

    //
    // Changing States
    //

    @Handler(priority = Priority.LAST)
    public void stateChange(PointCaptureEvent event) {
        this.renderBossBar(event.getGoal(), event.getCapturer().getColor(), event.getNewState().getProgress());
    }

    @Handler(priority = Priority.LAST)
    public void stateChange(PointCapturingEvent event) {
        this.renderBossBar(event.getGoal(), event.getNewState());
    }

    @Handler(priority = Priority.LAST)
    public void stateChange(PointLoseEvent event) {
        this.renderBossBar(event.getGoal(), event.getNewState());
    }

    @Handler(priority = Priority.LAST)
    public void stateChange(PointLosingEvent event) {
        this.renderBossBar(event.getGoal(), event.getNewState());
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

    private BaseComponent[] createBarTitle(String pointName, Color pointColor, FinitePercentage progress, Color progressColor) {
        String progressPretty = Math.round(progress.getValue() * 100D) + "%";

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
