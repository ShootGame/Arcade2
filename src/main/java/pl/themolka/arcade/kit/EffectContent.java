package pl.themolka.arcade.kit;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.potion.XMLPotionEffect;

public class EffectContent implements KitContent<PotionEffect> {
    private final PotionEffect result;

    public EffectContent(PotionEffect result) {
        this.result = result;
    }

    @Override
    public void apply(GamePlayer player) {
        Player bukkit = player.getBukkit();
        if (bukkit != null) {
            bukkit.addPotionEffect(this.getResult(), true);
        }
    }

    @Override
    public PotionEffect getResult() {
        return this.result;
    }

    public static class Parser implements KitContentParser<EffectContent> {
        @Override
        public EffectContent parse(Element xml) throws DataConversionException {
            return new EffectContent(XMLPotionEffect.parse(xml));
        }
    }
}
