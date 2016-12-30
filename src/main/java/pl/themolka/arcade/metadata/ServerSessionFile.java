package pl.themolka.arcade.metadata;

import pl.themolka.arcade.ArcadePlugin;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ServerSessionFile {
    public static final String DEFAULT_FILENAME = "server-session.json";

    private final ArcadePlugin plugin;

    private SerializedServerSession content = new SerializedServerSession();
    private File file;

    public ServerSessionFile(ArcadePlugin plugin) {
        this(plugin, new File(plugin.getDataFolder(), DEFAULT_FILENAME));
    }

    public ServerSessionFile(ArcadePlugin plugin, File file) {
        this.plugin = plugin;

        this.file = file;
    }

    public SerializedServerSession getContent() {
        return this.content;
    }

    public File getFile() {
        return this.file;
    }

    public void setContent(SerializedServerSession content) {
        this.content = content;
    }

    public void setFile(File file) {
        this.file = file;
    }

    //
    // Serializing methods
    //

    public void deserialize() throws IOException {
        this.setContent(this.plugin.deserializeJsonFile(this.getFile(), SerializedServerSession.class));
    }

    public void serialize() throws IOException {
        this.plugin.serializeJsonFile(this.getFile(), this.getContent());
    }

    public static class SerializedServerSession implements Serializable {
        private int lastGameId;

        public int getLastGameId() {
            return this.lastGameId;
        }

        public void setLastGameId(int lastGameId) {
            this.lastGameId = lastGameId;
        }
    }
}
