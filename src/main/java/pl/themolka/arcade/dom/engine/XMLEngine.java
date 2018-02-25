package pl.themolka.arcade.dom.engine;

import org.xml.sax.InputSource;
import pl.themolka.arcade.dom.DOMException;
import pl.themolka.arcade.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Base interface for all XML-oriented engines.
 */
public interface XMLEngine extends DOMEngine {
    @Override
    default Document read(InputStream stream) throws DOMException, IOException {
        return this.read(new InputSource(stream));
    }

    @Override
    default Document read(Reader reader) throws DOMException, IOException {
        return this.read(new InputSource(reader));
    }

    Document read(InputSource source) throws DOMException, IOException;
}
