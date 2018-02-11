package pl.themolka.arcade.kit;

public interface NoResultKitContent extends KitContent<Void> {
    @Override
    default Void getResult() {
        return null;
    }
}
