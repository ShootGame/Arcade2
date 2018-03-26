package pl.themolka.arcade.filter;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserNotSupportedException;

import java.util.HashMap;
import java.util.Map;

@ModuleInfo(id = "Filters")
public class FiltersModule extends Module<FiltersGame> {
    @Override
    public FiltersGame buildGameModule(Element xml, Game game) throws JDOMException {
        Map<String, FilterSet> filters = new HashMap<>();

        // ----------  Default Filters  ----------
        // This should be moved somewhere else...

        filters.putAll(this.defaultAllow());
        filters.putAll(this.defaultDeny());
        filters.putAll(this.defaultAbstain());

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

    //
    // Default Indexed Values
    //

    private Map<String, FilterSet> defaultAllow() {
        return this.defaultValues(StaticFilter.ALLOW, "allow", "yes", "on", "true");
    }

    private Map<String, FilterSet> defaultDeny() {
        return this.defaultValues(StaticFilter.DENY, "deny", "no", "off", "false");
    }

    private Map<String, FilterSet> defaultAbstain() {
        return this.defaultValues(StaticFilter.ABSTAIN, "abstain");
    }

    private Map<String, FilterSet> defaultValues(StaticFilter filter, String... ids) {
        Map<String, FilterSet> results = new HashMap<>();
        for (String id : ids) {
            results.put(id, new FilterSet(id, filter));
        }
        return results;
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return super.getGameModuleParser(context);
    }

    @Override
    public void defineGameModule(Game game, IGameModuleConfig<FiltersGame> config, ConfigContext context) {
    }
}
