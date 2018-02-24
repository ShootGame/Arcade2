package pl.themolka.arcade.settings;

import org.apache.commons.io.FileUtils;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.JDOMEngine;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Settings {
    public static final String DEFAULT_FILE = "settings.xml";

    private final ArcadePlugin plugin;

    private Document document;
    private boolean enabled;
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
            FileUtils.deleteQuietly(file);
        }

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(file.getName())) {
            Files.copy(input, file.toPath());
        }
    }

    public Node getData() {
        return this.getDocument().getRoot();
    }

    public Document getDocument() {
        return this.document;
    }

    public File getFile() {
        return this.file;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Document readSettingsFile() throws DOMException, IOException {
        return this.readSettingsFile(this.file);
    }

    public Document readSettingsFile(File file) throws DOMException, IOException {
        if (!file.exists()) {
            this.copySettingsFile(file, false);
        }

        return JDOMEngine.getDefaultEngine().read(file);
    }

    public void setDocument(Document document) throws ParserException {
        this.document = document;

        Node root = document.getRoot();
        if (root != null) {
            this.setup(root);
        }
    }

    private void setup(Node root) throws ParserException {
        this.enabled = Parsers.booleanParser().parse(root.property("enable", "enabled")).orFail();
    }
}
