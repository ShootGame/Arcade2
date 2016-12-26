package pl.themolka.arcade.xml;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import pl.themolka.arcade.ArcadePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SettingsFile {
    public static final String DEFAULT_FILE = "settings.xml";

    private final ArcadePlugin plugin;

    private final File defaultFile;

    public SettingsFile(ArcadePlugin plugin) {
        this.plugin = plugin;

        this.defaultFile = new File(plugin.getDataFolder(), DEFAULT_FILE);
    }

    public void copySettingsFile() throws IOException {
        this.copySettingsFile(false);
    }

    public void copySettingsFile(boolean force) throws IOException {
        this.copySettingsFile(this.defaultFile, force);
    }

    public void copySettingsFile(File file, boolean force) throws IOException {
        if (file.exists() && !force) {
            return;
        }

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(file.getName())) {
            Files.copy(input, file.toPath());
        }
    }

    public File getDefaultFile() {
        return this.defaultFile;
    }

    public void readSettingsFile() throws IOException, JDOMException {
        this.readSettingsFile(this.defaultFile);
    }

    public void readSettingsFile(File file) throws IOException, JDOMException {
        if (!file.exists()) {
            this.copySettingsFile(file, false);
        }

        Document document = new SAXBuilder().build(file);

    }
}
