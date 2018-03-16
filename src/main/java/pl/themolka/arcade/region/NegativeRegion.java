package pl.themolka.arcade.region;

import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;

import java.util.Random;

public class NegativeRegion extends AbstractRegion {
    private final UnionRegion region;

    public NegativeRegion(UnionRegion region) {
        this(region.getId(), region);
    }

    public NegativeRegion(String id, UnionRegion region) {
        super(region.getGame(), id);

        this.region = region;
    }

    public NegativeRegion(NegativeRegion original) {
        this(original.getId(), original.getRegion());
    }

    @Override
    public boolean contains(BlockVector vector) {
        return !this.getRegion().contains(vector);
    }

    @Override
    public boolean contains(Region region) {
        return !this.getRegion().contains(region);
    }

    @Override
    public boolean contains(Vector vector) {
        return !this.getRegion().contains(vector);
    }

    @Override
    public RegionBounds getBounds() {
        return this.getRegion().getBounds();
    }

    @Override
    public Vector getCenter() {
        return this.getRegion().getCenter();
    }

    @Override
    public double getHighestY() {
        return this.getRegion().getHighestY();
    }

    @Override
    public Vector getRandom(Random random, int limit) {
        return this.getRegion().getRandomVector(random, limit);
    }

    public UnionRegion getRegion() {
        return this.region;
    }

    public interface Config extends AbstractRegion.Config<NegativeRegion> {
        Ref<UnionRegion.Config> region();

        @Override
        default NegativeRegion create(Game game) {
            UnionRegion.Config region = this.region().getIfPresent();
            if (region == null) {
                return null;
            }

            UnionRegion union = region.create(game);
            if (union == null) {
                return null;
            }

            return new NegativeRegion(this.id(), union);
        }
    }
}
