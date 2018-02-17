package pl.themolka.arcade.filter;

import java.util.Collection;

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

    public static ModifierFilter<Filter[]> all(Collection<Filter> content) {
        return new All(content.toArray(new Filter[content.size()]));
    }

    public static ModifierFilter<Filter[]> any(Filter... content) {
        return new Any(content);
    }

    public static ModifierFilter<Filter[]> any(Collection<Filter> content) {
        return new Any(content.toArray(new Filter[content.size()]));
    }

    public static ModifierFilter<Filter[]> none(Filter... content) {
        return new None(content);
    }

    public static ModifierFilter<Filter[]> none(Collection<Filter> content) {
        return new None(content.toArray(new Filter[content.size()]));
    }

    public static ModifierFilter<Filter> not(Filter filter) {
        return new Not(filter);
    }

    public static ModifierFilter<Filter> not(Filter... content) {
        return new Not(all(content));
    }

    public static ModifierFilter<Filter> not(Collection<Filter> content) {
        return new Not(all(content));
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
    Any(Filter... content) {
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
    None(Filter... content) {
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
