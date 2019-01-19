/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.util;

import pl.themolka.arcade.dom.Document;
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

    private final Node emptyRoot = Node.empty();
    private final Document document;

    public ManifestFile(Document document) {
        this.document = document;
    }

    public Node getContent() {
        return this.document.hasRoot() ? this.document.getRoot() : this.emptyRoot;
    }

    public String getFieldName() {
        return this.getContent().propertyValue(FIELD_NAME);
    }

    public String getFieldGroupId() {
        return this.getContent().propertyValue(FIELD_GROUP_ID);
    }

    public String getFieldArtifactId() {
        return this.getContent().propertyValue(FIELD_ARTIFACT_ID);
    }

    public String getFieldVersion() {
        return this.getContent().propertyValue(FIELD_VERSION);
    }

    public String getFieldMcVersion() {
        return this.getContent().propertyValue(FIELD_MC_VERSION);
    }

    public String getFieldMainClass() {
        return this.getContent().propertyValue(FIELD_MAIN_CLASS);
    }

    public String getFieldGitCommit() {
        return this.getContent().propertyValue(FIELD_GIT_COMMIT);
    }
}
