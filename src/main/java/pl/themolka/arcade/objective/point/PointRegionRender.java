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

package pl.themolka.arcade.objective.point;

import net.engio.mbassy.listener.Handler;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.GameStartEvent;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.objective.Objective;
import pl.themolka.arcade.team.TeamEditEvent;
import pl.themolka.arcade.util.Color;
import pl.themolka.arcade.util.material.BannerUtils;
import pl.themolka.arcade.util.material.ColoredMaterials;

import java.util.Objects;
import java.util.Set;

public class PointRegionRender implements Listener {
    private final Set<Objective> objectives;

    public PointRegionRender(Set<Objective> objectives) {
        this.objectives = Objects.requireNonNull(objectives, "objectives cannot be null");
    }

    public boolean isRenderable(Block block) {
        return block != null && this.isRenderable(block.getType());
    }

    public boolean isRenderable(Material material) {
        return material != null && ColoredMaterials.isColored(material);
    }

    public boolean isRenderable(MaterialData data) {
        return data != null && this.isRenderable(data.getItemType());
    }

    public void renderAllPoints() {
        for (Objective objective : this.objectives) {
            if (objective instanceof Point) {
                this.renderPoint((Point) objective);
            }
        }
    }

    public boolean renderBanner(Banner banner, DyeColor color) {
        if (!Objects.equals(banner.getBaseColor(), color)) {
            banner.setBaseColor(color);
            return banner.update(false, false);
        }

        return false;
    }

    public boolean renderBanner(BlockState blockState, DyeColor color) {
        return blockState instanceof Banner && this.renderBanner((Banner) blockState, color);
    }

    public boolean renderBlock(Block block, DyeColor color) {
        Material material = block.getType();

        if (this.isRenderable(material) && !Objects.equals(ColoredMaterials.COLORS.get(material), color)) {
            Material newType = ColoredMaterials.TYPES.get(material).get(color);

            if (newType != null) {
                block.setType(newType, false);
                return true;
            }
        } else if (BannerUtils.isBanner(material)) {
            this.renderBanner(block.getState(), color);
            return true;
        }

        return false;
    }

    public void renderBlocks(Iterable<Block> blocks, DyeColor color) {
        for (Block block : blocks) {
            this.renderBlock(block, color);
        }
    }

    public void renderPoint(Point point) {
        Participator owner = point.getOwner();
        this.renderPoint(point, owner != null ? owner.getColor() : point.getNeutralColor());
    }

    public void renderPoint(Point point, Color color) {
        this.renderPoint(point, color.toDye());
    }

    public void renderPoint(Point point, DyeColor color) {
        this.renderBlocks(point.getStateRegion(), color);
    }

    @Handler(priority = Priority.LAST)
    public void initialRender(GameStartEvent event) {
        this.renderAllPoints();
    }

    @Handler(priority = Priority.LAST)
    public void renderParticipatorEdit(TeamEditEvent event) {
        if (event.getReason().equals(TeamEditEvent.Reason.PAINT)) {
            this.renderAllPoints();
        }
    }

    @Handler(priority = Priority.LAST)
    public void renderCaptured(PointCaptureEvent event) {
        this.renderPoint(event.getGoal(), event.getCapturer().getColor());
    }

    @Handler(priority = Priority.LAST)
    public void renderNeutral(PointLoseEvent event) {
        this.renderPoint(event.getGoal(), event.getNewState().getColor());
    }
}
