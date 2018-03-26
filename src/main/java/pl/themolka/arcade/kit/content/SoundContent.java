package pl.themolka.arcade.kit.content;

import org.bukkit.Location;
import org.bukkit.Sound;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
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

public class SoundContent implements KitContent<Sound> {
    private final Sound result;
    private Location location;
    private float pitch;
    private float volume;

    protected SoundContent(Config config) {
        this.result = config.result().get();
        this.location = config.location();
        this.pitch = config.pitch();
        this.volume = config.volume();
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

    @NestedParserName({"sound", "play-sound", "playsound"})
    @Produces(Config.class)
    public static class ContentParser extends BaseContentParser<Config>
                                      implements InstallableParser {
        private Parser<Sound> soundParser;
        private Parser<Location> locationParser;
        private Parser<Float> pitchParser;
        private Parser<Float> volumeParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.soundParser = context.enumType(Sound.class);
            this.locationParser = context.type(Location.class);
            this.pitchParser = context.type(Float.class);
            this.volumeParser = context.type(Float.class);
        }

        @Override
        protected ParserResult<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            Sound sound = this.soundParser.parse(node).orFail();
            Location location = this.locationParser.parse(node.property("location", "at")).orDefault(Config.DEFAULT_LOCATION);
            float pitch = this.pitchParser.parse(node.property("pitch")).orDefault(Config.DEFAULT_PITCH);
            float volume = this.volumeParser.parse(node.property("volume")).orDefault(Config.DEFAULT_VOLUME);

            return ParserResult.fine(node, name, value, new Config() {
                public Ref<Sound> result() { return Ref.ofProvided(sound); }
                public Location location() { return location; }
                public float pitch() { return pitch; }
                public float volume() { return volume; }
            });
        }
    }

    public interface Config extends KitContent.Config<SoundContent, Sound> {
        Location DEFAULT_LOCATION = null;
        float DEFAULT_PITCH = ArcadeSound.DEFAULT_PITCH;
        float DEFAULT_VOLUME = ArcadeSound.DEFAULT_VOLUME;

        default Location location() { return DEFAULT_LOCATION; }
        default float pitch() { return DEFAULT_PITCH; }
        default float volume() { return DEFAULT_VOLUME; }

        @Override
        default SoundContent create(Game game) {
            return new SoundContent(this);
        }
    }
}
