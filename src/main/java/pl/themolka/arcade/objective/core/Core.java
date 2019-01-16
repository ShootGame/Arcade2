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

package pl.themolka.arcade.objective.core;

import com.google.common.collect.ImmutableSet;
import net.engio.mbassy.listener.Handler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;
import pl.themolka.arcade.channel.Messageable;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.event.BlockTransformEvent;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.goal.GoalProgressEvent;
import pl.themolka.arcade.objective.Objective;
import pl.themolka.arcade.region.AbstractRegion;
import pl.themolka.arcade.util.FinitePercentage;

import java.util.HashSet;
import java.util.Set;

public class Core extends Objective {
    private final Set<Block> blocks = new HashSet<>();
    private Detector detector;
    private final int detectorLevel;
    private final Liquid liquid;
    private final Set<Block> liquidSnapshot = new HashSet<>();
    private final Set<Material> material;
    private final AbstractRegion region;

    protected Core(Game game, IGameConfig.Library library, Config config) {
        super(game, library, config);

        this.detectorLevel = config.detectorLevel().get();
        this.liquid = config.liquid().get();
        this.material = config.material().get();
        this.region = library.getOrDefine(game, config.region().get());
    }

    @Override
    public void completeObjective(Participator completer, GamePlayer player) {
        this.getGame().sendGoalMessage(this.describeOwner() + this.describeObjective() + ChatColor.YELLOW +
                " has leaked" + this.contributions.getContributorsPretty() + ChatColor.YELLOW + ".");
        super.completeObjective(completer, player);
    }

    @Override
    public String getDefaultName() {
        return Config.DEFAULT_NAME;
    }

    public boolean breakPiece(Participator participator, Block block, GamePlayer breaker) {
        CoreBreakEvent event = new CoreBreakEvent(this, block, breaker, participator);
        this.getPlugin().getEventBus().publish(event);

        if (event.isCanceled()) {
            return false;
        }

        FinitePercentage oldProgress = this.getProgress();

        block.setType(Material.AIR);
        this.blocks.remove(block);

        this.contributions.addContributor(breaker);
        participator.sendGoalMessage(this.createBreakMessage(breaker));

        GoalProgressEvent.call(this, oldProgress);
        return true;
    }

    public void build() {
        this.blocks.addAll(this.createBlocks(this.region));
        this.liquidSnapshot.addAll(this.createLiquidSnapshot(this.region));

        this.detector = Detector.create(this, Detector.createMinMaxVectors(this.region.getBounds(),
                                                                           this.detectorLevel));
    }

    public boolean contains(Block block) {
        return this.blocks.contains(block);
    }

    public Set<Block> createBlocks(Iterable<Block> from) {
        Set<Block> results = new HashSet<>();
        for (Block block : from) {
            if (this.matchMaterial(block.getType())) {
                results.add(block);
            }
        }

        return results;
    }

    public Set<Block> createLiquidSnapshot(Iterable<Block> from) {
        Set<Block> results = new HashSet<>();
        for (Block block : from) {
            if (this.liquid.accepts(block)) {
                results.add(block);
            }
        }

        return results;
    }

    public Liquid getLiquid() {
        return this.liquid;
    }

    public Set<Material> getMaterial() {
        return new HashSet<>(this.material);
    }

    public AbstractRegion getRegion() {
        return this.region;
    }

    public boolean matchMaterial(Material material) {
        for (Material accept : this.material) {
            if (material.equals(accept)) {
                return true;
            }
        }

        return false;
    }

    private String createBreakMessage(GamePlayer breaker) {
        return ChatColor.GOLD + breaker.getDisplayName() + " broke a piece of " +
                this.describeOwner() + this.describeObjective() + ChatColor.YELLOW + ".";
    }

    //
    // Listeners
    //

    @Handler(priority = Priority.NORMAL)
    public void detectBreak(BlockTransformEvent event) {
        if (event.isCanceled()) {
            return;
        }

        Block block = event.getBlock();
        Material newMaterial = event.getNewState().getType();

        if (!newMaterial.equals(Material.AIR) || this.isCompleted() ||
                !this.contains(block) || this.liquid.accepts(newMaterial)) {
            return;
        }

        GamePlayer breaker = event.getGamePlayer();
        if (breaker == null) {
            event.setCanceled(true);
            return;
        }

        Participator participator = this.getParticipatorResolver().resolve(breaker);
        if (participator == null) {
            event.setCanceled(true);
            return;
        }

        if (this.hasOwner() && this.getOwner().equals(participator)) {
            event.setCanceled(true);
            breaker.sendError("You may not damage your own " + ChatColor.GOLD +
                    this.getColoredName() + Messageable.ERROR_COLOR + ".");
            return;
        }

        if (!this.breakPiece(participator, block, breaker)) {
            event.setCanceled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void detectLeak(BlockFromToEvent event) {
        if (this.isCompleted() || !this.detector.contains(event.getToBlock())) {
            return;
        }

        Block block = event.getBlock();
        if (this.liquid.accepts(block.getType())) {
            CoreLeakEvent leakEvent = new CoreLeakEvent(this, block);
            this.getPlugin().getEventBus().publish(leakEvent);

            if (leakEvent.isCanceled()) {
                event.setCancelled(true);
            } else {
                this.completeObjective(null, // We don't know the participator
                                       null); // We don't know the player
            }
        }
    }

    @Handler(priority = Priority.HIGHER)
    public void detectLiquidTransform(BlockTransformEvent event) {
        if (event.isCanceled() || this.liquid.accepts(event.getNewState().getType())) {
            return;
        }

        if (this.liquidSnapshot.contains(event.getBlock())) {
            event.setCanceled(true);

            if (event.hasPlayer()) {
                event.getPlayer().sendError("You may not build inside " + ChatColor.GOLD +
                        this.getColoredName() + Messageable.ERROR_COLOR + ".");
            }
        }
    }

    public interface Config extends Objective.Config<Core> {
        int DEFAULT_DETECTOR_LEVEL = 5;
        Liquid DEFAULT_LIQUID = Liquid.LAVA;
        Set<Material> DEFAULT_MATERIAL = ImmutableSet.of(Material.OBSIDIAN);
        String DEFAULT_NAME = "Core";

        default Ref<Integer> detectorLevel() { return Ref.ofProvided(DEFAULT_DETECTOR_LEVEL); }
        default Ref<Liquid> liquid() { return Ref.ofProvided(DEFAULT_LIQUID); }
        default Ref<Set<Material>> material() { return Ref.ofProvided(DEFAULT_MATERIAL); }
        Ref<AbstractRegion.Config<?>> region();

        @Override
        default Core create(Game game, Library library) {
            return new Core(game, library, this);
        }
    }
}
