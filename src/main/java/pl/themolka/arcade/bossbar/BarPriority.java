package pl.themolka.arcade.bossbar;

import pl.themolka.arcade.event.Priority;

/**
 * Bar priorities are same as {@link Priority}. Note that {@link #FIRST} and
 * {@link #LAST} should be reserved for internal use only.
 */
public class BarPriority extends Priority {
    public static int undefined() {
        return NORMAL;
    }
}
