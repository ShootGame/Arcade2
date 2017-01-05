package pl.themolka.arcade.filter;

public abstract class AbstractFilter implements Filter {
    @Override
    public FilterResult filter(Object... objects) {
        for (Object object : objects) {
            FilterResult result = filter(object);
            if (result.equals(FilterResult.ABSTAIN)) {
                continue;
            }

            return result;
        }

        return FilterResult.ABSTAIN;
    }

    public abstract FilterResult filter(Object object);
}
