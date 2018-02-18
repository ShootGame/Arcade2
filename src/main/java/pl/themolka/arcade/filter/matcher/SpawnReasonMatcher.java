package pl.themolka.arcade.filter.matcher;

import org.bukkit.event.entity.CreatureSpawnEvent;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.parser.EnumParser;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.Parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@MatcherId("spawn-reason")
public class SpawnReasonMatcher extends Matcher {
    public static final MatcherParser PARSER = new SpawnReasonParser();

    private final List<CreatureSpawnEvent.SpawnReason> container = new ArrayList<>();

    public SpawnReasonMatcher(Collection<CreatureSpawnEvent.SpawnReason> container) {
        this.container.addAll(container);
    }

    @Override
    public FilterResult matches(Object object) {
        if (object instanceof CreatureSpawnEvent) {
            return this.of(this.matches((CreatureSpawnEvent) object));
        } else if (object instanceof CreatureSpawnEvent.SpawnReason) {
            return this.of(this.matches((CreatureSpawnEvent.SpawnReason) object));
        }

        return this.abstain();
    }

    public boolean matches(CreatureSpawnEvent event) {
        return this.matches(event.getSpawnReason());
    }

    public boolean matches(CreatureSpawnEvent.SpawnReason reason) {
        return this.container.contains(reason);
    }
}

class SpawnReasonParser implements MatcherParser<SpawnReasonMatcher> {
    private final EnumParser<CreatureSpawnEvent.SpawnReason> reasonParser =
            Parsers.enumParser(CreatureSpawnEvent.SpawnReason.class);

    @Override
    public SpawnReasonMatcher parsePrimitive(Node node) throws ParserException {
        return new SpawnReasonMatcher(Collections.singleton(this.reasonParser.parse(node)));
    }
}
