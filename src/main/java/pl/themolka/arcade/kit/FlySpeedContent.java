package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;

public class FlySpeedContent implements RemovableKitContent<Float> {
    public static final float DEFAULT_SPEED = 0.1F;

    private final float result;

    public FlySpeedContent(float result) {
        this.result = result;
    }

    public FlySpeedContent(int result) {
        this((float) result);
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.testBukkit(player);
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        player.getBukkit().setFlySpeed(value);
    }

    @Override
    public Float defaultValue() {
        return DEFAULT_SPEED;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<FlySpeedContent> {
        @Override
        public FlySpeedContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null) {
                return new FlySpeedContent(DEFAULT_SPEED);
            }

            try {
                return new FlySpeedContent(Integer.parseInt(xml.getValue()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
