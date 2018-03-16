package pl.themolka.arcade.map;

import net.engio.mbassy.listener.Handler;
import org.apache.commons.io.FileUtils;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameDestroyEvent;
import pl.themolka.arcade.game.GameDestroyedEvent;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.module.SimpleGlobalModule;

import java.io.File;
import java.io.IOException;

@ModuleInfo(id = "Archive")
public class ArchiveModule extends SimpleGlobalModule {
    public static final String DEFAULT_DIRECTORY_NAME = "./archive";

    private File directory;

    @Override
    public void onEnable(Node options) {
        if (options == null) {
            return;
        }

        Node directoryNode = options.firstChild("directory");
        if (directoryNode == null) {
            directoryNode = Node.ofPrimitive(null, "directory", DEFAULT_DIRECTORY_NAME);
        }

        File file = new File(directoryNode.getValue());
        if (!file.isDirectory()) {
            FileUtils.deleteQuietly(file);
        } else if (!file.exists()) {
            file.mkdir();
        }

        this.directory = file;
    }

    @Handler(priority = Priority.NORMAL)
    public void onGameDestroy(GameDestroyEvent event) {
        event.setSaveWorld(this.directory != null);
    }

    @Handler(priority = Priority.NORMAL)
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
