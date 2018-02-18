package pl.themolka.arcade.dom;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Something that can hold properties.
 */
public interface Propertable {
    boolean hasProperties();

    List<Property> properties();

    List<Property> properties(Iterable<String> names);

    default List<Property> properties(String... names) {
        return names != null ? this.properties(Arrays.asList(names))
                             : null;
    }

    Property property(Iterable<String> names);

    default Property property(String... names) {
        return names != null ? this.property(Arrays.asList(names))
                             : null;
    }

    String propertyValue(Iterable<String> names);

    default String propertyValue(String... names) {
        return names != null ? this.propertyValue(Arrays.asList(names))
                             : null;
    }

    String propertyValue(Iterable<String> names, String def);

    Property setProperty(Property property);

    default Property setProperty(String name, String value) {
        return name != null ? this.setProperty(Property.of(name, value))
                            : null;
    }

    void setProperties(Iterable<Property> properties);

    default void setProperties(Property... properties) {
        if (properties != null) {
            this.setProperties(Arrays.asList(properties));
        }
    }

    void sortProperties(Comparator<? super Property> comparator);

    int unsetProperties();

    boolean unsetProperty(Property property);

    boolean unsetProperty(String name);
}
