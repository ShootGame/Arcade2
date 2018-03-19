package pl.themolka.arcade.kit.content;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.session.ArcadeSound;
import pl.themolka.arcade.xml.XMLLocation;
import pl.themolka.arcade.xml.XMLSound;

public class SoundContent implements KitContent<Sound> {
    public static final float DEFAULT_PITCH = ArcadeSound.DEFAULT_PITCH;
    public static final float DEFAULT_VOLUME = ArcadeSound.DEFAULT_VOLUME;

    private Location location;
    private float pitch = DEFAULT_PITCH;
    private final Sound result;
    private float volume = DEFAULT_VOLUME;

    public SoundContent(Sound result) {
        this.result = result;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        Location location = this.hasLocation() ? this.getLocation() : player.getBukkit().getLocation();
        player.getPlayer().play(this.getResult(), location, this.getVolume(), this.getPitch());
    }

    @Override
    public Sound getResult() {
        return this.result;
    }

    public Location getLocation() {
        return this.location;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getVolume() {
        return this.volume;
    }

    public boolean hasLocation() {
        return this.location != null;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public static class LegacyParser implements LegacyKitContentParser<SoundContent> {
        @Override
        public SoundContent parse(Element xml) throws DataConversionException {
            Sound sound = XMLSound.parse(xml.getValue());
            if (sound != null) {
                SoundContent content = new SoundContent(sound);
                content.setLocation(XMLLocation.parse(xml));

                Attribute pitch = xml.getAttribute("pitch");
                if (pitch != null) {
                    content.setPitch(pitch.getFloatValue());
                }

                Attribute volume = xml.getAttribute("volume");
                if (volume != null) {
                    content.setVolume(volume.getFloatValue());
                }

                return content;
            }

            return null;
        }
    }

    @NestedParserName({"sound", "play-sound", "playsound"})
    @Produces(SoundContent.class)
    public static class ContentParser extends BaseContentParser<SoundContent>
                                      implements InstallableParser {
        private Parser<Sound> soundParser;
        private Parser<Location> locationParser;
        private Parser<Float> pitchParser;
        private Parser<Float> volumeParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            this.soundParser = context.enumType(Sound.class);
            this.locationParser = context.type(Location.class);
            this.pitchParser = context.type(Float.class);
            this.volumeParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<SoundContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            SoundContent content = new SoundContent(this.soundParser.parse(node).orFail());
            content.setLocation(this.locationParser.parse(node.property("location", "at")).orDefaultNull());
            content.setPitch(this.pitchParser.parse(node.property("pitch")).orDefault(DEFAULT_PITCH));
            content.setVolume(this.volumeParser.parse(node.property("volume")).orDefault(DEFAULT_VOLUME));
            return ParserResult.fine(node, name, value, content);
        }
    }
}
