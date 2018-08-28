package pl.themolka.arcade.objective;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.IRegionFieldStrategy;
import pl.themolka.arcade.region.RegionFieldStrategy;
import pl.themolka.arcade.region.tracker.PlayerTracker;

public abstract class Capture {
    private final IRegionFieldStrategy fieldStrategy;
    private final Filter filter;
    private final AbstractRegion region;

    public Capture(Game game, IGameConfig.Library library, Config config) {
        this.fieldStrategy = config.fieldStrategy().get();
        this.filter = Filters.secure(library.getOrDefine(game, config.filter().getIfPresent()));
        this.region = library.getOrDefine(game, config.region().get());
    }

    public boolean canCapture(Participator participator) {
        return participator != null && participator.isParticipating() && this.filter.filter(participator).isNotFalse();
    }

    public IRegionFieldStrategy getFieldStrategy() {
        return this.fieldStrategy;
    }

    public AbstractRegion getRegion() {
        return this.region;
    }

    public abstract PlayerTracker getTracker();

    public interface Config extends IGameConfig<Capture> {
        IRegionFieldStrategy DEFAULT_FIELD_STRATEGY = RegionFieldStrategy.EXACT;

        default Ref<IRegionFieldStrategy> fieldStrategy() { return Ref.ofProvided(DEFAULT_FIELD_STRATEGY); }
        default Ref<Filter.Config<?>> filter() { return Ref.empty(); }
        Ref<AbstractRegion.Config<?>> region();
    }
}
