package pl.themolka.arcade.filter;

public abstract class ModifierFilter<T> extends AbstractFilter {
    private final T content;

    protected ModifierFilter(T content) {
        this.content = content;
    }

    public T getContent() {
        return this.content;
    }

    public static ModifierFilter<Filter[]> all(Filter... content) {
        return new All(content);
    }

    public static ModifierFilter<Filter[]> any(Filter... content) {
        return new Any(content);
    }

    public static ModifierFilter<Filter[]> none(Filter... content) {
        return new None(content);
    }

    public static ModifierFilter<Filter> not(Filter filter) {
        return new Not(filter);
    }
}

class All extends ModifierFilter<Filter[]> {
    All(Filter... content) {
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

class Any extends ModifierFilter<Filter[]> {
    Any(Filter[] content) {
        super(content);
    }

    @Override
    public FilterResult filter(Object object) {
        for (Filter filter : this.getContent()) {
            if (filter.filter(object).equals(FilterResult.ALLOW)) {
                return FilterResult.ALLOW;
            }
        }

        return FilterResult.DENY;
    }
}

class None extends ModifierFilter<Filter[]> {
    None(Filter[] content) {
        super(content);
    }

    @Override
    public FilterResult filter(Object object) {
        for (Filter filter : this.getContent()) {
            if (filter.filter(object).equals(FilterResult.ALLOW)) {
                return FilterResult.DENY;
            }
        }

        return FilterResult.ALLOW;
    }
}

class Not extends ModifierFilter<Filter> {
    Not(Filter filter) {
        super(filter);
    }

    @Override
    public FilterResult filter(Object object) {
        return this.getContent().filter(object).getOpposite();
    }
}
