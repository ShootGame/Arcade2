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

package pl.themolka.arcade.objective;

import org.bukkit.ChatColor;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.config.Unique;
import pl.themolka.arcade.event.EventListenerComponent;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalCompleteEvent;
import pl.themolka.arcade.goal.GoalResetEvent;
import pl.themolka.arcade.goal.SimpleInteractableGoal;
import pl.themolka.arcade.util.StringId;

public abstract class Objective extends SimpleInteractableGoal
                                implements EventListenerComponent, StringId {
    private final String id;
    private final boolean objective;

    protected Objective(Game game, IGameConfig.Library library, Config<?> config) {
        super(game, library, config);

        this.id = config.id();
        this.objective = config.objective().get();
    }

    @Override
    protected final void complete(Participator completer) {
        ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(this, completer);
        this.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled()) {
            GoalCompleteEvent.call(this, completer);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public final boolean reset() {
        ObjectiveResetEvent event = new ObjectiveResetEvent(this);
        this.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return false;
        }

        GoalResetEvent.call(this);
        this.contributions.clearContributors();
        this.setCompleted(false);
        this.setTouched(false);
        this.resetObjective();
        return true;
    }

    // NOTE: Implementations MUST call super.completeObjective(...)!
    public void completeObjective(Participator completer, GamePlayer player) {
        this.setCompleted(true, completer);
    }

    public String describeParticipator(Participator participator) {
        return participator != null ? ChatColor.GOLD + participator.getTitle() + ChatColor.YELLOW + "'s " + ChatColor.RESET
                                    : "";
    }

    public String describeObjective() {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + ChatColor.ITALIC + this.getColoredName() + ChatColor.RESET;
    }

    public String describeOwner() {
        return this.describeParticipator(this.getOwner());
    }

    public boolean isObjective() {
        return this.objective;
    }

    // NOTE: Implementations MUST call super.resetObjective(...)!
    public void resetObjective() {
    }

    public interface Config<T extends Objective> extends SimpleInteractableGoal.Config<T>, Unique {
        boolean DEFAULT_IS_OBJECTIVE = true;

        Ref<Boolean> objective();
    }
}
