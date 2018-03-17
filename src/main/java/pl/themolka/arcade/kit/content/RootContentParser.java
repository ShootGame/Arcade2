package pl.themolka.arcade.kit.content;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.NodeParser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Produces(KitContent.class)
public class RootContentParser extends NodeParser<BaseContentParser<?>>
                               implements InstallableParser {
    private static final Set<Class<? extends BaseContentParser>> types = ImmutableSet.<Class<? extends BaseContentParser>>builder()
            .add(BootsContent.ContentParser.class)
            .add(ChestplateContent.ContentParser.class)
            .add(ClearInventoryContent.ContentParser.class)
            .add(EffectContent.ContentParser.class)
            .add(FlyContent.ContentParser.class)
            .add(FlySpeedContent.ContentParser.class)
            .add(FoodLevelContent.ContentParser.class)
            .add(GameModeContent.ContentParser.class)
            .add(HealthContent.ContentParser.class)
            .add(HeldSlotContent.ContentParser.class)
            .add(HelmetContent.ContentParser.class)
            .add(ItemStackContent.ContentParser.class)
            .add(KillContent.ContentParser.class)
            .add(KnockbackContent.ContentParser.class)
            .add(LeggingsContent.ContentParser.class)
            .add(LivesContent.ContentParser.class)
            .add(MaxHealthContent.ContentParser.class)
            .add(MessageContent.ContentParser.class)
            .add(SaturationContent.ContentParser.class)
            .add(SoundContent.ContentParser.class)
            .add(WalkSpeedContent.ContentParser.class)
            .build();

    private Map<String, BaseContentParser<?>> parsers;

    @Override
    public void install(ParserContext context) {
        this.parsers = new HashMap<>();
        for (Class<? extends BaseContentParser> clazz : types) {
            NestedParserName name = clazz.getDeclaredAnnotation(NestedParserName.class);
            if (name == null) {
                continue;
            }

            Produces produces = clazz.getDeclaredAnnotation(Produces.class);
            if (produces == null) {
                continue;
            }

            Class<?> contentType = produces.value();
            if (contentType == null || !KitContent.class.isAssignableFrom(contentType)) {
                continue;
            }

            for (String singleName : name.value()) {
                try {
                    String trim = singleName.trim();
                    if (!trim.isEmpty()) {
                        this.parsers.put(singleName, clazz.newInstance());
                    }
                } catch (ReflectiveOperationException ignored) {
                }
            }
        }

        for (BaseContentParser<?> parent : parsers.values()) {
            if (parent instanceof InstallableParser) {
                ((InstallableParser) parent).install(context);
            }
        }
    }

    @Override
    public Set<Object> expect() {
        return Collections.singleton("root content node");
    }

    @Override
    protected ParserResult<BaseContentParser<?>> parseNode(Node node, String name, String value) throws ParserException {
        BaseContentParser<?> content = this.parsers.get(name);
        if (content == null) {
            return ParserResult.empty(node, name);
        }

        return ParserResult.fine(node, name, value, content);
    }
}
