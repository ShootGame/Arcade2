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

package pl.themolka.arcade.util.state;

public class ObjectState<T extends State> implements Statable<T> {
    private T state;

    protected ObjectState(T state) {
        this.state = state;
    }

    @Override
    public T getState() {
        return this.state;
    }

    @Override
    public boolean transform(T newState) {
        T old = this.getState();
        if (old != null) {
            old.destroy();
        }

        this.state = newState;
        newState.construct();
        return true;
    }

    //
    // Instancing
    //

    public static <T extends State> ObjectState defined(T state) {
        return new ObjectState<>(state);
    }

    public static <T extends State> ObjectState<T> undefined() {
        return new ObjectState<>(null);
    }
}
