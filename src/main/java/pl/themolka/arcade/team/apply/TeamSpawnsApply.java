package pl.themolka.arcade.team.apply;

import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.PlayerApplicable;
import pl.themolka.arcade.map.ArcadeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeamSpawnsApply implements PlayerApplicable {
    private final Random random = new Random();

    private final List<TeamSpawnApply> spawns = new ArrayList<>();

    public TeamSpawnsApply() {
    }

    public TeamSpawnsApply(List<TeamSpawnApply> spawns) {
        if (spawns != null) {
            this.spawns.addAll(spawns);
        }
    }

    @Override
    public void apply(GamePlayer player) {
        TeamSpawnApply spawn = this.nextRandomSpawn();
        if (spawn != null) {
            spawn.apply(player);
        }
    }

    public boolean addSpawn(TeamSpawnApply spawn) {
        return this.spawns.add(spawn);
    }

    public List<TeamSpawnApply> getSpawns() {
        return this.spawns;
    }

    public boolean isEmpty() {
        return this.spawns.isEmpty();
    }

    public TeamSpawnApply nextRandomSpawn() {
        if (this.isEmpty()) {
            return null;
        } else {
            return this.spawns.get(this.random.nextInt(this.spawns.size()));
        }
    }

    public boolean removeSpawn(TeamSpawnApply spawn) {
        return this.spawns.remove(spawn);
    }

    public static TeamSpawnsApply parse(ArcadeMap map, Element xml) {
        List<TeamSpawnApply> results = new ArrayList<>();

        for (Element child : xml.getChildren()) {
            switch (child.getName().toLowerCase()) {
                case "region":
                    RegionSpawnApply region = RegionSpawnApply.parse(map, child);
                    if (region != null) {
                        results.add(region);
                    }
                    break;
            }
        }

        if (!results.isEmpty()) {
            return new TeamSpawnsApply(results);
        }

        return null;
    }
}
