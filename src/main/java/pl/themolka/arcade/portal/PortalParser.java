package pl.themolka.arcade.portal;

import org.bukkit.entity.Player;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.RegionParser;
import pl.themolka.arcade.region.UnionRegion;
import pl.themolka.arcade.spawn.Direction;
import pl.themolka.arcade.spawn.SmoothSpawnAgent;
import pl.themolka.arcade.spawn.Spawn;
import pl.themolka.arcade.spawn.SpawnAgent;
import pl.themolka.arcade.spawn.SpawnApply;

import java.util.Collections;
import java.util.Set;

@Produces(Portal.Config.class)
public class PortalParser extends ConfigParser<Portal.Config>
                          implements InstallableParser {
    private Parser<Ref> destinationParser;
    private Parser<Direction> yawDirectionParser;
    private Parser<Direction> pitchDirectionParser;
    private Parser<Ref> filterParser;
    private Parser<Ref> kitParser;
    private Parser<UnionRegion.Config> regionParser;
    private Parser<Boolean> smoothParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.destinationParser = context.type(Ref.class);
        this.yawDirectionParser = context.type(Direction.class);
        this.pitchDirectionParser = context.type(Direction.class);
        this.filterParser = context.type(Ref.class);
        this.kitParser = context.type(Ref.class);
        this.regionParser = context.of(RegionParser.Union.class);
        this.smoothParser = context.type(Boolean.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("portal");
    }

    @Override
    protected ParserResult<Portal.Config> parseNode(Node node, String name, String value) throws ParserException {
        SpawnApply destination = this.parseDestination(node, name, value);
        Ref<Filter> filter = this.filterParser.parse(node.property("filter")).orDefault(Ref.empty());
        String id = this.parseOptionalId(node);
        Ref<Kit> kit = this.kitParser.parse(node.property("kit")).orDefault(Ref.empty());
        AbstractRegion.Config<?> region = this.regionParser.parseWithDefinition(node, name, value).orFail();

        return ParserResult.fine(node, name, value, new Portal.Config() {
            public SpawnApply destination() { return destination; }
            public Ref<Filter> filter() { return filter; }
            public String id() { return id; }
            public Ref<Kit> kit() { return kit; }
            public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
        });
    }

    protected SpawnApply parseDestination(Node node, String name, String value) throws ParserException {
        Spawn destination = (Spawn) this.destinationParser.parse(node.property("destination")).orFail().get();
        boolean smooth = this.smoothParser.parse(node.property("smooth")).orDefault(false);

        Direction yaw = this.yawDirectionParser.parse(node.property("yaw")).orDefault(Direction.ENTITY);
        Direction pitch = this.pitchDirectionParser.parse(node.property("pitch")).orDefault(Direction.ENTITY);

        return new SpawnApply(destination, new SpawnApply.AgentFactory() {
            @Override
            public SpawnAgent createAgent(Spawn spawn, GamePlayer player, Player bukkit) {
                if (smooth) {
                    return SmoothSpawnAgent.create(spawn, player.getBukkit(), yaw, pitch);
                } else {
                    return SpawnAgent.create(spawn, player.getBukkit(), yaw, pitch);
                }
            }
        });
    }
}
