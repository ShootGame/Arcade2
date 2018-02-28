package pl.themolka.arcade.environment;

import org.apache.commons.lang3.StringUtils;
import pl.themolka.arcade.development.Development;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Node;

public enum EnvironmentType {
    DEVELOPMENT {
        @Override
        public Environment buildEnvironment(Node options) throws DOMException {
            return new Development(options, this);
        }
    },

    PRODUCTION {
        @Override
        public Environment buildEnvironment(Node options) throws DOMException {
            return new Production(options, this);
        }
    },
    ;

    public abstract Environment buildEnvironment(Node options) throws DOMException;

    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase().replace("_", " "));
    }
}
