package pl.themolka.arcade.module;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModulesFile {
    public static final String DEFAULT_FILENAME = "modules.xml";

    private final Element root;

    public ModulesFile(InputStream input) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        this.root = builder.build(input).getRootElement();
    }

    public Element getRoot() {
        return this.root;
    }

    public List<Module<?>> getModules() {
        return this.getModules(this.getRoot());
    }

    public List<Module<?>> getModules(Element parent) {
        List<Module<?>> modules = new ArrayList<>();

        for (Element xml : parent.getChildren("module")) {
            try {
                Module<?> module = (Module<?>) Class.forName(xml.getAttributeValue("class")).newInstance();
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
