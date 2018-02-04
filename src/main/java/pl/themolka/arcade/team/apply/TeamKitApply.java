package pl.themolka.arcade.team.apply;

import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.kit.Kit;
import pl.themolka.arcade.kit.KitsGame;
import pl.themolka.arcade.map.ArcadeMap;

public class TeamKitApply implements PlayerApplicable {
    private final Kit kit;

    public TeamKitApply(Kit kit) {
        this.kit = kit;
    }

    @Override
    public void apply(GamePlayer player) {
        this.getKit().apply(player);
    }

    public Kit getKit() {
        return this.kit;
    }

    public static TeamKitApply parse(ArcadeMap map, Element xml, KitsGame kits) {
        String id = xml.getValue();
        if (id != null) {
            id = id.trim();
        }

        Kit kit = kits.getKit(id);
        return kit != null ? new TeamKitApply(kit) : null;
    }
}
