package pl.themolka.arcade.filter;

public abstract class ModifierFilter<T> extends AbstractFilter {
    private final T content;

    protected ModifierFilter(T content) {
        this.content = content;
    }

    public T getContent() {
        return this.content;
    }

    public static class All extends ModifierFilter<Filter[]> {
        public All(Filter[] content) {
            super(content);
        }

        @Override
        public FilterResult filter(Object object) {
            for (Filter filter : this.getContent()) {
                if (filter.filter(object).equals(FilterResult.DENY)) {
                    return FilterResult.DENY;
                }
            }

            return FilterResult.ALLOW;
        }
    }

    public static class Not extends ModifierFilter<Filter> {
        public Not(Filter filter) {
            super(filter);
        }

        @Override
        public FilterResult filter(Object object) {
            return this.getContent().filter(object).getOpposite();
        }
    }
}
