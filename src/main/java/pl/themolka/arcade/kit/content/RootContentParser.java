package pl.themolka.arcade.kit.content;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.Set;

@Produces(KitContent.class)
public class RootContentParser extends NodeParser<KitContent<?>>
                               implements InstallableParser {
    private static final Set<Class<?>> types = ImmutableSet.<Class<?>>builder()
            .add(BootsContent.class)
            .add(ChestplateContent.class)
            .add(ClearInventoryContent.class)
            .add(EffectContent.class)
            .add(FlyContent.class)
            .add(FlySpeedContent.class)
            .add(FoodLevelContent.class)
            .add(GameModeContent.class)
            .add(HealthContent.class)
            .add(HeldSlotContent.class)
            .add(HelmetContent.class)
            .add(ItemStackContent.class)
            .add(KillContent.class)
            .add(KnockbackContent.class)
            .add(LeggingsContent.class)
            .add(LivesContent.class)
            .add(MaxHealthContent.class)
            .add(MessageContent.class)
            .add(SaturationContent.class)
            .add(SoundContent.class)
            .add(TeamContent.class)
            .add(TitleContent.class)
            .add(WalkSpeedContent.class)
            .build();

    private NestedParserMap<BaseContentParser<?>> nested;

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.nested = new NestedParserMap<>(context);
        for (Class<?> clazz : types) {
            this.nested.scan(clazz);
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("root kit content node");
    }

    @Override
    protected ParserResult<KitContent<?>> parseNode(Node node, String name, String value) throws ParserException {
        BaseContentParser<?> parser = this.nested.parse(name);
        if (parser == null) {
            throw this.fail(node, name, value, "Unknown kit content type");
        }

        return ParserResult.fine(node, name, value, parser.parseWithName(node, name).orFail());
    }
}
