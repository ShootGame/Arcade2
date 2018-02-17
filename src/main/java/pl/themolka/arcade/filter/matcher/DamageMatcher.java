package pl.themolka.arcade.filter.matcher;

import org.bukkit.event.entity.EntityDamageEvent;
import pl.themolka.arcade.filter.FilterResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MatcherId("damage")
public class DamageMatcher extends Matcher {
    private final List<EntityDamageEvent.DamageCause> container = new ArrayList<>();

    public DamageMatcher(Collection<EntityDamageEvent.DamageCause> container) {
        this.container.addAll(container);
    }

    @Override
    public FilterResult matches(Object object) {
        if (object instanceof EntityDamageEvent) {
            return this.of(this.matches((EntityDamageEvent) object));
        } else if (object instanceof EntityDamageEvent.DamageCause) {
            return this.of(this.matches((EntityDamageEvent.DamageCause) object));
        }

        return this.abstain();
    }

    public boolean matches(EntityDamageEvent event) {
        return this.matches(event.getCause());
    }

    public boolean matches(EntityDamageEvent.DamageCause damage) {
        for (EntityDamageEvent.DamageCause value : this.getContainer()) {
            if (value.equals(damage)) {
                return true;
            }
        }

        return false;
    }

    public List<EntityDamageEvent.DamageCause> getContainer() {
        return this.container;
    }
}
