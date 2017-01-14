package pl.themolka.arcade.repository;

public abstract class AbstractRepository implements Repository {
    private final RepoFile info;

    public AbstractRepository(RepoFile info) {
        this.info = info;
    }

    @Override
    public RepoFile getInfo() {
        return this.info;
    }
}
