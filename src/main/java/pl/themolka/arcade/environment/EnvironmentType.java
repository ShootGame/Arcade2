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

package pl.themolka.arcade.environment;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.development.Development;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Node;

public enum EnvironmentType {
    DEVELOPMENT {
        @Override
        public Environment buildEnvironment(Node options) throws DOMException {
            return new Development(options, this);
        }
    },

    PRODUCTION {
        @Override
        public Environment buildEnvironment(Node options) throws DOMException {
            return new Production(options, this);
        }
    },
    ;

    public abstract Environment buildEnvironment(Node options) throws DOMException;

    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase().replace("_", " "));
    }
}
