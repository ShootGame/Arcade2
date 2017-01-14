package pl.themolka.arcade.util;

public interface StringId extends Identifiable<String> {
    String getId();

    @Override
    default String getIdentifier() {
        return this.getId();
    }
}
