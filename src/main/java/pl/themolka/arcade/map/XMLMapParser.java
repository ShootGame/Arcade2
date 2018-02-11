package pl.themolka.arcade.map;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.jdom2.Attribute;
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
import pl.themolka.arcade.xml.XMLParser;
import pl.themolka.arcade.xml.XMLPreProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XMLMapParser implements MapParser {
    public static final String XML_DEFAULT_FILENAME = "map.xml";

    private final ArcadePlugin plugin;

    private Document document;

    public XMLMapParser(ArcadePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void readFile(File file) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(file);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }

        this.preprocess();
    }

    @Override
    public void readStream(InputStream input) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(input);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }

        this.preprocess();
    }

    @Override
    public void readReader(Reader reader) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(reader);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }

        this.preprocess();
    }

    @Override
    public void readUrl(URL url) throws IOException, MapParserException {
        try {
            this.document = new SAXBuilder().build(url);
        } catch (JDOMException jdom) {
            throw new MapParserException("Failed to parse XML", jdom);
        }

        this.preprocess();
    }

    public Document getDocument() {
        return this.document;
    }

    public void preprocess() {
        XMLPreProcessor handler = new XMLPreProcessor(this.plugin, this.getDocument().getRootElement());
        handler.prepare();
//        handler.run();

        this.getDocument().detachRootElement();
        this.getDocument().setRootElement(handler.getElement());
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
        List<Changelog> changelogs = this.parseChangelog(root.getChild("changelog"));

        if (description == null) {
            description = this.parseDescription(root.getChildTextNormalize("objective"));
        }

        return new OfflineMap(name, version, description, authors, changelogs);
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

        String username = author.getValue();
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

        return Author.of(uuid, username, description);
    }

    private List<Changelog> parseChangelog(Element parent) {
        List<Changelog> changelogs = new ArrayList<>();
        if (parent != null) {
            for (Element version : parent.getChildren()) {
                Attribute versionAttribute = version.getAttribute("version");
                if (versionAttribute == null) {
                    continue;
                }

                MapVersion mapVersion = MapVersion.valueOf(versionAttribute.getValue());
                if (mapVersion == null) {
                    continue;
                }

                Changelog changelog = new Changelog(mapVersion);

                Attribute releaseAttribute = version.getAttribute("release");
                if (releaseAttribute != null) {
                    try {
                        LocalDate release = Changelog.parseRelease(releaseAttribute.getValue());
                        if (release != null) {
                            changelog.setRelease(release);
                        }
                    } catch (DateTimeParseException ignored) {
                    }
                }

                for (Element log : version.getChildren("log")) {
                    String value = log.getValue();
                    if (!value.isEmpty()) {
                        changelog.add(value);
                    }
                }

                changelogs.add(changelog);
            }
        }

        return changelogs;
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
        map.setPvp(this.parsePvp(world));
        map.setSeed(this.parseSeed(world));
        map.setSpawn(this.parseSpawn(world));
        map.setTime(this.parseTime(world));

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

    private Generator parseGenerator(Element parent, ArcadePlugin plugin, ArcadeMap map) throws MapParserException {
        if (parent != null) {
            Element xml = parent.getChild("generator");
            if (xml != null) {
                return XMLGenerator.parse(xml, plugin, map);
            }
        }

        return null;
    }

    private boolean parsePvp(Element parent) {
        return parent == null || XMLParser.parseBoolean(parent.getAttributeValue("pvp"), true);
    }

    private String parseScoreboardTitle(Element parent) {
        if (parent != null) {
            Element xml = parent.getChild("title");
            if (xml != null) {
                return XMLParser.parseMessage(xml.getValue());
            }
        }

        return null;
    }

    private long parseSeed(Element parent) throws MapParserException {
        if (parent != null) {
            Element xml = parent.getChild("generator");
            if (xml != null) {
                return XMLParser.parseLong(xml.getAttributeValue("seed"), ArcadeMap.DEFAULT_SEED);
            }
        }

        return ArcadeMap.DEFAULT_SEED;
    }

    private Location parseSpawn(Element parent) throws MapParserException {
        return XMLLocation.parse(parent.getChild("spawn"));
    }

    private MapTime parseTime(Element parent) {
        MapTime result = MapTime.defaultTime();

        if (parent != null) {
            Element xml = parent.getChild("time");
            if (xml != null) {
                result = this.parseTimeValue(xml.getValue(), result);
                result.setLocked(XMLParser.parseBoolean(xml.getAttributeValue("locked"), false));
            }
        }

        return result;
    }

    private MapTime parseTimeValue(String value, MapTime def) {
        if (value != null) {
            try {
                return MapTimeConstants.valueOf(XMLParser.parseEnumValue(value)).time();
            } catch (IllegalArgumentException notByName) {
            }

            try {
                return MapTime.ofTicks(XMLParser.parseInt(value, -1 /* -1 results outOfBounds */));
            } catch (IllegalArgumentException outOfBounds) {
            }
        }

        return def;
    }

    public static class XMLParserTechnology implements MapParser.Technology {
        private final ArcadePlugin plugin;

        public XMLParserTechnology(ArcadePlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String getDefaultFilename() {
            return XML_DEFAULT_FILENAME;
        }

        @Override
        public MapParser newInstance() {
            return new XMLMapParser(this.plugin);
        }
    }
}
