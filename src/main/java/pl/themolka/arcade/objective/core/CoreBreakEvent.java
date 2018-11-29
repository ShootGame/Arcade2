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

package pl.themolka.arcade.objective.core;

import org.bukkit.block.Block;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public class CoreBreakEvent extends CoreEvent implements Cancelable {
    private final Block block;
    private final GamePlayer breaker;
    private boolean cancel;
    private final Participator participator;

    public CoreBreakEvent(Core core, Block block, GamePlayer breaker, Participator participator) {
        super(core);

        this.block = block;
        this.breaker = breaker;
        this.participator = participator;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Block getBlock() {
        return this.block;
    }

    public GamePlayer getBreaker() {
        return this.breaker;
    }

    public Participator getParticipator() {
        return this.participator;
    }
}
