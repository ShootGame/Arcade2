package pl.themolka.arcade.team;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.match.Match;

public class TeamBuilder implements Builder<Team> {
    private final ArcadePlugin plugin;

    private final Match match;
    private final String id;

    private ChatColor color;
    private DyeColor dyeColor;
    private String name;
    private int slots;

    public TeamBuilder(ArcadePlugin plugin, Match match, String id) {
        this.plugin = plugin;
        this.match = match;
        this.id = id;
    }

    @Override
    public Team build() {
        Team team = new Team(this.plugin, this.match, this.id());
        team.setColor(this.color());
        team.setDyeColor(this.dyeColor());
        team.setName(this.name());
        team.setSlots(this.slots());

        return team;
    }

    public ChatColor color() {
        return this.color;
    }

    public TeamBuilder color(ChatColor color) {
        this.color = color;
        return this;
    }

    public DyeColor dyeColor() {
        return this.dyeColor;
    }

    public TeamBuilder dyeColor(DyeColor dyeColor) {
        this.dyeColor = dyeColor;
        return this;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public TeamBuilder name(String name) {
        this.name = name;
        return this;
    }

    public int slots() {
        return this.slots;
    }

    public TeamBuilder slots(int slots) {
        this.slots = slots;
        return this;
    }
}
