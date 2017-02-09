package pl.themolka.arcade.team;

import org.apache.commons.lang3.builder.Builder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import pl.themolka.arcade.ArcadePlugin;

public class TeamBuilder implements Builder<Team> {
    private final ArcadePlugin plugin;

    private final String id;
    private ChatColor color;
    private DyeColor dyeColor;
    private boolean friendlyFire;
    private int maxPlayers;
    private int minPlayers;
    private String name;
    private int slots;

    public TeamBuilder(ArcadePlugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    @Override
    public Team build() {
        Team team = new Team(this.plugin, this.id());
        team.setColor(this.color());
        team.setDyeColor(this.dyeColor());
        team.setFriendlyFire(this.friendlyFire());
        team.setMaxPlayers(this.maxPlayers());
        team.setMinPlayers(this.minPlayers());
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

    public boolean friendlyFire() {
        return this.friendlyFire;
    }

    public TeamBuilder friendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
        return this;
    }

    public String id() {
        return this.id;
    }

    public int maxPlayers() {
        return this.maxPlayers;
    }

    public TeamBuilder maxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public int minPlayers() {
        return this.minPlayers;
    }

    public TeamBuilder minPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
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
