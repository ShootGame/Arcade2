package pl.themolka.arcade.objective.point;

import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.filter.Filter;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.region.IRegionFieldStrategy;
import pl.themolka.arcade.region.UnionRegion;

import java.util.Collections;
import java.util.Set;

@Produces(Capture.Config.class)
public class CaptureParser extends ConfigParser<Capture.Config>
                           implements InstallableParser {
    private Parser<IRegionFieldStrategy> fieldStrategyParser;
    private Parser<Ref> filterParser;
    private Parser<UnionRegion.Config> regionParser;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        super.install(context);
        this.fieldStrategyParser = context.type(IRegionFieldStrategy.class);
        this.filterParser = context.type(Ref.class);
        this.regionParser = context.type(UnionRegion.Config.class);
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("point capture");
    }

    @Override
    protected ParserResult<Capture.Config> parseNode(Node node, String name, String value) throws ParserException {
        IRegionFieldStrategy fieldStrategy = this.fieldStrategyParser.parse(node.property(
                "field-strategy", "fieldstrategy")).orDefault(Capture.Config.DEFAULT_FIELD_STRATEGY);
        Ref<Filter.Config<?>> filter = this.filterParser.parse(node.firstChild("filter")).orDefault(Ref.empty());
        UnionRegion.Config region = this.regionParser.parseWithDefinition(node, name, value).orFail();

        return ParserResult.fine(node, name, value, new Capture.Config() {
            public Ref<IRegionFieldStrategy> fieldStrategy() { return Ref.ofProvided(fieldStrategy); }
            public Ref<Filter.Config<?>> filter() { return filter; }
            public Ref<AbstractRegion.Config<?>> region() { return Ref.ofProvided(region); }
        });
    }
}
