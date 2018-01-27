package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class KnockbackContent implements RemovableKitContent<Float> {
    public static float DEFAULT_KNOCKBACK = 0F;

    private final float result;

    public KnockbackContent(float result) {
        this.result = result;
    }

    @Override
    public void attach(GamePlayer player, Float value) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            bukkit.setKnockbackReduction(value);
        }
    }

    @Override
    public Float defaultValue() {
        return DEFAULT_KNOCKBACK;
    }

    @Override
    public Float getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<KnockbackContent> {
        @Override
        public KnockbackContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue())) {
                return new KnockbackContent(DEFAULT_KNOCKBACK);
            }

            try {
                return new KnockbackContent(Float.parseFloat(xml.getTextNormalize()));
            } catch (NumberFormatException ex) {
                return null;
            }
        }
    }
}
