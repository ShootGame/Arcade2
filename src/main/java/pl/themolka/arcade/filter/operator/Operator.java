package pl.themolka.arcade.filter.operator;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.Game;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class Operator implements Filter {
    private final Set<Filter> body = new LinkedHashSet<>();

    protected Operator(Game game, Config<?> config) {
        for (Filter.Config<?> filter : config.body().get()) {
            this.body.add(filter.create(game));
        }
    }

    public Set<Filter> getBody() {
        return new LinkedHashSet<>(this.body);
    }

    public interface Config<T extends Operator> extends Filter.Config<T> {
        Ref<Set<Filter.Config<?>>> body();
    }
}
