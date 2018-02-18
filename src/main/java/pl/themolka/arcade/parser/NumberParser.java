package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Element;

public interface NumberParser<R extends Number> extends ElementParser<R> {
    NumberParser<Byte> BYTE = new ByteParser();
    NumberParser<Double> DOUBLE = new DoubleParser();
    NumberParser<Float> FLOAT = new FloatParser();
    NumberParser<Integer> INT = new IntegerParser();
    NumberParser<Long> LONG = new LongParser();
    NumberParser<Short> SHORT = new ShortParser();
}

class ByteParser implements NumberParser<Byte> {
    @Override
    public Byte parse(Element element, String value) throws ParserException {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException ex) {
            return this.exception(element, value, "byte", ex);
        }
    }
}

class DoubleParser implements NumberParser<Double> {
    @Override
    public Double parse(Element element, String value) throws ParserException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return this.exception(element, value, "double", ex);
        }
    }
}

class FloatParser implements NumberParser<Float> {
    @Override
    public Float parse(Element element, String value) throws ParserException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException ex) {
            return this.exception(element, value, "float", ex);
        }
    }
}

class IntegerParser implements NumberParser<Integer> {
    @Override
    public Integer parse(Element element, String value) throws ParserException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return this.exception(element, value, "integer", ex);
        }
    }
}

class LongParser implements NumberParser<Long> {
    @Override
    public Long parse(Element element, String value) throws ParserException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return this.exception(element, value, "long", ex);
        }
    }
}

class ShortParser implements NumberParser<Short> {
    @Override
    public Short parse(Element element, String value) throws ParserException {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException ex) {
            return this.exception(element, value, "short", ex);
        }
    }
}
