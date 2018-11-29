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

package pl.themolka.arcade.filter;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.OptionalResult;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;

public enum StaticFilter implements Filter {
    ALLOW(OptionalResult.TRUE),
    DENY(OptionalResult.FALSE),
    ABSTAIN(OptionalResult.ABSTAIN),
    ;

    private final AbstainableResult result;
    private final Config config;

    StaticFilter(AbstainableResult result) {
        this.result = result;
        this.config = new Config() {
            public Ref<AbstainableResult> result() { return Ref.ofProvided(result); }
            public StaticFilter create(Game game, Library library) { return StaticFilter.this; }
        };
    }

    @Override
    public AbstainableResult filter(Object... objects) {
        return this.result;
    }

    public Config config() {
        return this.config;
    }

    public AbstainableResult getResult() {
        return this.result;
    }

    @Override
    public String toString() {
        return this.result.toString();
    }

    public interface Config extends Filter.Config<StaticFilter> {
        Ref<AbstainableResult> result();

        @Override
        default StaticFilter create(Game game, Library library) {
            AbstainableResult result = this.result().get();
            if (result.isTrue()) {
                return ALLOW;
            } else if (result.isFalse()) {
                return DENY;
            } else if (result.isAbstaining()) {
                return ABSTAIN;
            } else {
                throw new IllegalStateException("Illegal result");
            }
        }
    }
}
