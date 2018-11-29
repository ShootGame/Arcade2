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

/**
 * Handlers are ordered by priority and handlers with higher priority are processed before
 * those with lower priority, i.e. Influence the order in which different handlers that consume
 * the same message type are invoked.
 *
 * https://github.com/bennidi/mbassador/blob/master/src/main/java/net/engio/mbassy/listener/Handler.java#L42-L47
 */
public class Priority {
    // This class can be inherited.
    protected Priority() {
    }

    /**
     * Handled first, should contain <u>major</u> handlers
     * <b>Should <u>never</u> be canceled!</b>
     */
    public static final int FIRST = Integer.MAX_VALUE;

    /**
     * Handled after {@link #FIRST}, should contain <u>major handlers</u>
     */
    public static final int HIGHEST = 3;

    /**
     * Handled after {@link #HIGHEST}, should contain <u>minor handlers</u>
     */
    public static final int HIGHER = 2;

    /**
     * Handled after {@link #HIGHER}
     */
    public static final int HIGH = 1;

    /**
     * Handled after {@link #HIGH}, should contain <u>most handlers</u>
     */
    public static final int NORMAL = 0;

    /**
     * Handled after {@link #NORMAL}
     */
    public static final int LOW = -1;

    /**
     * Handled after {@link #LOW}, should contain <u>minor handlers</u>
     */
    public static final int LOWER = -2;

    /**
     * Handled after {@link #LOWER}, should contain <u>major handlers</u>
     */
    public static final int LOWEST = -3;

    /**
     * Handled after {@link #LOWEST}, should contain <u>major handlers</u>
     * <b>Should <u>never</u> be canceled!</b>
     */
    public static final int LAST = Integer.MIN_VALUE;
}
