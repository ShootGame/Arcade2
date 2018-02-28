package pl.themolka.arcade.module;

import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Node;

import java.util.List;

public class SimpleModuleListener implements ModuleListener {
    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        return null;
    }

    public void onEnable(Node options) throws DOMException {
        this.onEnable();
    }
}
