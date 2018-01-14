package pl.themolka.arcade.firework;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.capture.point.PointCaptureFireworks;
import pl.themolka.arcade.capture.wool.WoolPlaceFireworks;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.leak.core.CoreLeakFireworks;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(id = "Fireworks")
public class FireworksModule extends Module<FireworksGame> {
    @Override
    public FireworksGame buildGameModule(Element xml, Game game) throws JDOMException {
        List<FireworkHandler> handlers = new ArrayList<>();
        handlers.add(new CoreLeakFireworks(XMLParser.parseBoolean("core-leak", true)));
        handlers.add(new PointCaptureFireworks(XMLParser.parseBoolean("point-capture", true)));
        handlers.add(new WoolPlaceFireworks(XMLParser.parseBoolean("wool-place", true)));

        return new FireworksGame(handlers);
    }
}
