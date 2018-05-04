package pl.themolka.arcade.filter.matcher;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.OptionalResult;
import pl.themolka.arcade.filter.Filter;

public abstract class Matcher<T> implements Filter {
    @Override
    public final AbstainableResult filter(Object... objects) {
        if (objects != null) {
            for (Object object : objects) {
                if (this.find(object)) {
                    return OptionalResult.TRUE;
                }
            }
        }

        return OptionalResult.ABSTAIN;
    }

    public abstract boolean find(Object object);

    public abstract boolean matches(T t);

    interface Config<T extends Matcher<?>> extends Filter.Config<T> {
    }
}
