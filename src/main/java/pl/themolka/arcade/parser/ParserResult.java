package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.util.NamedValue;

import java.util.Objects;
import java.util.Optional;

public abstract class ParserResult<T> {
    private final Element element; // DOM element
    private final Source source; // DOM name and value

    public ParserResult(Element element, String name, String value) {
        this.element = element;
        this.source = new Source(name, value);
    }

    public Element element() {
        return this.element;
    }

    public abstract T get();

    public abstract boolean isPresent();

    public T or(T def) {
        return this.isPresent() ? this.get() : def;
    }

    public T orFail() throws ParserException {
        return this.orNull();
    }

    public T orNull() {
        return this.or(null);
    }

    public Source source() {
        return this.source;
    }

    public abstract Optional<T> toOptional();

    protected class Source extends NamedValue<String, String> {
        Source(String name, String value) {
            super(name, value);
        }
    }

    //
    // Instancing
    //

    // empty
    public static ParserResult<?> empty() {
        return empty(null);
    }

    public static ParserResult<?> empty(Element element) {
        return empty(element, null);
    }

    public static ParserResult<?> empty(Element element, String name) {
        return empty(element, null, null);
    }

    public static ParserResult<?> empty(Element element, String name, String value) {
        return new EmptyResult<>(element, name, value);
    }

    // fail
    public static ParserResult<?> fail(ParserException fail) {
        return fail(fail, null);
    }

    public static ParserResult<?> fail(ParserException fail, String name) {
        return fail(fail, name, null);
    }

    public static ParserResult<?> fail(ParserException fail, String name, String value) {
        return new FailResult<>(fail, name, value);
    }

    // fine
    public static <T> ParserResult<T> fine(Element element, String name, String value, T result) {
        return new FineResult<>(
                Objects.requireNonNull(element, "element cannot be null (use maybe(...) instead?)"),
                Objects.requireNonNull(name, "name cannot be null (use maybe(...) instead?)"),
                value,
                Objects.requireNonNull(result, "result cannot be null (use maybe(...) instead?)"));
    }

    // auto
    public static <T> ParserResult<T> maybe(Element element, String name, String value, T maybe) {
        return element != null && name != null && value != null && maybe != null
                ? fine(element, name, value, maybe)
                : (ParserResult<T>) empty(element, name, value);
    }

    // java.util.Optional<T>
    public static <T> ParserResult<T> ofOptional(Element element, String name, String value, Optional<T> optional) {
        return maybe(element, name, value, Objects.requireNonNull(optional.get(), "optional cannot be null"));
    }
}

class EmptyResult<T> extends ParserResult<T> {
    EmptyResult(Element element, String name, String value) {
        super(element, name, value);
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T orFail() throws ParserException {
        Element element = this.element();
        throw new ParserException(element, element != null ? "No value defined (or empty)" : "Element undefined");
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }
}

class FailResult<T> extends ParserResult<T> {
    final ParserException fail;

    FailResult(ParserException fail, String name, String value) {
        super(fail.getElement(), name, value);

        this.fail = Objects.requireNonNull(fail, "fail cannot be null");
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T orFail() throws ParserException {
        throw this.fail();
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }

    ParserException fail() {
        return this.fail;
    }
}

class FineResult<T> extends ParserResult<T> {
    final T result;

    FineResult(Element element, String name, String value, T result) {
        super(element, name, value);

        this.result = Objects.requireNonNull(result, "results cannot be null");
    }

    @Override
    public T get() {
        return this.result();
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.of(this.result);
    }

    T result() {
        return this.result;
    }
}
