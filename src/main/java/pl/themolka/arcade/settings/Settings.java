package pl.themolka.arcade.settings;

import org.apache.commons.io.FileUtils;
import org.bukkit.util.Vector;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Settings {
    public static final String DEFAULT_FILE = "settings.xml";

    private final ArcadePlugin plugin;

    private final ParserContext context;
    private Document document;
    private boolean enabled;
    private final File file;
    private Path includeRepository;
    private Vector spawn;
    private Path worldContainer;

    public Settings(ArcadePlugin plugin) {
        this(plugin, new File(plugin.getDataFolder(), DEFAULT_FILE));
    }

    public Settings(ArcadePlugin plugin, File file) {
        this.plugin = plugin;

        this.context = plugin.getParsers().createContext();
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

    public Path getIncludeRepository() {
        return this.includeRepository;
    }

    public Vector getSpawn() {
        return this.spawn;
    }

    public Path getWorldContainer() {
        return this.worldContainer;
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

        return this.plugin.getDomEngines().forFile(file).read(file);
    }

    public void setDocument(Document document) throws ParserException, ParserNotSupportedException {
        this.document = document;

        Node root = document.getRoot();
        if (root != null) {
            this.setup(root);
        }
    }

    private void setup(Node root) throws ParserException, ParserNotSupportedException {
        this.enabled = this.context.type(Boolean.class).parse(root.property("enable", "enabled")).orFail();
        this.includeRepository = this.context.type(Path.class).parse(root.firstChild("include-repository")).orFail();
        this.spawn = this.context.type(Vector.class).parse(root.child("spawn")).orFail();
        this.worldContainer = this.context.type(Path.class).parse(root.child("world-container")).orFail();
    }
}
