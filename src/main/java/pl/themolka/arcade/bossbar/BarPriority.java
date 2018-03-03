package pl.themolka.arcade.bossbar;

import pl.themolka.arcade.event.Priority;

/**
 * Bar priorities are same as {@link Priority}. Note that {@link #FIRST} and
 * {@link #LAST} should be reserved for internal use only.
 *
 * By convention, priorities higher than {@link #NORMAL} (eg. {@link #HIGH},
 * {@link #HIGHER} and {@link #HIGHEST}) should be used for static content, or
 * server announcements.
 *
 * By convention, priorities lower than {@link #NORMAL} (eg. {@link #LOW},
 * {@link #LOWER} and {@link #LOWEST}) should be used for dynamical content, or
 * specific game goals and map features.
 */
public class BarPriority extends Priority {
    public static int undefined() {
        return NORMAL;
    }
}
