package pl.themolka.arcade.map;

import java.io.File;
import java.io.IOException;

public interface MapParser {
    void readFile(File file) throws IOException, MapParserException;

    OfflineMap parseOfflineMap() throws MapParserException;

    ArcadeMap parseArcadeMap(OfflineMap offline) throws MapParserException;
}
