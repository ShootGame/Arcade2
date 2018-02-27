package pl.themolka.arcade.map;

import pl.themolka.arcade.ArcadePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * Legacy {@link OfflineMap} and {@link ArcadeMap} parsing. This parser will be
 * replaced with two separate parsers - {@link OfflineMapParser} for
 * {@link OfflineMap} objects, and {@link MapManifestParser} for
 * {@link ArcadeMap} objects. The idea is to create independent parsers for
 * these two totally different classes.
 * @deprecated {@link OfflineMapParser} and {@link MapManifestParser}
 */
@Deprecated
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
