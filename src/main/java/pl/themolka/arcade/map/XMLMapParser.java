package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import pl.themolka.arcade.xml.parser.XMLDifficulty;
import pl.themolka.arcade.xml.parser.XMLEnvironment;
import pl.themolka.arcade.xml.parser.XMLLocation;

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

    public File getFile() {
        return this.file;
    }

    public Document getDocument() {
        return this.document;
    }

    //
    // Parsing OfflineMap object
    //

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

    private String parseName(String name) throws MapParserException {
        if (name == null) {
            throw new MapParserException("<name> not given");
        } else if (name.length() > OfflineMap.NAME_MAX_LENGTH) {
            throw new MapParserException("<name> too long");
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

    private Author parseAuthor(Element author) {
        UUID uuid = null;
        String uuidValue = author.getAttributeValue("uuid");
        if (uuidValue != null) {
            try {
                uuid = UUID.fromString(uuidValue);
            } catch (IllegalArgumentException ignored) {
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

    //
    // Parsing ArcadeMap object
    //

    @Override
    public ArcadeMap parseArcadeMap(OfflineMap offline) throws MapParserException {
        Element root = this.getDocument().getRootElement();
        Element world = root.getChild("world");

        ArcadeMap map = new ArcadeMap(offline);
        map.setConfiguration(root);
        map.setDifficulty(this.parseDifficulty(world));
        map.setEnvironment(this.parseEnvironment(world));
        map.setSpawn(this.parseSpawn(world));
        map.setPvp(this.parsePvp(world));

        WorldNameGenerator generator = new WorldNameGenerator(map);
        map.setWorldName(generator.nextWorldName());

        return map;
    }

    private Difficulty parseDifficulty(Element parent) {
        if (parent != null) {
            return XMLDifficulty.parse(parent);
        }

        return null;
    }

    private World.Environment parseEnvironment(Element parent) {
        if (parent != null) {
            return XMLEnvironment.parse(parent);
        }

        return null;
    }

    private Location parseSpawn(Element parent) throws MapParserException {
        if (parent == null) {
            throw new MapParserException("<world> not given");
        }

        Element spawn = parent.getChild("spawn");
        if (spawn == null) {
            throw new MapParserException("<spawn> in <world> not given");
        }

        try {
            return XMLLocation.parse(spawn);
        } catch (DataConversionException ex) {
            throw new MapParserException("<spawn> incorrect", ex);
        }
    }

    private boolean parsePvp(Element parent) {
        if (parent != null) {
            Attribute attribute = parent.getAttribute("pvp");
            if (attribute != null) {
                try {
                    return attribute.getBooleanValue();
                } catch (DataConversionException ignored) {
                }
            }
        }

        return true;
    }

    public static class XMLParserTechnology implements MapParser.Technology {
        @Override
        public MapParser newInstance() {
            return new XMLMapParser();
        }
    }
}
