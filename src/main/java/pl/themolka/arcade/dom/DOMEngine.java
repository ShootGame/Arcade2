package pl.themolka.arcade.dom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

/**
 * Something that can read DOM content and convert them into {@link Document}s.
 */
public interface DOMEngine {
    default Document read(File file) throws DOMException, IOException {
        return this.read(file.getAbsoluteFile().toURI().toURL());
    }

    default Document read(InputStream stream) throws DOMException, IOException {
        return this.read(new InputStreamReader(stream));
    }

    Document read(Reader reader) throws DOMException, IOException;

    default Document read(String dom) throws DOMException, IOException {
        return this.read(new StringReader(dom));
    }

    default Document read(URL url) throws DOMException, IOException {
        return this.read(url.openStream());
    }
}
