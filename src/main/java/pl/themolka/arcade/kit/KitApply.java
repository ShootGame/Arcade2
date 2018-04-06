package pl.themolka.arcade.kit;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;

public class KitApply implements PlayerApplicable {
    private final Kit kit;

    public KitApply(Kit kit) {
        this.kit = kit;
    }

    @Override
    public void apply(GamePlayer player) {
        this.getKit().apply(player, false);
        player.getBukkit().updateInventory();
    }

    public Kit getKit() {
        return this.kit;
    }
}
