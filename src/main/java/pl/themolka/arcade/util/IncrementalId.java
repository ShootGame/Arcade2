package pl.themolka.arcade.util;

public interface IncrementalId extends Identifiable<Integer> {
    int getId();

    @Override
    default Integer getIdentifier() {
        return this.getId();
    }
}
