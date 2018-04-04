package pl.themolka.arcade.life;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.kit.Kit;

public class KillReward implements PlayerApplicable {
    private final Filter filter;
    private final Kit kit;

    @Deprecated
    public KillReward(Filter filter, Kit kit) {
        this.filter = Filters.secure(filter);
        this.kit = kit;
    }

    protected KillReward(Config config) {
        this.filter = Filters.secure(config.filter().getIfPresent());
        this.kit = config.kit().getIfPresent();
    }

    @Override
    public void apply(GamePlayer player) {
        if (this.canReward(player)) {
            this.rewardPlayer(player);
        }
    }

    public boolean canReward(GamePlayer player) {
        return player != null && player.isOnline() && this.filter.filter(player).isNotFalse();
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean rewardPlayer(GamePlayer player) {
        if (player != null && player.isOnline()) {
            Kit kit = this.getKit();

            if (kit != null) {
                kit.apply(player);
                return true;
            }
        }

        return false;
    }

    public interface Config extends IGameConfig<KillReward> {
        default Ref<Filter> filter() { return Ref.empty(); }
        default Ref<Kit> kit() { return Ref.empty(); }

        @Override
        default KillReward create(Game game) {
            return new KillReward(this);
        }
    }
}
