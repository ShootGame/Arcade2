package pl.themolka.arcade.dom.engine;

import pl.themolka.arcade.dom.DOMException;

public class EngineNotInstalledException extends DOMException {
    public EngineNotInstalledException() {
        super(null);
    }

    public EngineNotInstalledException(String message) {
        super(null, message);
    }
}
