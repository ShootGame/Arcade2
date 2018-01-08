package pl.themolka.arcade.region;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRegion extends AbstractRegion {
    private final List<Block> blocks = new ArrayList<>();
    private final Region parent;

    public BlockRegion(String id, ArcadeMap map, Region parent) {
        super(id, map);

        this.parent = parent;
    }

    public BlockRegion(String id, ArcadeMap map, Region parent, Block... blocks) {
        this(id, map, parent);

        this.addBlocks(blocks);
    }

    public BlockRegion(String id, ArcadeMap map, Region parent, Material... materials) {
        this(id, map, parent);

        this.addBlocks(materials);
    }

    public BlockRegion(String id, ArcadeMap map, Region parent, MaterialData... materials) {
        this(id, map, parent);

        this.addBlocks(materials);
    }

    public BlockRegion(BlockRegion original) {
        this(original.getId(), original.getMap(), original.getParent(), original.getBlocks().toArray(new Block[original.getBlocks().size()]));
    }

    @Override
    public boolean contains(Block block) {
        return this.blocks.contains(block);
    }

    @Override
    public boolean contains(BlockVector vector) {
        return this.containsZero(vector);
    }

    @Override
    public boolean contains(Region region) {
        return false;
    }

    @Override
    public boolean contains(Vector vector) {
        return this.contains(this.getWorld().getBlockAt(vector));
    }

    @Override
    public RegionBounds getBounds() {
        return this.getParent().getBounds();
    }

    @Override
    public Vector getCenter() {
        return this.getParent().getCenter();
    }

    @Override
    public double getHighestY() {
        return this.getParent().getHighestY();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getParent().getRandomVector(random, limit);
    }

    public boolean addBlock(Block block) {
        return this.blocks.add(block);
    }

    public boolean addBlocks(Block... blocks) {
        int index = 0;
        for (Block block : blocks) {
            if (this.addBlock(block)) {
                index++;
            }
        }

        return index != 0;
    }

    public boolean addBlocks(Material... materials) {
        List<MaterialData> data = new ArrayList<>();
        for (Material material : materials) {
            data.add(new MaterialData(material));
        }

        return this.addBlocks(data.toArray(new MaterialData[data.size()]));
    }

    public boolean addBlocks(MaterialData... materials) {
        int index = 0;
        for (Block block : this.getBlocks()) {
            for (MaterialData data : materials) {
                if (data.getItemType().equals(block.getType()) && data.getData() == block.getData() && this.addBlock(block)) {
                    index++;
                }
            }
        }

        return index != 0;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    public Region getParent() {
        return this.parent;
    }

    public boolean removeBlock(Block block) {
        return this.blocks.remove(block);
    }
}
