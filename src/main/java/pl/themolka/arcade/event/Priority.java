package pl.themolka.arcade.event;

/**
 * Handlers are ordered by priority and handlers with higher priority are processed before
 * those with lower priority, i.e. Influence the order in which different handlers that consume
 * the same message type are invoked.
 *
 * https://github.com/bennidi/mbassador/blob/master/src/main/java/net/engio/mbassy/listener/Handler.java#L42-L47
 */
public class Priority {
    /**
     * Handled first, should contain <u>major</u> handlers
     * <b>Should be <u>never</u> canceled!</b>
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
     * <b>Should be <u>never</u> canceled!</b>
     */
    public static final int LAST = Integer.MIN_VALUE;
}
