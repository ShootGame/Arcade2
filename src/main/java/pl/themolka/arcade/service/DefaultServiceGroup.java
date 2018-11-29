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

package pl.themolka.arcade.service;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.command.BanCommandsService;
import pl.themolka.arcade.dom.Namespace;
import pl.themolka.arcade.event.BlockTransformEventService;
import pl.themolka.arcade.map.MapTimeService;
import pl.themolka.arcade.session.PlayerMoveEventService;
import pl.themolka.arcade.session.SessionsService;
import pl.themolka.arcade.window.WindowService;

import java.util.Set;

public class DefaultServiceGroup extends ServiceGroup {
    public static final Namespace SERVICE_GROUP_NAMESPACE = Namespace.getDefault();

    private static final Set<Class<? extends Service>> defaultServices = ImmutableSet.<Class<? extends Service>>builder()
            .add(AntiLogoutService.class)
            .add(BanCommandsService.class)
            .add(BanEnderChestsService.class)
            .add(BlockTransformEventService.class)
            .add(DimensionPortalsService.class)
            .add(FixArrowsStuckService.class)
            .add(MapTimeService.class)
            .add(PlayerMoveEventService.class)
            .add(SafeWorkbenchesService.class)
            .add(SessionsService.class)
            .add(SetNextRestartService.class)
            .add(WeatherService.class)
            .add(WindowService.class)
            .add(WorldInitEventService.class)
            .build();

    public DefaultServiceGroup() {
        super(SERVICE_GROUP_NAMESPACE);
    }

    public void registerDefaults() {
        for (Class<? extends Service> defaultService : defaultServices) {
            this.addService(defaultService);
        }
    }
}
