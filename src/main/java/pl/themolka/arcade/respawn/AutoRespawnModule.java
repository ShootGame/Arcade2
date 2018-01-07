package pl.themolka.arcade.respawn;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.time.Time;

@ModuleInfo(id = "Auto-Respawn")
public class AutoRespawnModule extends Module<AutoRespawnGame> {
    public static final int DEFAULT_COOLDOWN = 1;

    @Override
    public AutoRespawnGame buildGameModule(Element xml,
                                           Game game) throws JDOMException {
        return new AutoRespawnGame(this.parseSeconds(DEFAULT_COOLDOWN));
    }

    private long parseSeconds(int seconds) {
        long ticks = Time.SECOND.toTicks();
        return Math.max(seconds * ticks, ticks);
    }
}
