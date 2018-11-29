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

package pl.themolka.arcade.objective.wool;

import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public class WoolPickupEvent extends WoolEvent implements Cancelable {
    private boolean cancel;
    private final boolean firstParticipatorPickup;
    private final boolean firstPickerPickup;
    private final ItemStack itemStack;
    private final Participator participator;
    private final GamePlayer picker;

    public WoolPickupEvent(Wool wool, boolean firstParticipatorPickup, boolean firstPickerPickup,
                           ItemStack itemStack, Participator participator, GamePlayer picker) {
        super(wool);

        this.firstParticipatorPickup = firstParticipatorPickup;
        this.firstPickerPickup = firstPickerPickup;
        this.itemStack = itemStack;
        this.participator = participator;
        this.picker = picker;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public Participator getParticipator() {
        return this.participator;
    }

    public GamePlayer getPicker() {
        return this.picker;
    }

    public boolean isFirstParticipatorPickup() {
        return this.firstParticipatorPickup;
    }

    public boolean isFirstPickerPickup() {
        return this.firstPickerPickup;
    }
}
