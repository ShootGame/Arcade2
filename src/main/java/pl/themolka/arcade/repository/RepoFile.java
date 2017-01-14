package pl.themolka.arcade.repository;

import java.util.ArrayList;
import java.util.List;

public class RepoFile {
    private final List<Repository> children = new ArrayList<>();
    private RepositoryParser<?> parser;

    public boolean addChild(Repository repository) {
        return this.children.add(repository);
    }

    public List<Repository> getChildren() {
        return this.children;
    }

    public RepositoryParser<?> getParser() {
        return this.parser;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public boolean hasParser() {
        return this.parser != null;
    }

    public Repository parseRepository() {
        if (this.hasParser()) {
            return (Repository) this.getParser().parse(this);
        }

        return null;
    }

    public boolean removeChild(Repository repository) {
        return this.children.remove(repository);
    }

    public void setParser(RepositoryParser<?> parser) {
        this.parser = parser;
    }
}
