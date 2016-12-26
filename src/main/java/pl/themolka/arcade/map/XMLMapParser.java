package pl.themolka.arcade.map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XMLMapParser implements MapParser {
    private File file;
    private Document document;

    @Override
    public void readFile(File file) throws IOException, MapParserException {
        this.file = file;

        try {
            this.document = new SAXBuilder().build(file);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }
    }

    @Override
    public OfflineMap parseOfflineMap() throws MapParserException {
        Element root = this.getDocument().getRootElement();

        String name = this.parseName(root.getChildTextNormalize("name"));
        MapVersion version = this.parseVersion(root.getChildTextNormalize("version"));
        String description = this.parseDescription(root.getChildTextNormalize("description"));
        List<Author> authors = this.parseAuthors(root.getChild("authors"));

        OfflineMap map = new OfflineMap(name, version, description, authors);
        map.setDirectory(this.getFile().getParentFile());
        map.setSettings(this.getFile());

        return map;
    }

    @Override
    public ArcadeMap parseArcadeMap(OfflineMap offline) throws MapParserException {
        return null;
    }

    public File getFile() {
        return this.file;
    }

    public Document getDocument() {
        return this.document;
    }

    private String parseName(String name) throws MapParserException {
        if (name == null) {
            throw new MapParserException("map name not given");
        } else if (name.length() > OfflineMap.NAME_MAX_LENGTH) {
            throw new MapParserException("map name too long");
        }

        return name.trim();
    }

    private MapVersion parseVersion(String version) {
        if (version != null) {
            return MapVersion.valueOf(version.trim());
        }

        return null;
    }

    private String parseDescription(String description) {
        if (description != null) {
            return description.trim();
        }

        return null;
    }

    private Author parseAuthor(Element author) {
        UUID uuid = null;
        String uuidValue = author.getAttributeValue("uuid");
        if (uuidValue != null) {
            try {
                uuid = UUID.fromString(uuidValue);
            } catch (IllegalArgumentException ex) {
            }
        }

        String username = author.getTextTrim();
        if (username == null) {
            return null;
        }

        String description = author.getAttributeValue("description");
        if (description != null) {
            description = description.trim();
        }

        return new Author(uuid, username, description);
    }

    private List<Author> parseAuthors(Element parent) {
        List<Author> authors = new ArrayList<>();

        if (parent != null) {
            for (Element child : parent.getChildren("author")) {
                Author author = this.parseAuthor(child);
                if (author == null) {
                    continue;
                }

                authors.add(author);
            }
        }

        return authors;
    }
}
