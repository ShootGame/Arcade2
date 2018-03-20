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
        this.getKit().apply(player);
        player.getBukkit().updateInventory();
    }

    public Kit getKit() {
        return this.kit;
    }

    /**
     * @deprecated Used in deprecated {@link pl.themolka.arcade.team.XMLTeam}
     */
    @Deprecated
    public static KitApply parse(String id, KitsGame kits) {
        if (id != null) {
            Kit kit = kits.getKit(id.trim());
            return kit != null ? new KitApply(kit) : null;
        }

        return null;
    }
}
