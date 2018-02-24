package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.util.NamedValue;

import java.util.Objects;
import java.util.Optional;

public abstract class ParserResult<T> implements IParserResult<T> {
    private final Element element; // DOM element
    private final Source source; // DOM name and value

    public ParserResult(Element element, String name, String value) {
        this.element = element;
        this.source = new Source(name, value);
    }

    public Element element() {
        return this.element;
    }

    public Source source() {
        return this.source;
    }

    protected class Source extends NamedValue<String, String> {
        Source(String name, String value) {
            super(name, value);
        }
    }

    //
    // Instancing
    //

    // empty
    public static <T> ParserResult<T> empty() {
        return empty(null);
    }

    public static <T> ParserResult<T> empty(Element element) {
        return empty(element, null);
    }

    public static <T> ParserResult<T> empty(Element element, String name) {
        return empty(element, name, null);
    }

    public static <T> ParserResult<T> empty(Element element, String name, String value) {
        return new EmptyResult<>(element, name, value);
    }

    // fail
    public static <T> ParserResult<T> fail(ParserException fail) {
        return fail(fail, null);
    }

    public static <T> ParserResult<T> fail(ParserException fail, String name) {
        return fail(fail, name, null);
    }

    public static <T> ParserResult<T> fail(ParserException fail, String name, String value) {
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
                : empty(element, name, value);
    }

    // java.util.Optional<T>
    public static <T> ParserResult<T> ofOptional(Element element, String name, String value, Optional<T> optional) {
        return maybe(element, name, value, Objects.requireNonNull(optional.get(), "optional cannot be null"));
    }
}

class EmptyResult<T> extends FailResult<T> {
    EmptyResult(Element element, String name, String value) {
        super(new ParserException(element, createMessage(element)), name, value);
    }

    @Override
    public T orDefault(T def) throws ParserException {
        return def;
    }

    @Override
    public T orDefaultNull() throws ParserException {
        return null;
    }

    static String createMessage(Element element) {
        return element != null ? "No value defined (or empty)." : "Element undefined";
    }
}

class FailResult<T> extends ParserResult<T> {
    final ParserException fail;

    FailResult(ParserException fail, String name, String value) {
        super(fail.getElement(), name, value);

        this.fail = Objects.requireNonNull(fail, "fail cannot be null (use EmptyResult instead?)");
    }

    @Override
    public T get() {
        throw new NullPointerException("Parser failed");
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Optional<T> optional() {
        return Optional.empty();
    }

    @Override
    public T orDefault(T def) throws ParserException {
        return this.orFail();
    }

    @Override
    public T orFail() throws ParserException {
        throw this.fail();
    }

    ParserException fail() {
        return this.fail;
    }
}

class FineResult<T> extends ParserResult<T> {
    final T result;

    FineResult(Element element, String name, String value, T result) {
        super(element, name, value);

        this.result = Objects.requireNonNull(result, "results cannot be null (use EmptyResult instead?)");
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
    public T orDefault(T def) throws ParserException {
        return this.or(def);
    }

    T result() {
        return this.result;
    }
}
