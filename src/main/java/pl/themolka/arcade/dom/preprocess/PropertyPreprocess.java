package pl.themolka.arcade.dom.preprocess;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.dom.Propertable;
import pl.themolka.arcade.dom.Property;

import java.util.List;

public abstract class PropertyPreprocess implements DefinedPreprocess<Property, Propertable>, Preprocess {
    @Override
    public List<Property> define(Propertable propertable) {
        return propertable.properties();
    }

    @Override
    public final void preprocess(Document document) throws PreprocessException {
        if (document.hasRoot()) {
            this.preprocess(document.getRoot());
        }
    }

    @Override
    public final void preprocess(Node node) throws PreprocessException {
        List<Property> definition = this.define(node);
        if (definition != null) {
            for (Property property : definition) {
                this.preprocess(property);
            }
        }
    }

    @Override
    public final void preprocess(Property property) throws PreprocessException {
        this.invoke(property);
    }
}
