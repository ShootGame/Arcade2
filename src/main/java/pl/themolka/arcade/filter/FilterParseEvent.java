package pl.themolka.arcade.filter;

import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Event;

/**
 * @deprecated Will be removed in the future.
 */
@Deprecated
public class FilterParseEvent extends Event {
    private final String name;
    private Filter result;
    private final Element xml;

    public FilterParseEvent(ArcadePlugin plugin, Element xml) {
        super(plugin);
        this.name = xml.getName().toLowerCase();
        this.xml = xml;
    }

    public String getName() {
        return this.name;
    }

    public Filter getResult() {
        return this.result;
    }

    public Element getXml() {
        return this.xml;
    }

    public boolean hasResult() {
        return this.result != null;
    }

    public void setResult(Filter result) {
        this.result = result;
    }
}
