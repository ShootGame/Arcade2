package pl.themolka.arcade.map;

import pl.themolka.arcade.module.Module;

public class MapParserException extends Exception {
    private Module<?> module;

    public MapParserException() {
        super();
    }

    public MapParserException(Module<?> module) {
        super();

        this.module = module;
    }

    public MapParserException(String message) {
        super(message);
    }

    public MapParserException(Module<?> module, String message) {
        super(message);

        this.module = module;
    }

    public MapParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapParserException(Module<?> module, String message, Throwable cause) {
        super(message, cause);

        this.module = module;
    }

    public MapParserException(Throwable cause) {
        super(cause);
    }

    public MapParserException(Module<?> module, Throwable cause) {
        super(cause);

        this.module = module;
    }

    public Module<?> getModule() {
        return this.module;
    }

    public boolean hasModule() {
        return this.module != null;
    }
}
