package pl.themolka.arcade.filter;

public abstract class StaticFilter extends AbstractFilter {
    public static final StaticFilter ALLOW = new Allow();
    public static final StaticFilter DENY = new Deny();

    private static class Allow extends StaticFilter {
        @Override
        public FilterResult filter(Object object) {
            return FilterResult.ALLOW;
        }
    }

    private static class Deny extends StaticFilter {
        @Override
        public FilterResult filter(Object object) {
            return FilterResult.DENY;
        }
    }
}
