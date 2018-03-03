package pl.themolka.arcade.life;

import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.filter.Filters;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.kit.Kit;

public class KillReward implements PlayerApplicable {
    private final Filter filter;
    private final Kit kit;

    public KillReward(Filter filter, Kit kit) {
        this.filter = Filters.secure(filter);
        this.kit = kit;
    }

    @Override
    public void apply(GamePlayer player) {
        if (this.canReward(player)) {
            this.rewardPlayer(player);
        }
    }

    public boolean canReward(GamePlayer player) {
        return player != null && player.isOnline() && this.getFilter().filter(player).isNotDenied();
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
}
