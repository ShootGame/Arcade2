package pl.themolka.arcade.kit.content;

public interface BaseVoidKitContent extends KitContent<Void> {
    @Override
    default Void getResult() {
        return null;
    }
}
