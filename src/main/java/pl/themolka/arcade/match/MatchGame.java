package pl.themolka.arcade.match;

import net.engio.mbassy.listener.Handler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.command.GeneralCommands;
import pl.themolka.arcade.command.Sender;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.cycle.CycleCountdown;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameManager;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.game.RestartCountdown;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.session.ArcadePlayer;
import pl.themolka.arcade.session.ArcadeSound;
import pl.themolka.arcade.task.Countdown;
import pl.themolka.arcade.time.Time;
import pl.themolka.arcade.time.TimeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchGame extends GameModule {
    public static final String JOIN_ON_CYCLE_MESSAGE = Messageable.ERROR_COLOR + "The match has ended. " +
            ChatColor.GOLD + "Please wait until the server cycle.";

    private boolean autoCycle;
    private boolean autoStart;
    private int defaultStartCountdown;
    private Match match;
    private Observers observers;
    private MatchStartCountdown startCountdown;

    protected MatchGame(Game game, IGameConfig.Library library, Config config) {
        this.autoCycle = config.autoCycle();
        this.autoStart = config.autoStart();
        this.defaultStartCountdown = (int) config.startCountdown().toSeconds();
        this.observers = (Observers) library.getOrDefine(game, config.observers().get());
    }

    @Override
    public void onEnable() {
        this.match = new Match(this.getPlugin(), this.getGame(), this.getObservers());
        this.getObservers().setMatch(this.getMatch());

        this.startCountdown = new MatchStartCountdown(this.getPlugin(), this.getMatch());
        this.getStartCountdown().setGame(this.getGame());

        this.getGame().getVisibility().addFilter(new MatchVisibilityFilter(this.getMatch())); // visibility filter

        for (ArcadePlayer player : this.getPlugin().getPlayers()) {
            if (player.getGamePlayer() != null) {
                this.getObservers().join(player.getGamePlayer(), false, true);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        return Arrays.asList(new MatchListeners(this), new ObserverListeners(this));
    }

    public int getDefaultStartCountdown() {
        return this.defaultStartCountdown;
    }

    public Match getMatch() {
        return this.match;
    }

    public Observers getObservers() {
        return this.observers;
    }

    public MatchStartCountdown getStartCountdown() {
        return this.startCountdown;
    }

    public void handleBeginCommand(Sender sender, int seconds, boolean force) {
        if (seconds == -1) {
            seconds = this.getDefaultStartCountdown();
        } else if (seconds < 3) {
            seconds = 3;
        }

        String message = "Starting";
        if (force) {
            message = "Force starting";
        }

        if (!this.getMatch().isStarting()) {
            throw new CommandException("The match is not in the starting state.");
        } else if (this.getPlugin().getGames().getCycleCountdown().isTaskRunning()) {
            throw new CommandException("Cannot start when cycle is running.");
        } else if (this.getPlugin().getGames().getRestartCountdown().isTaskRunning()) {
            throw new CommandException("Cannot start when restart is running.");
        }

        sender.sendSuccess(message + " the match in " + seconds + " seconds...");
        this.getMatch().setForceStart(force);
        this.startCountdown(seconds);
    }

    public void handleEndCommand(Sender sender, boolean auto, String winnerQuery, boolean draw) {
        if (this.getMatch().isRunning()) {
            MatchWinner winner = null;
            if (auto) {
                winner = this.getMatch().getWinner();

                if (winner == null) {
                    throw new CommandException("No winners are currently winning.");
                }
            } else if (draw) {
                winner = this.getMatch().getDrawWinner();
            } else if (winnerQuery != null) {
                winner = this.getMatch().findWinner(winnerQuery);

                if (winner == null) {
                    throw new CommandException("No winners found from the given query.");
                }
            }

            sender.sendSuccess("Force ending the match...");
            this.getMatch().end(winner, true);
        } else {
            throw new CommandException("The match is not currently running.");
        }
    }

    public List<String> handleEndCompleter(Sender sender, CommandContext context) {
        String request = context.getParams(0);
        if (request == null) {
            request = "";
        }

        List<String> results = new ArrayList<>();
        for (MatchWinner winner : this.getMatch().getWinnerList()) {
            if (winner.getName().toLowerCase().startsWith(request.toLowerCase())) {
                results.add(winner.getName());
            }
        }

        return results;
    }

    public boolean isAutoCycle() {
        return this.autoCycle;
    }

    public boolean isAutoStart() {
        return this.autoStart;
    }

    public int startCountdown(int seconds) {
        this.getStartCountdown().cancelCountdown();
        if (!this.getStartCountdown().isTaskRunning()) {
            return this.getStartCountdown().countStart(seconds);
        }

        return this.getStartCountdown().getTaskId();
    }

    @Handler(priority = Priority.HIGH)
    public void onCycleCommand(GeneralCommands.CycleCommandEvent event) {
        if (event.isForceRestart() && this.getMatch().isRunning()) {
            this.getMatch().sendGoalMessage(event.getPlugin().getServerName() + " is now restarting...");
            this.getMatch().end(true);
        } else if (this.getMatch().isRunning()) {
            event.getSender().sendError("The match is currently running. Type /end to end the match.");
            event.setCanceled(true);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onGoalCompleteEndMatch(GoalCompleteEvent event) {
        this.getMatch().refreshWinners();
    }

    @Handler(priority = Priority.HIGHEST)
    public void onJoinWhenMatchEnded(GameCommands.JoinCommandEvent event) {
        if (this.getMatch().isCycling()) {
            event.getSender().sendError(JOIN_ON_CYCLE_MESSAGE);
            event.setCanceled(true);
            event.setJoined(false);
        }
    }

    @Handler(priority = Priority.LAST)
    public void onMatchCountdownAutoStart(GameCommands.JoinCommandEvent event) {
        if (!event.isCanceled() && event.hasJoined() && !this.getStartCountdown().isTaskRunning() && this.isAutoStart()) {
            MatchStartCountdownEvent countdownEvent = new MatchStartCountdownEvent(this.getPlugin(), this.getMatch(), this.startCountdown);
            if (!countdownEvent.isCanceled()) {
                this.startCountdown(this.getDefaultStartCountdown());
            }
        }
    }

    /*
     * Don't use MatchEndEvent, because it is called before the players
     * are reset. The reset method would reset their titles.
     */
    @Handler(priority = Priority.LAST)
    public void onGameOverScreenRender(MatchEndedEvent event) {
        BaseComponent[] defaultComponent = TextComponent.fromLegacyText(
                ChatColor.AQUA + ChatColor.UNDERLINE.toString() + "Game over!");
        BaseComponent[] winnerComponent = TextComponent.fromLegacyText(
                ChatColor.GOLD + ChatColor.UNDERLINE.toString() + "Victory!");
        BaseComponent[] loserComponent = TextComponent.fromLegacyText(
                ChatColor.RED + ChatColor.UNDERLINE.toString() + "Defeat!");

        MatchWinner winner = event.getWinner();

        BaseComponent[] resultComponent;
        if (winner != null) {
            resultComponent = TextComponent.fromLegacyText(winner.getMessage());
        } else {
            resultComponent = TextComponent.fromLegacyText(""); // empty
        }

        int fadeIn = (int) Time.ofTicks(10).toTicks();
        int stay = (int) Time.ofSeconds(3).toTicks();
        int fadeOut = (int) Time.ofTicks(30).toTicks();

        boolean isDrawWinner = winner instanceof DrawMatchWinner;
        for (ArcadePlayer online : event.getPlugin().getPlayers()) {
            if (online.getGamePlayer() == null) {
                continue;
            }

            BaseComponent[] title;
            ArcadeSound sound;

            if (winner == null || this.getObservers().contains(online)) {
                title = defaultComponent;
                sound = ArcadeSound.ENEMY_LOST;
            } else if (isDrawWinner || winner.contains(online)) {
                title = winnerComponent;
                sound = ArcadeSound.ENEMY_LOST;
            } else {
                title = loserComponent;
                sound = ArcadeSound.ENEMY_WON;
            }

            online.getBukkit().showTitle(title, resultComponent, fadeIn, stay, fadeOut);
            online.play(sound);
        }
    }

    @Handler(priority = Priority.LOWEST)
    public void onCycleCountdownAutoStart(MatchEndedEvent event) {
        if (!this.isAutoCycle()) {
            // auto cycle is disabled
            return;
        }

        GameManager games = event.getPlugin().getGames();

        Countdown countdown;
        if (games.isNextRestart()) {
            countdown = games.getRestartCountdown();
            if (!countdown.isTaskRunning()) {
                ((RestartCountdown) countdown).setDefaultDuration();
            }
        } else {
            countdown = games.getCycleCountdown();
            if (!countdown.isTaskRunning()) {
                ((CycleCountdown) countdown).setDefaultDuration();
            }
        }

        if (!countdown.isTaskRunning()) {
            countdown.countSync();
        }
    }

    @Handler(priority = Priority.NORMAL)
    public void onMatchTimeDescribe(GameCommands.GameCommandEvent event) {
        String time = TimeUtils.prettyTime(this.getMatch().getTime(),
                                           ChatColor.GOLD.toString() + ChatColor.BOLD,
                                           ChatColor.GRAY.toString());

        event.getSender().send(ChatColor.GREEN + "Time: " + ChatColor.DARK_AQUA + time);
    }

    public interface Config extends IGameModuleConfig<MatchGame> {
        boolean DEFAULT_IS_AUTO_CYCLE = true;
        boolean DEFAULT_IS_AUTO_START = true;
        Time DEFAULT_START_COUNTDOWN = Time.ofSeconds(15);

        default boolean autoCycle() { return DEFAULT_IS_AUTO_CYCLE; }
        default boolean autoStart() { return DEFAULT_IS_AUTO_START; }
        default Time startCountdown() { return DEFAULT_START_COUNTDOWN; };
        Ref<Observers.Config> observers();

        @Override
        default MatchGame create(Game game, Library library) {
            return new MatchGame(game, library, this);
        }
    }
}
