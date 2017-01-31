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
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.generator.Generator;
import pl.themolka.arcade.generator.XMLGenerator;
import pl.themolka.arcade.xml.XMLDifficulty;
import pl.themolka.arcade.xml.XMLEnvironment;
import pl.themolka.arcade.xml.XMLLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XMLMapParser implements MapParser {
    public static final String XML_DEFAULT_FILENAME = "map.xml";

    private Document document;

    @Override
    public void readFile(File file) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(file);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }
    }

    @Override
    public void readStream(InputStream input) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(input);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }
    }

    @Override
    public void readReader(Reader reader) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(reader);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }
    }

    @Override
    public void readUrl(URL url) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(url);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }
    }

    public Document getDocument() {
        return this.document;
    }

    //
    // Parsing OfflineMap object
    //

    @Override
    public OfflineMap parseOfflineMap(ArcadePlugin plugin) throws MapParserException {
        Element root = this.getDocument().getRootElement();

        String name = this.parseName(root.getChildTextNormalize("name"));
        MapVersion version = this.parseVersion(root.getChildTextNormalize("version"));
        String description = this.parseDescription(root.getChildTextNormalize("description"));
        List<Author> authors = this.parseAuthors(root.getChild("authors"));

        if (description == null) {
            description = this.parseDescription(root.getChildTextNormalize("objective"));
        }

        return new OfflineMap(name, version, description, authors);
    }

    private String parseName(String name) throws MapParserException {
        if (name == null) {
            throw new MapParserException("<name> not given");
        } else if (name.length() > OfflineMap.NAME_MAX_LENGTH) {
            throw new MapParserException("<name> too long (longer than " + OfflineMap.NAME_MAX_LENGTH + " characters)");
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
        if (username.isEmpty()) {
            return null;
        }

        String description = author.getAttributeValue("description");
        String contribution = author.getAttributeValue("contribution");
        if (description != null) {
            description = description.trim();
        } else if (contribution != null) {
            description = contribution.trim();
        }

        return new Author(uuid, username, description);
    }

    //
    // Parsing ArcadeMap object
    //

    @Override
    public ArcadeMap parseArcadeMap(ArcadePlugin plugin, OfflineMap offline) throws MapParserException {
        Element root = this.getDocument().getRootElement();

        Element world = root.getChild("world");
        if (world == null) {
            world = new Element("world");
        }

        Element scoreboard = root.getChild("scoreboard");
        if (scoreboard == null) {
            scoreboard = new Element("scoreboard");
        }

        ArcadeMap map = new ArcadeMap(offline);
        map.setConfiguration(new ArcadeMapConfiguration(root));
        map.setDifficulty(this.parseDifficulty(world));
        map.setEnvironment(this.parseEnvironment(world));
        map.setGenerator(this.parseGenerator(world, plugin, map));
        map.setScoreboardTitle(this.parseScoreboardTitle(scoreboard));
        map.setSeed(this.parseSeed(world));
        map.setSpawn(this.parseSpawn(world));
        map.setPvp(this.parsePvp(world));

        WorldNameGenerator generator = new WorldNameGenerator(map);
        map.setWorldName(generator.nextWorldName());

        return map;
    }

    private Difficulty parseDifficulty(Element parent) {
        if (parent != null) {
            return XMLDifficulty.parse(parent, Difficulty.PEACEFUL);
        }

        return null;
    }

    private World.Environment parseEnvironment(Element parent) {
        if (parent != null) {
            return XMLEnvironment.parse(parent, World.Environment.NORMAL);
        }

        return null;
    }

    private Generator parseGenerator(Element parent, ArcadePlugin plugin, ArcadeMap map) throws MapParserException {
        if (parent != null) {
            Element xml = parent.getChild("generator");
            if (xml != null) {
                return XMLGenerator.parse(xml, plugin, map);
            }
        }

        return null;
    }

    private String parseScoreboardTitle(Element parent) {
        if (parent != null) {
            Element xml = parent.getChild("title");
            if (xml != null) {
                return xml.getTextNormalize();
            }
        }

        return null;
    }

    private long parseSeed(Element parent) throws MapParserException {
        if (parent != null) {
            try {
                Element xml = parent.getChild("generator");
                if (xml != null) {
                    Attribute seed = xml.getAttribute("seed");
                    if (seed != null) {
                        return seed.getLongValue();
                    }
                }
            } catch (DataConversionException ignored) {
            }
        }

        return ArcadeMap.DEFAULT_SEED;
    }

    private Location parseSpawn(Element parent) throws MapParserException {
        Element spawn = parent.getChild("spawn");
        if (spawn == null) {
            return new Location((World) null, XMLLocation.X, XMLLocation.Y, XMLLocation.Z);
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
        public String getDefaultFilename() {
            return XML_DEFAULT_FILENAME;
        }

        @Override
        public MapParser newInstance() {
            return new XMLMapParser();
        }
    }
}
