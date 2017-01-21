package pl.themolka.arcade.repository;

import pl.themolka.arcade.command.Command;
import pl.themolka.arcade.command.CommandContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class RepoFile {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    private final List<Repository> children = new ArrayList<>();
    private final List<String> exclude = new ArrayList<>();
    private ExcludeState excludeState = ExcludeState.EXCLUDE;
    private final RepoFileQueries queries;
    private RepositoryParser<?> parser;

    public RepoFile(RepositoriesModule module) {
        this.queries = new RepoFileQueries(module.getPlugin(), this, module);
    }

    public boolean addChild(Repository repository) {
        return this.children.add(repository);
    }

    public List<Repository> getChildren() {
        return this.children;
    }

    public List<String> getExclude() {
        return this.exclude;
    }

    public ExcludeState getExcludeState() {
        return this.excludeState;
    }

    public RepositoryParser<?> getParser() {
        return this.parser;
    }

    public RepoFileQueries getQueries() {
        return this.queries;
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

    public void setExclude(List<String> exclude) {
        this.exclude.clear();
        this.exclude.addAll(exclude);
    }

    public void setExcludeState(ExcludeState excludeState) {
        this.excludeState = excludeState;
    }

    public void setParser(RepositoryParser<?> parser) {
        this.parser = parser;
    }

    public static RepoFile read(RepositoriesModule module, String[] file) {
        RepoFile repo = new RepoFile(module);
        for (int i = 0; i < file.length; i++) {
            String[] line = file[i].split(" ");

            String[] args = new String[0];
            if (line.length > 1) {
                System.arraycopy(line, 1, args, 0, line.length - 1);
            }

            Command command = repo.getQueries().getCommand(line[0]);
            if (command != null) {
                try {
                    CommandContext context = CommandContext.parse(command, line[0], args);
                    command.handleCommand(repo.getQueries().getConsole(), context);
                } catch (Throwable th) {
                    module.getPlugin().getLogger().log(Level.SEVERE, "Could not handle repo query: " + command.getCommand(), th);
                }
            }
        }

        return repo;
    }

    public enum ExcludeState {
        EXCLUDE, INCLUDE
    }
}
