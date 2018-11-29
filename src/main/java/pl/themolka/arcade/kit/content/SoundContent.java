/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.session.ArcadeSound;

public class SoundContent implements KitContent<Sound> {
    private final Sound result;
    private Location location;
    private float pitch;
    private float volume;

    protected SoundContent(Config config) {
        this.result = config.result().get();
        this.location = config.location().getIfPresent();
        this.pitch = config.pitch().get();
        this.volume = config.volume().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void apply(GamePlayer player) {
        Location playerLocation = player.getBukkit().getLocation();
        Location location = this.hasLocation() ? this.getLocation().clone()
                                               : playerLocation.clone();
        location.setWorld(playerLocation.getWorld());

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
            this.soundParser = context.type(Sound.class);
            this.locationParser = context.type(Location.class);
            this.pitchParser = context.type(Float.class);
            this.volumeParser = context.type(Float.class);
        }

        @Override
        protected Result<Config> parsePrimitive(Node node, String name, String value) throws ParserException {
            Sound sound = this.soundParser.parseWithDefinition(node, name, value).orFail();
            Location location = this.locationParser.parse(node.property("location", "at")).orDefaultNull();
            float pitch = this.pitchParser.parse(node.property("pitch")).orDefault(Config.DEFAULT_PITCH);
            float volume = this.volumeParser.parse(node.property("volume")).orDefault(Config.DEFAULT_VOLUME);

            return Result.fine(node, name, value, new Config() {
                public Ref<Sound> result() { return Ref.ofProvided(sound); }
                public Ref<Location> location() { return location != null ? Ref.ofProvided(location) : Ref.empty(); }
                public Ref<Float> pitch() { return Ref.ofProvided(pitch); }
                public Ref<Float> volume() { return Ref.ofProvided(volume); }
            });
        }
    }

    public interface Config extends KitContent.Config<SoundContent, Sound> {
        float DEFAULT_PITCH = ArcadeSound.DEFAULT_PITCH;
        float DEFAULT_VOLUME = ArcadeSound.DEFAULT_VOLUME;

        default Ref<Location> location() { return Ref.empty(); }
        default Ref<Float> pitch() { return Ref.ofProvided(DEFAULT_PITCH); }
        default Ref<Float> volume() { return Ref.ofProvided(DEFAULT_VOLUME); }

        @Override
        default SoundContent create(Game game, Library library) {
            return new SoundContent(this);
        }
    }
}
