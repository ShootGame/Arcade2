package pl.themolka.arcade.filter.matcher;

import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.Objects;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@MatcherId("damage")
public class DamageMatcher extends Matcher {
    public static final MatcherParser PARSER = new DamageParser();

    private final DamageCause cause;

    public DamageMatcher(DamageCause cause) {
        this.cause = cause;
    }

    @Override
    public FilterResult matches(Object object) {
        if (object instanceof EntityDamageEvent) {
            return this.of(this.matches((EntityDamageEvent) object));
        } else if (object instanceof DamageCause) {
            return this.of(this.matches((DamageCause) object));
        }

        return this.abstain();
    }

    public boolean matches(EntityDamageEvent event) {
        return this.matches(event.getCause());
    }

    public boolean matches(DamageCause cause) {
        return Objects.equals(this.cause, cause);
    }
}

class DamageParser implements MatcherParser<DamageMatcher> {
    @Override
    public DamageMatcher parsePrimitive(Node node, ParserContext context) throws ParserException, ParserNotSupportedException {
        return new DamageMatcher(context.type(DamageCause.class).parse(node).orFail());
    }
}
