package pl.themolka.arcade.repository;

public interface Repository {
    RepositoryItem[] getContent();

    RepoFile getInfo();

    void load(String[] content) throws RepositoryException;
}
