package pl.themolka.arcade.map;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.GameDestroyEvent;
import pl.themolka.arcade.game.GameDestroyedEvent;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

import java.io.File;
import java.io.IOException;

@ModuleInfo(id = "archive")
public class ArchiveModule extends Module<Object> {
    public static final String DEFAULT_DIRECTORY_NAME = "./archive";

    private File directory;

    @Override
    public void onEnable(Element global) throws JDOMException {
        Element directoryElement = global.getChild("directory");
        if (directoryElement == null) {
            directoryElement = new Element("directory").setText(DEFAULT_DIRECTORY_NAME);
        }

        File file = new File(directoryElement.getTextNormalize());
        if (!file.isDirectory()) {
            FileUtils.deleteQuietly(file);
        } else if (!file.exists()) {
            file.mkdir();
        }

        this.directory = file;
    }

    @Subscribe
    public void onGameDestroy(GameDestroyEvent event) {
        event.setSaveWorld(this.directory != null);
    }

    @Subscribe
    public void onGameDestroyed(GameDestroyedEvent event) {
        if (this.directory == null) {
            return;
        }

        String worldName = event.getGame().getMap().getWorldName();
        File worldContainer = this.getPlugin().getMaps().getWorldContainer();
        File worldDirectory = new File(worldContainer, worldName);

        if (!worldDirectory.exists() || !worldDirectory.isDirectory()) {
            return;
        }

        File destination = new File(this.directory, worldName);
        if (destination.exists()) {
            FileUtils.deleteQuietly(destination);
        }

        try {
            FileUtils.copyDirectory(worldDirectory, destination);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
