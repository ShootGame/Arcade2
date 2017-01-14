package pl.themolka.arcade.map;

import pl.themolka.arcade.ArcadePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

public interface MapParser {
    void readFile(File file) throws IOException, MapParserException;

    void readReader(Reader reader) throws IOException, MapParserException;

    void readStream(InputStream input) throws IOException, MapParserException;

    void readUrl(URL url) throws IOException, MapParserException;

    OfflineMap parseOfflineMap(ArcadePlugin plugin) throws MapParserException;

    ArcadeMap parseArcadeMap(ArcadePlugin plugin, OfflineMap offline) throws MapParserException;

    interface Technology {
        String getDefaultFilename();

        MapParser newInstance();
    }
}
