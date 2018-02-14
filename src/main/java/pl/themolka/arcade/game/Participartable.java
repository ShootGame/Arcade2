package pl.themolka.arcade.game;

public interface Participartable {
    default boolean canParticipate() {
        return true;
    }

    boolean isParticipating();

    default void setParticipating(boolean participating) {
        throw new UnsupportedOperationException("Dynamic participation.");
    }
}
