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

package pl.themolka.arcade.map;

import pl.themolka.arcade.util.versioning.SemanticVersion;

public class MapFileVersion extends SemanticVersion {
    public MapFileVersion(int major, int minor) {
        super(major, minor);
    }

    public MapFileVersion(int major, int minor, boolean pre) {
        super(major, minor, pre);
    }

    public MapFileVersion(int major, int minor, int patch) {
        super(major, minor, patch);
    }

    public MapFileVersion(int major, int minor, int patch, boolean pre) {
        super(major, minor, patch, pre);
    }

    @Override
    public SemanticVersion previous() throws NoPreviousException {
        throw new NoPreviousException();
    }

    @Override
    public SemanticVersion next() throws NoNextException {
        throw new NoNextException();
    }

    public static MapFileVersion convertFrom(SemanticVersion semantic) {
        return new MapFileVersion(semantic.getMajor(), semantic.getMinor(), semantic.getPatch(), semantic.isPre());
    }
}
