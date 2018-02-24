package pl.themolka.arcade.module;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.JDOMEngine;
import pl.themolka.arcade.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModulesFile {
    public static final String DEFAULT_FILENAME = "modules.xml";

    private final Document document;

    public ModulesFile(InputStream input) throws DOMException, IOException {
        this.document = JDOMEngine.getDefaultEngine().read(input);
    }

    public Document getDocument() {
        return this.document;
    }

    public List<Module<?>> getModules() {
        return this.getModules(this.getDocument());
    }

    public List<Module<?>> getModules(Document document) {
        return this.getModules(document.getRoot());
    }

    public List<Module<?>> getModules(Node parent) {
        List<Module<?>> modules = new ArrayList<>();

        for (Node node : parent.children("module")) {
            try {
                Module<?> module = (Module<?>) Class.forName(node.propertyValue("class")).newInstance();
                if (module != null) {
                    modules.add(module);
                }
            } catch (ClassCastException cast) {
                throw new RuntimeException("module class does not inherit the " + Module.class.getName());
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

        return modules;
    }
}
