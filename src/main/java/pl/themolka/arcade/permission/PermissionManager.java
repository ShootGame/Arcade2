package pl.themolka.arcade.permission;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import pl.themolka.arcade.ArcadePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PermissionManager {
    public static final String PERMISSIONS_FILE = "permissions.xml";

    private final ArcadePlugin plugin;

    private Group defaultGroup;
    private Document document;
    private final File file;
    private final Map<String, Group> groupsById = new HashMap<>();
    private ClientPermissionStorage permissionStorage;

    public PermissionManager(ArcadePlugin plugin) {
        this(plugin, new File(plugin.getDataFolder(), PERMISSIONS_FILE));
    }

    public PermissionManager(ArcadePlugin plugin, File file) {
        this.plugin = plugin;

        this.file = file;
    }

    public void addGroup(Group group) {
        this.groupsById.put(group.getId(), group);
    }

    public void clearGroups() {
        this.groupsById.clear();
    }

    public void copyPermissionsFile() throws IOException {
        this.copyPermissionsFile(false);
    }

    public void copyPermissionsFile(boolean force) throws IOException {
        this.copyPermissionsFile(this.file, force);
    }

    public void copyPermissionsFile(File file, boolean force) throws IOException {
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

    public Element getData() {
        return this.getDocument().getRootElement();
    }

    public Group getDefaultGroup() {
        return this.defaultGroup;
    }

    public Document getDocument() {
        return this.document;
    }

    public Group getGroup(String groupId) {
        return this.groupsById.get(groupId);
    }

    public Set<String> getGroupIds() {
        return this.groupsById.keySet();
    }

    public Collection<Group> getGroups() {
        return this.groupsById.values();
    }

    public ClientPermissionStorage getPermissionStorage() {
        return this.permissionStorage;
    }

    public boolean hasGroup(String groupId) {
        return this.getGroup(groupId) != null;
    }

    public Document readPermissionsFile() throws IOException, JDOMException {
        return this.readPermissionsFile(this.file);
    }

    public Document readPermissionsFile(File file) throws IOException, JDOMException {
        if (!file.exists()) {
            this.copyPermissionsFile(file, false);
        }

        SAXBuilder builder = new SAXBuilder();
        return builder.build(file);
    }

    public void removeGroup(Group group) {
        this.removeGroup(group.getId());
    }

    public void removeGroup(String groupId) {
        this.groupsById.remove(groupId);
    }

    public void setDefaultGroup(Group defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setPermissionStorage(ClientPermissionStorage permissionStorage) {
        this.permissionStorage = permissionStorage;
    }
}
