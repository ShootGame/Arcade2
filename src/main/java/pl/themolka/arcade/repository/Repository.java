package pl.themolka.arcade.repository;

public interface Repository {
    String[] getContent();

    RepoFile getInfo();

    RepositoryItem[] getRepoItems();

    void load(String content) throws RepositoryException;
}
