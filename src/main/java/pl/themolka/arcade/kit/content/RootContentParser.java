/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.themolka.arcade.kit.content;

import com.google.common.collect.ImmutableSet;
import pl.themolka.arcade.config.ConfigParser;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserMap;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.Collections;
import java.util.Set;

@Produces(KitContent.Config.class)
public class RootContentParser extends ConfigParser<KitContent.Config<?, ?>>
                               implements InstallableParser {
    private static final Set<Class<?>> types = ImmutableSet.<Class<?>>builder()
            .add(AbsorptionContent.class)
            .add(BootsContent.class)
            .add(BurnContent.class)
            .add(CanFlyContent.class)
            .add(ChestplateContent.class)
            .add(ClearInventoryContent.class)
            .add(CompassTargetContent.class)
            .add(DamageContent.class)
            .add(EffectContent.class)
            .add(EjectContent.class)
            .add(EliminateContent.class)
            .add(ExhaustionContent.class)
            .add(ExperienceContent.class)
            .add(FallDistanceContent.class)
            .add(FlyContent.class)
            .add(FlySpeedContent.class)
            .add(GameModeContent.class)
            .add(GiveExperienceContent.class)
            .add(GiveKitContent.class)
            .add(GiveLevelContent.class)
            .add(GlowContent.class)
            .add(GravityContent.class)
            .add(HealthContent.class)
            .add(HealthScaleContent.class)
            .add(HeldSlotContent.class)
            .add(HelmetContent.class)
            .add(HungerContent.class)
            .add(ItemStackContent.class)
            .add(KillContent.class)
            .add(KnockbackContent.class)
            .add(LeaveVehicleContent.class)
            .add(LeggingsContent.class)
            .add(LevelContent.class)
            .add(LivesContent.class)
            .add(MaxHealthContent.class)
            .add(MessageContent.class)
            .add(ModifierContent.class)
            .add(RemoveArrowsContent.class)
            .add(RemoveEffectsContent.class)
            .add(RemoveKitContent.class)
            .add(RemoveModifiersContent.class)
            .add(ResetContent.class)
            .add(ResetHealthContent.class)
            .add(SaturationContent.class)
            .add(SilentContent.class)
            .add(SoundContent.class)
            .add(TeamContent.class)
            .add(TitleContent.class)
            .add(VelocityContent.class)
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
        return Collections.singleton("kit content type");
    }

    @Override
    protected Result<KitContent.Config<?, ?>> parseNode(Node node, String name, String value) throws ParserException {
        BaseContentParser<?> parser = this.nested.parse(name);
        if (parser == null) {
            throw this.fail(node, null, name, "Unknown kit content type");
        }

        return Result.fine(node, name, value, parser.parseWithDefinition(node, name, value).orFail());
    }
}
