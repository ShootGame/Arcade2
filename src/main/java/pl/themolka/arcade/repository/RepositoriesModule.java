package pl.themolka.arcade.repository;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.SimpleGlobalModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ModuleInfo(id = "repositories")
public class RepositoriesModule extends SimpleGlobalModule {
    private final Map<String, RepositoryParser<?>> parsers = new HashMap<>();

    @Override
    public void onEnable(Element global) throws JDOMException {
        this.addDefaultParsers();
    }

    public void addParser(RepositoryParser<?> parser) {
        this.parsers.put(parser.getId(), parser);
    }

    public RepositoryParser<?> getParser(String id) {
        return this.parsers.get(id);
    }

    public Set<String> getParserIds() {
        return this.parsers.keySet();
    }

    public Collection<RepositoryParser<?>> getParsers() {
        return this.parsers.values();
    }

    public boolean hasParser(RepositoryParser<?> parser) {
        return this.hasParser(parser.getId());
    }

    public boolean hasParser(String id) {
        return this.parsers.containsKey(id);
    }

    public Repository parse(String id, RepoFile info) {
        if (this.hasParser(id)) {
            return (Repository) this.getParser(id).parse(info);
        }

        return null;
    }

    public void removeParser(RepositoryParser<?> parser) {
        this.removeParser(parser.getId());
    }

    public void removeParser(String id) {
        this.parsers.remove(id);
    }

    private void addDefaultParsers() {

    }
}
