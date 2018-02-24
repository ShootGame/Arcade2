package pl.themolka.arcade.parser;

import java.util.Optional;

public interface IParserResult<T> {
    /**
     * Get the result, never null.
     * @throws NullPointerException if there is no result.
     */
    T get();

    /**
     * If the result is present?
     */
    boolean isPresent();

    /**
     * Convert this result to an {@link Optional<T>}.
     */
    default Optional<T> optional() {
        return this.isPresent() ? Optional.of(this.get()) : Optional.empty();
    }

    /**
     * Return parsed result,
     * or return the given value, if the result is not provided.
     */
    default T or(T def) {
        return this.isPresent() ? this.get() : def;
    }

    /**
     * Return parsed result,
     * or return the given value, if the input was not provided.
     * @throws ParserException If input was provided but it failed to parse.
     */
    T orDefault(T def) throws ParserException;

    /**
     * Return parsed result,
     * or return null, if the input was not provided.
     * @throws ParserException If input was provided but it failed to parse.
     */
    default T orDefaultNull() throws ParserException {
        return this.orDefault(null);
    }

    /**
     * Return parsed result,
     * or throw {@link ParserException} if the result is not provided.
     * @throws ParserException If result is not provided.
     */
    default T orFail() throws ParserException {
        return this.orNull();
    }

    /**
     * Return parsed result,
     * or return null, if the result is not provided.
     */
    default T orNull() {
        return this.or(null);
    }
}
