/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;
import pl.themolka.arcade.util.NamedValue;

import java.util.Objects;
import java.util.Optional;

public abstract class Result<T> implements IResult<T> {
    private final Element element; // DOM element
    private final Source source; // DOM name and value

    public Result(Element element, String name, String value) {
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
            super(name != null ? name : "", value);
        }
    }

    //
    // Instancing
    //

    // empty
    public static <T> Result<T> empty() {
        return empty(null);
    }

    public static <T> Result<T> empty(Element element) {
        return empty(element, null);
    }

    public static <T> Result<T> empty(Element element, String name) {
        return new EmptyResult<>(element, name);
    }

    // fail
    public static <T> Result<T> fail(ParserException fail) {
        return fail(fail, null);
    }

    public static <T> Result<T> fail(ParserException fail, String name) {
        return fail(fail, name, null);
    }

    public static <T> Result<T> fail(ParserException fail, String name, String value) {
        return new FailResult<>(fail, name, value);
    }

    // fine
    public static <T> Result<T> fine(Element element, String name, T result) {
        return fine(element, name, null, result);
    }

    public static <T> Result<T> fine(Element element, String name, String value, T result) {
        return new FineResult<>(
                Objects.requireNonNull(element, "element cannot be null (use maybe(...) instead?)"),
                Objects.requireNonNull(name, "name cannot be null (use maybe(...) instead?)"),
                value, // value is null when eg. NodeParser.parseTree is used.
                Objects.requireNonNull(result, "result cannot be null (use maybe(...) instead?)"));
    }

    // auto
    public static <T> Result<T> maybe(Element element, String name, String value, T maybe) {
        return element != null && name != null && value != null && maybe != null
                ? fine(element, name, value, maybe)
                : empty(element, name);
    }

    // java.util.Optional<T>
    public static <T> Result<T> ofOptional(Element element, String name, String value, Optional<T> optional) {
        return maybe(element, name, value, Objects.requireNonNull(optional.get(), "optional cannot be null"));
    }
}

class EmptyResult<T> extends FailResult<T> {
    EmptyResult(Element element, String name) {
        super(new ParserException(element, createMessage(element)), name, null);
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
        return element != null ? "No value defined (or missing)." : "Missing element";
    }
}

class FailResult<T> extends Result<T> {
    final ParserException fail;

    FailResult(ParserException fail, String name, String value) {
        super(fail.getContent(), name, value);

        this.fail = Objects.requireNonNull(fail, "fail cannot be null (use EmptyResult instead?)");
    }

    @Override
    public T get() {
        throw new NullPointerException("There is no parsed object in this result");
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

class FineResult<T> extends Result<T> {
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
