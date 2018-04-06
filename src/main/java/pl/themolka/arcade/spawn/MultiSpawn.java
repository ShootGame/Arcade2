package pl.themolka.arcade.spawn;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MultiSpawn extends ForwardingSpawn implements Iterable<Spawn> {
    private final Random random = new Random();

    private final List<Spawn> spawns = new ArrayList<>();

    protected MultiSpawn(Game game, IGameConfig.Library library, Config config) {
        for (Spawn.Config<?> spawn : config.spawns().get()) {
            this.spawns.add(library.getOrDefine(game, spawn));
        }
    }

    @Override
    protected Spawn delegate() {
        return this.getRandomSpawn();
    }

    @Override
    public Iterator<Spawn> iterator() {
        return this.spawns.iterator();
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
                List<Spawn.Config<?>> finalSpawns = new ArrayList<>();
                for (Spawn spawn : spawns) {
                    finalSpawns.add(new Spawn.Config<Spawn>() {
                        public String id() { return spawn.getId(); }
                        public Spawn create(Game game, Library library) { return spawn; }
                    });
                }

                return new MultiSpawn.Config() {
                    public String id() { return null; }
                    public Ref<List<Spawn.Config<?>>> spawns() { return Ref.ofProvided(finalSpawns); }
                }.create(null, IGameConfig.Library.EMPTY);
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
                results.addAll(((MultiSpawn) spawn).spawns);
            } else {
                results.add(spawn);
            }
        }

        return results;
    }

    public interface Config extends ForwardingSpawn.Config<MultiSpawn> {
        Ref<List<Spawn.Config<?>>> spawns();

        @Override
        default MultiSpawn create(Game game, Library library) {
            return new MultiSpawn(game, library, this);
        }
    }
}
