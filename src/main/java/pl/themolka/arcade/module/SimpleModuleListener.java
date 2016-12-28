package pl.themolka.arcade.module;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import java.util.List;

public class SimpleModuleListener implements ModuleListener {
    @Override
    public final void onEnable() {
        throw new UnsupportedOperationException("Not supported here!");
    }

    @Override
    public void onDisable() {
    }

    @Override
    public List<Object> onListenersRegister(List<Object> register) {
        return null;
    }

    public void onEnable(Element global) throws JDOMException {
    }
}
