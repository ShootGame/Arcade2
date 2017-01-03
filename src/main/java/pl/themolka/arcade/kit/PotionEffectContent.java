package pl.themolka.arcade.kit;

import org.bukkit.potion.PotionEffect;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.potion.XMLPotionEffect;

public class PotionEffectContent implements KitContent<PotionEffect> {
    private final PotionEffect result;

    public PotionEffectContent(PotionEffect result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        player.getPlayer().getBukkit().addPotionEffect(this.getResult(), true);
    }

    @Override
    public PotionEffect getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<PotionEffectContent> {
        @Override
        public PotionEffectContent parse(Element xml) throws DataConversionException {
            return new PotionEffectContent(XMLPotionEffect.parse(xml));
        }
    }
}
