package pl.themolka.arcade.environment;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.devlopment.Development;
import pl.themolka.arcade.xml.XMLParser;

public enum EnvironmentType {
    DEVELOPMENT {
        @Override
        public Environment buildEnvironment(Element settings) throws JDOMException {
            return new Development(settings, this);
        }
    },

    PRODUCTION {
        @Override
        public Environment buildEnvironment(Element settings) throws JDOMException {
            return new Production(settings, this);
        }
    },
    ;

    public abstract Environment buildEnvironment(Element settings) throws JDOMException;

    public String prettyName() {
        return StringUtils.capitalize(this.name().toLowerCase().replace("_", " "));
    }

    public String toPrettyString() {
        return this.prettyName();
    }

    public static EnvironmentType forName(String name) {
        if (name != null) {
            EnvironmentType environment = valueOf(XMLParser.parseEnumValue(name));
            if (environment != null) {
                return environment;
            }
        }

        return Environment.DEFAULT_TYPE;
    }
}
