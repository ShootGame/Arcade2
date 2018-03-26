package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;

public interface BaseVoidKitContent extends KitContent<Void> {
    @Override
    default Void getResult() {
        return null;
    }

    interface Config<T extends BaseVoidKitContent> extends KitContent.Config<T, Void> {
        default Ref<Void> result() { return Ref.empty(); }
    }
}
