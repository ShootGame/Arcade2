package pl.themolka.arcade.spawn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MultiSpawn extends ForwardingSpawn {
    private final Random random = new Random();

    private final List<Spawn> spawns;

    private MultiSpawn(List<Spawn> spawns) {
        this.spawns = spawns;
    }

    @Override
    protected Spawn delegate() {
        return this.getRandomSpawn();
    }

    public List<Spawn> getSpawns() {
        return new ArrayList<>(this.spawns);
    }

    public boolean isEmpty() {
        return this.spawns.isEmpty();
    }

    public Spawn getRandomSpawn() {
        return this.spawns.get(this.random.nextInt(this.spawns.size()));
    }

    //
    // Instancing
    //

    public static MultiSpawn of(Collection<Spawn> spawns) {
        if (spawns != null) {
            spawns = unpack(spawns);

            if (!spawns.isEmpty()) {
                return new MultiSpawn(new ArrayList<>(spawns));
            }
        }

        throw new IllegalArgumentException("Illegal spawn collection.");
    }

    public static MultiSpawn of(Spawn... spawns) {
        return of(Arrays.asList(spawns));
    }

    private static List<Spawn> unpack(Collection<Spawn> spawns) {
        List<Spawn> results = new ArrayList<>();
        for (Spawn spawn : spawns) {
            if (spawn instanceof MultiSpawn) {
                results.addAll(((MultiSpawn) spawn).getSpawns());
            } else {
                results.add(spawn);
            }
        }

        return results;
    }
}
