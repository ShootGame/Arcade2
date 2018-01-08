package pl.themolka.arcade.filter;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;

import java.util.HashMap;
import java.util.Map;

@ModuleInfo(id = "Filters")
public class FiltersModule extends Module<FiltersGame> {
    @Override
    public FiltersGame buildGameModule(Element xml, Game game) throws JDOMException {
        Map<String, FilterSet> filters = new HashMap<>();

        for (Element baseElement : xml.getChildren("filter")) {
            String id = baseElement.getAttributeValue("id");
            if (id == null || id.trim().isEmpty()) {
                continue;
            }

            FilterSet set = new FilterSet(id);
            for (Element filterElement : baseElement.getChildren()) {
                Filter filter = XMLFilter.parse(this.getPlugin(), filterElement);

                if (filter != null) {
                    set.addFilter(filter);
                }
            }

            if (!set.isEmpty()) {
                filters.put(id.trim(), set);
            }
        }

        return new FiltersGame(filters);
    }
}
