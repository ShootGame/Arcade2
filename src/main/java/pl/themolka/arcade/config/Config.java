package pl.themolka.arcade.config;

public interface Config<P extends Config> {
    P getParent();

    default boolean hasParent() {
        return this.getParent() != null;
    }
}
