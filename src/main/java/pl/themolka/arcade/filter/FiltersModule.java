package pl.themolka.arcade.filter;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.config.ConfigContext;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.game.IGameModuleConfig;
import pl.themolka.arcade.module.Module;
import pl.themolka.arcade.module.ModuleInfo;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.xml.XML;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

            try {
                FilterSet.Config set = game.getPlugin().getParsers().forType(FilterSet.Config.class)
                        .parseWithDefinition(XML.convert(baseElement), baseElement.getName(), baseElement.getValue())
                        .orDefaultNull();

                if (set != null) {
                    filters.put(id.trim(), set.create(game));
                }
            } catch (ParserNotSupportedException | ParserException ex) {
                ex.printStackTrace();
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
            results.put(id, new FilterSet.Config() {
                public String id() { return id; }
                public Ref<Set<Filter.Config<?>>> filters() { return Ref.ofProvided(Collections.singleton(filter.config())); }
            }.create(this.getGame()));
        }
        return results;
    }

    @Override
    public GameModuleParser<?, ?> getGameModuleParser(ParserContext context) throws ParserNotSupportedException {
        return context.of(FiltersGameParser.class);
    }

    @Override
    public void defineGameModule(Game game, IGameModuleConfig<FiltersGame> config, ConfigContext context) {
        context.define("allow", StaticFilter.ALLOW.config());
        context.define("deny", StaticFilter.DENY.config());
        context.define("abstain", StaticFilter.ABSTAIN.config());
    }
}
