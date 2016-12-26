package pl.themolka.arcade.xml;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import pl.themolka.arcade.module.Module;

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

    public Module<?> getModule(Element module) throws ReflectiveOperationException {
        String clazzPath = module.getAttributeValue("class");

        Class<?> clazz = Class.forName(clazzPath);
        if (clazz.isAssignableFrom(Module.class)) {
            return (Module<?>) clazz.newInstance();
        }

        return null;
    }

    public List<Module<?>> getModules(Element parent) {
        List<Module<?>> modules = new ArrayList<>();

        for (Element xml : parent.getChildren()) {
            try {
                Module<?> module = this.getModule(xml);
                if (module != null) {
                    modules.add(module);
                }
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }

        return modules;
    }
}
