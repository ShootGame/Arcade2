package pl.themolka.arcade.kit;

public interface BaseVoidKitContent extends KitContent<Void> {
    @Override
    default Void getResult() {
        return null;
    }
}
