package pl.themolka.arcade.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ManifestFile extends Properties {
    public static final String DEFAULT_FILE = "manifest.properties";

    public static final String FIELD_NAME = "name";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_ARTIFACT_ID = "artifactId";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_MC_VERSION = "mcVersion";
    public static final String FIELD_MAIN_CLASS = "mainClass";
    public static final String FIELD_GIT_COMMIT = "gitCommit";

    public String getFieldName() {
        return this.getProperty(FIELD_NAME);
    }

    public String getFieldGroupId() {
        return this.getProperty(FIELD_GROUP_ID);
    }

    public String getFieldArtifactId() {
        return this.getProperty(FIELD_ARTIFACT_ID);
    }

    public String getFieldVersion() {
        return this.getProperty(FIELD_VERSION);
    }

    public String getFieldMcVersion() {
        return this.getProperty(FIELD_MC_VERSION);
    }

    public String getFieldMainClass() {
        return this.getProperty(FIELD_MAIN_CLASS);
    }

    public String getFieldGitCommit() {
        return this.getProperty(FIELD_GIT_COMMIT);
    }

    public void readManifestFile() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE)) {
            this.load(input);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
