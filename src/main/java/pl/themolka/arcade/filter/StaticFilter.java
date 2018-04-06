package pl.themolka.arcade.filter;

import pl.themolka.arcade.condition.AbstainableResult;
import pl.themolka.arcade.condition.OptionalResult;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;

public enum StaticFilter implements Filter {
    ALLOW(OptionalResult.TRUE),
    DENY(OptionalResult.FALSE),
    ABSTAIN(OptionalResult.ABSTAIN),
    ;

    private final AbstainableResult result;
    private final Config config;

    StaticFilter(AbstainableResult result) {
        this.result = result;
        this.config = new Config() {
            public Ref<AbstainableResult> result() { return Ref.ofProvided(result); }
            public StaticFilter create(Game game, Library library) { return StaticFilter.this; }
        };
    }

    @Override
    public AbstainableResult filter(Object... objects) {
        return this.result;
    }

    public Config config() {
        return this.config;
    }

    public AbstainableResult getResult() {
        return this.result;
    }

    @Override
    public String toString() {
        return this.result.toString();
    }

    public interface Config extends Filter.Config<StaticFilter> {
        Ref<AbstainableResult> result();

        @Override
        default StaticFilter create(Game game, Library library) {
            AbstainableResult result = this.result().get();
            if (result.isTrue()) {
                return ALLOW;
            } else if (result.isFalse()) {
                return DENY;
            } else if (result.isAbstaining()) {
                return ABSTAIN;
            } else {
                throw new IllegalStateException("Illegal result");
            }
        }
    }
}
