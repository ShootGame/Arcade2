package pl.themolka.arcade.xml;

import org.jdom2.Attribute;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

/**
 * @deprecated Not used anymore. Will be removed in the near future.
 */
@Deprecated
public class XMLPreProcessor implements Runnable {
    private final ArcadePlugin plugin;

    private final Element element;
    private final Executor[] executors;
    private final Queue<Entry> queue = new LinkedList<>();

    public XMLPreProcessor(ArcadePlugin plugin, Element element) {
        this.plugin = plugin;

        this.element = element;
        this.executors = new Executor[] {
                new IncludeExecutor(plugin)
        };
    }

    @Override
    public void run() {
        for (Entry entry : this.getQueue()) {
            try {
                entry.getExecutor().process(this, entry, this.getElement());
            } catch (Throwable th) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not handle " + entry.getElement().toString(), th);
            }
        }
    }

    public Executor findExecutor(String elementName) {
        for (Executor executor : this.getExecutors()) {
            if (executor.elementName().equals(elementName)) {
                return executor;
            }
        }

        return null;
    }

    public Element getElement() {
        return this.element;
    }

    public Executor[] getExecutors() {
        return this.executors;
    }

    public Queue<Entry> getQueue() {
        return this.queue;
    }

    public void prepare() {
        for (Element child : this.getElement().getChildren()) {
            Executor executor = this.findExecutor(child.getName());
            if (executor != null) {
                Entry entry = new Entry(child, executor);
                this.getQueue().offer(entry);
            }
        }
    }

    public class Entry {
        private final Element element;
        private final Executor executor;

        private Entry(Element element, Executor executor) {
            this.element = element;
            this.executor = executor;
        }

        public Element getElement() {
            return this.element;
        }

        public Executor getExecutor() {
            return this.executor;
        }
    }

    public interface Executor {
        String elementName();

        void process(XMLPreProcessor xml, Entry entry, Element map) throws Throwable;
    }

    //
    // Executors
    //

    public static class IncludeExecutor implements Executor {
        private final ArcadePlugin plugin;

        public IncludeExecutor(ArcadePlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String elementName() {
            return "include";
        }

        @Override
        public void process(XMLPreProcessor xml, Entry entry, Element map) throws Throwable {
            Attribute typeAttribute = entry.getElement().getAttribute("type");
            Type type = Type.LOCAL; // default value
            if (typeAttribute != null) {
                Type parsedType = Type.valueOf(XMLParser.parseEnumValue(typeAttribute.getValue()));
                if (parsedType != null) {
                    type = parsedType;
                }
            }

            String path = entry.getElement().getValue();
            if (path.isEmpty()) {
                return;
            }

            Element include = type.process(this.plugin, entry.getElement(), path);
            if (include == null) {
                return;
            }

            for (Element rootChild : include.getChildren()) {
                this.attachElement(map, rootChild);
            }
        }

        private void attachElement(Element apply, Element from) {
            // attributes
            for (Attribute attribute : from.getAttributes()) {
                if (apply.getAttribute(attribute.getName()) == null) {
                    apply.setAttribute(attribute);
                }
            }

            // text
            if (!from.getValue().isEmpty()) {
                apply.setText(from.getValue());
            }

            // children
            for (Element element : from.getChildren()) {
                this.attachElement(apply, element.detach());
            }
        }

        private enum Type {
            GLOBAL {
                @Override
                public Element process(ArcadePlugin plugin, Element element, String path) throws Throwable {
                    return LOCAL.process(plugin, element, path);
                }
            },

            LOCAL {
                @Override
                public Element process(ArcadePlugin plugin, Element element, String path) throws Throwable {
                    File file = new File(path);
                    if (file.exists()) {
                        Element root = JDOM.from(plugin.getDomEngines().forFile(file).read(file)).getRootElement();

                        if (root != null) {
                            XMLPreProcessor handler = new XMLPreProcessor(plugin, root);
                            handler.prepare();

                            handler.run();
                            return handler.getElement();
                        }
                    }

                    return null;
                }
            },
            ;

            public abstract Element process(ArcadePlugin plugin, Element element, String path) throws Throwable;
        }
    }
}
