package pl.themolka.arcade.repository;

import pl.themolka.arcade.util.StringId;

public interface RepositoryParser<T> extends StringId {
    T parse(RepoFile info);
}
