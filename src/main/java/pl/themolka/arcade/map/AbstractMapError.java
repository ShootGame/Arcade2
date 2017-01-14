package pl.themolka.arcade.map;

public abstract class AbstractMapError implements MapError {
    private final ArcadeMap map;

    public AbstractMapError(ArcadeMap map) {
        this.map = map;
    }

    @Override
    public ArcadeMap getMap() {
        return this.map;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.getProvider() != null) {
            builder.append(this.getProvider()).append(": ");
        }

        if (this.getError() != null) {
            builder.append(this.getError());
        } else {
            builder.append("unknown error");
        }

        return builder.toString();
    }
}
