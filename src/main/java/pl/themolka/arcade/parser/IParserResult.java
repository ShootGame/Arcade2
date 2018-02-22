package pl.themolka.arcade.parser;

import java.util.Optional;

public interface IParserResult<T> {
    T get();

    boolean isPresent();

    default Optional<T> optional() {
        return this.isPresent() ? Optional.of(this.get()) : Optional.empty();
    }

    default T or(T def) {
        return this.isPresent() ? this.get() : def;
    }

    default T orFail() throws ParserException {
        return this.orNull();
    }

    default T orNull() {
        return this.or(null);
    }
}
