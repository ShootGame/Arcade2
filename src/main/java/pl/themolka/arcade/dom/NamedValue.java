package pl.themolka.arcade.dom;

import java.util.Map;

public interface NamedValue extends Map.Entry<String, String> {
    /**
     * @deprecated Use {@link #getName()} instead.
     */
    @Override
    @Deprecated
    default String getKey() {
        return this.getName();
    }

    String getName();

    @Override
    String getValue();

    boolean hasValue();

    String setName(String name);

    @Override
    String setValue(String value);
}
