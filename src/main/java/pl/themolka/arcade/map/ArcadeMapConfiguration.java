package pl.themolka.arcade.map;

import org.jdom2.Element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ArcadeMapConfiguration {
    private final Map<String, Element> elementsById = new HashMap<>();
    private final Element root;

    public ArcadeMapConfiguration(Element root) throws MapParserException {
        this.root = root;

        this.preprocess("<" + root.getName() + ">", root);
    }

    public boolean addElement(String id, Element element, boolean override) {
        if (this.hasElement(id) && !override) {
            return false;
        }

        this.elementsById.put(id, element);
        return true;
    }

    public Element getElement(String id) {
        return this.elementsById.get(id);
    }

    public Set<String> getElementIds() {
        return this.elementsById.keySet();
    }

    public Collection<Element> getElements() {
        return this.elementsById.values();
    }

    public Element getRoot() {
        return this.root;
    }

    public boolean hasElement(String id) {
        return this.elementsById.containsKey(id);
    }

    private void preprocess(String path, Element parent) throws MapParserException {
        for (Element child : parent.getChildren()) {
            String newPath = path += "/<" + child.getName() + ">";

            this.preprocessElement(newPath, child);
            this.preprocess(newPath, child);
        }
    }

    private void preprocessElement(String path, Element element) throws MapParserException {
        String uniqueId = element.getAttributeValue("id");
        if (uniqueId != null && !this.addElement(uniqueId, element, false)) {
            throw new MapParserException("Given ID already exists in " + path);
        }
    }
}
