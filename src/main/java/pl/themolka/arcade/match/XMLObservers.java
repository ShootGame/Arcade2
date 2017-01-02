package pl.themolka.arcade.match;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.jdom2.Element;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.team.XMLTeam;

public class XMLObservers extends XMLTeam {
    public static Observers parse(Element xml, ArcadePlugin plugin) {
        Observers observers = new Observers(plugin);

        if (xml != null) {
            ChatColor color = parseColor(xml);
            if (color != null) {
                observers.setColor(color);
            }

            DyeColor dyeColor = parseDyeColor(xml);
            if (dyeColor != null) {
                observers.setDyeColor(dyeColor);
            }

            String name = parseName(xml);
            if (name != null) {
                observers.setName(name);
            }
        }

        return observers;
    }
}
