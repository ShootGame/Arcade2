package pl.themolka.arcade.parser;

import pl.themolka.arcade.util.Forwarding;

import java.util.Optional;

public abstract class ForwardingResult<T> extends Forwarding<IResult<T>>
                                          implements IResult<T> {
    @Override
    public T get() {
        return this.delegate().get();
    }

    @Override
    public boolean isPresent() {
        return this.delegate().isPresent();
    }

    @Override
    public Optional<T> optional() {
        return this.delegate().optional();
    }

    @Override
    public T or(T def) {
        return this.delegate().or(def);
    }

    @Override
    public T orDefault(T def) throws ParserException {
        return this.delegate().orDefault(def);
    }

    @Override
    public T orDefaultNull() throws ParserException {
        return this.delegate().orDefaultNull();
    }

    @Override
    public T orFail() throws ParserException {
        return this.delegate().orFail();
    }

    @Override
    public T orNull() {
        return this.delegate().orNull();
    }
}
