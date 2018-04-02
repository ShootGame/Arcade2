package pl.themolka.arcade.filter.matcher;

import org.bukkit.event.entity.CreatureSpawnEvent;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.Objects;

import static org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

@MatcherId("spawn-reason")
public class SpawnReasonMatcher extends Matcher {
    public static final MatcherParser PARSER = new SpawnReasonParser();

    private final SpawnReason reason;

    public SpawnReasonMatcher(SpawnReason reason) {
        this.reason = reason;
    }

    @Override
    public FilterResult matches(Object object) {
        if (object instanceof CreatureSpawnEvent) {
            return this.of(this.matches((CreatureSpawnEvent) object));
        } else if (object instanceof SpawnReason) {
            return this.of(this.matches((SpawnReason) object));
        }

        return this.abstain();
    }

    public boolean matches(CreatureSpawnEvent event) {
        return this.matches(event.getSpawnReason());
    }

    public boolean matches(SpawnReason reason) {
        return Objects.equals(this.reason, reason);
    }
}

class SpawnReasonParser implements MatcherParser<SpawnReasonMatcher> {
    @Override
    public SpawnReasonMatcher parsePrimitive(Node node, ParserContext context) throws ParserException, ParserNotSupportedException {
        return new SpawnReasonMatcher(context.type(SpawnReason.class).parse(node).orFail());
    }
}
