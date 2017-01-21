package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;

public class FlyContent implements KitContent<Boolean> {
    public static final boolean DEFAULT_ALLOW_FLYING = false;
    public static final boolean DEFAULT_FLYING = false;
    public static final float DEFAULT_SPEED = 0.1F;

    private final boolean flying;
    private final float speed;
    private final boolean result;

    public FlyContent(boolean flying, float speed, boolean result) {
        this.flying = flying;
        this.speed = speed;
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        Player bukkit = player.getBukkit();
        bukkit.setAllowFlight(this.getResult());
        bukkit.setFlying(this.isFlying());
        bukkit.setFlySpeed(this.getSpeed());
    }

    @Override
    public Boolean getResult() {
        return this.result;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public static class Parser implements KitContentParser<FlyContent> {
        @Override
        public FlyContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null) {
                return new FlyContent(DEFAULT_FLYING, DEFAULT_SPEED, DEFAULT_ALLOW_FLYING);
            }

            return null;
        }
    }
}
