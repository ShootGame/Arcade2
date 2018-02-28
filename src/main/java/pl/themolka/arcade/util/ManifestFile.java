package pl.themolka.arcade.util;

import pl.themolka.arcade.dom.Document;
import pl.themolka.arcade.dom.ImmutableNode;
import pl.themolka.arcade.dom.Node;

public class ManifestFile {
    public static final String DEFAULT_FILE = "manifest.properties";

    public static final String FIELD_NAME = "name";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_ARTIFACT_ID = "artifactId";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_MC_VERSION = "mcVersion";
    public static final String FIELD_MAIN_CLASS = "mainClass";
    public static final String FIELD_GIT_COMMIT = "gitCommit";

    private final Node root = ImmutableNode.of("root");
    private final Document document;

    public ManifestFile(Document document) {
        this.document = document;
    }

    public Node getContent() {
        return this.document.hasRoot() ? this.document.getRoot() : this.root;
    }

    public String getFieldName() {
        return this.getContent().firstChildValue(FIELD_NAME);
    }

    public String getFieldGroupId() {
        return this.getContent().firstChildValue(FIELD_GROUP_ID);
    }

    public String getFieldArtifactId() {
        return this.getContent().firstChildValue(FIELD_ARTIFACT_ID);
    }

    public String getFieldVersion() {
        return this.getContent().firstChildValue(FIELD_VERSION);
    }

    public String getFieldMcVersion() {
        return this.getContent().firstChildValue(FIELD_MC_VERSION);
    }

    public String getFieldMainClass() {
        return this.getContent().firstChildValue(FIELD_MAIN_CLASS);
    }

    public String getFieldGitCommit() {
        return this.getContent().firstChildValue(FIELD_GIT_COMMIT);
    }
}
