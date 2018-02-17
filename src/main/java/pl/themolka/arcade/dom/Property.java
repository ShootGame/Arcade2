package pl.themolka.arcade.dom;

public class Property implements NamedValue {
    private String name;
    private String value;

    private Property(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean hasValue() {
        return this.value != null;
    }

    @Override
    public String setName(String name) {
        String oldName = this.name;
        if (name != null) {
            this.name = name;
        }

        return oldName;
    }

    @Override
    public String setValue(String value) {
        String oldValue = this.value;
        this.value = value;

        return oldValue;
    }

    //
    // Instancing
    //

    public static Property of(String name) {
        return new Property(name);
    }

    public static Property of(String name, String value) {
        Property property = of(name);
        property.setValue(value);
        return property;
    }
}
