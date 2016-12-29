package pl.themolka.arcade.settings;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import pl.themolka.arcade.ArcadePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Settings {
    public static final String DEFAULT_FILE = "settings.xml";

    private final ArcadePlugin plugin;

    private Document document;
    private final File file;

    public Settings(ArcadePlugin plugin) {
        this(plugin, new File(plugin.getDataFolder(), DEFAULT_FILE));
    }

    public Settings(ArcadePlugin plugin, File file) {
        this.plugin = plugin;

        this.file = file;
    }

    public void copySettingsFile() throws IOException {
        this.copySettingsFile(false);
    }

    public void copySettingsFile(boolean force) throws IOException {
        this.copySettingsFile(this.file, force);
    }

    public void copySettingsFile(File file, boolean force) throws IOException {
        if (file.exists() && !force) {
            return;
        }

        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdir();
        } else if (force) {
            file.delete();
        }

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(file.getName())) {
            Files.copy(input, file.toPath());
        }
    }

    public Element getData() {
        return this.getDocument().getRootElement();
    }

    public Document getDocument() {
        return this.document;
    }

    public File getFile() {
        return this.file;
    }

    public Document readSettingsFile() throws IOException, JDOMException {
        return this.readSettingsFile(this.file);
    }

    public Document readSettingsFile(File file) throws IOException, JDOMException {
        if (!file.exists()) {
            this.copySettingsFile(file, false);
        }

        SAXBuilder builder = new SAXBuilder();
        return builder.build(file);
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
