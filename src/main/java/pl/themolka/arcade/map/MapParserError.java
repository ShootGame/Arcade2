package pl.themolka.arcade.map;

public class MapParserError extends AbstractMapError {
    private final MapParserException exception;

    public MapParserError(ArcadeMap map, MapParserException exception) {
        super(map);

        this.exception = exception;
    }

    @Override
    public String getError() {
        if (this.exception.hasModule()) {
            return this.exception.getModule().getId() + " - " + this.exception.getMessage();
        }

        return this.exception.getMessage();
    }

    @Override
    public String getProvider() {
        return this.exception.getClass().getSimpleName();
    }

    public MapParserException getException() {
        return this.exception;
    }
}
