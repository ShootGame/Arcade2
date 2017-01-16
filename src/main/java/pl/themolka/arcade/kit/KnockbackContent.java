package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class KnockbackContent implements KitContent<Float> {
    public static float DEFAULT_KNOCKBACK = 1F;

    private final float result;

    public KnockbackContent(float result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getBukkit().setKnockbackReduction(this.getResult());
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
