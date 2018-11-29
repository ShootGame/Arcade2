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

package pl.themolka.arcade.event;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PublicationErrorHandler implements IPublicationErrorHandler {
    private final Logger logger;

    public PublicationErrorHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handleError(PublicationError error) {
        this.logger.log(Level.SEVERE, "Could not handle event '" +
                ((Event) error.getPublishedMessage()).getEventName() +
                "' in " + error.getListener().getClass().getName() +
                " because: " + error.getMessage(), error.getCause());
    }
}
