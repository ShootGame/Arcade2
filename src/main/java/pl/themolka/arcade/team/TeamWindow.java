package pl.themolka.arcade.team;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.command.CommandContext;
import pl.themolka.arcade.command.CommandException;
import pl.themolka.arcade.command.GameCommands;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.item.ItemStackBuilder;
import pl.themolka.arcade.match.PlayMatchWindow;

import java.util.HashMap;
import java.util.Map;

/**
 * <strong>The Window Overview:</strong>
 * The window will automatically fill empty slots with
 * available teams which can participate.
 *
 * +---------------------------------------------------------------+
 * |  Choose your team                                             |
 * +------+------+------+------+------+------+------+------+-------+
 * | auto | team | team | team | team |      |      |      | close |
 * +------+------+------+------+------+------+------+------+-------+
 *
 * <hr />
 *
 * <strong>The Overfilled Window Overview:</strong>
 * If the slots gets overfilled, this will automatically
 * increase the window.
 *
 * +---------------------------------------------------------------+
 * |  Choose your team                                             |
 * +------+------+------+------+------+------+------+------+-------+
 * | auto | team | team | team | team | team | team | team | team  |
 * +------+------+------+------+------+------+------+------+-------+
 * | team | team |  and |  so  |  on  | ...  |      |      | close |
 * +------+------+------+------+------+------+------+------+-------+
 */
public class TeamWindow extends PlayMatchWindow {
    public static final String NAME = ChatColor.DARK_BLUE + "Choose your team";
    public static final ItemStack AUTO_JOIN_ITEM = new ItemStackBuilder()
            .type(Material.NETHER_STAR)
            .displayName(ChatColor.YELLOW + "Auto join the best team")
            .build();

    private final TeamsGame game;
    private final Map<Integer, Team> teams = new HashMap<>();

    public TeamWindow(TeamsGame game) {
        super(game.getMatch(), Math.min((game.getTeams().size() / 7) + 1, ROW_LIMIT), NAME);

        this.game = game;

        int slot = 1;
        for (Team team : game.getTeams()) {
            if (team.isParticipating()) {
                this.teams.put(slot, team);
                slot++;
            }
        }
    }

    @Override
    public boolean onClick(GamePlayer player, ClickType click, int slot, ItemStack item) {
        if (slot != this.getCloseItemSlot() && item != null) {
            try {
                if (slot == 0) {
                    // auto
                    this.join(player, null);
                } else if (slot != this.getContainer().getSize() - 1) {
                    // team
                    Team team = this.teams.get(slot);
                    if (team != null) {
                        this.join(player, team);
                    }
                }

                // close anyway
                this.close(player);
            } catch (CommandException ex) {
                player.sendError(ex.getMessage());
            }

            return false;
        }

        return super.onClick(player, click, slot, item);
    }

    @Override
    public void onCreate() {
        this.getContainer().setItem(0, AUTO_JOIN_ITEM);
        for (int slot : this.teams.keySet()) {
            this.getContainer().setItem(slot, this.buildTeamItem(this.teams.get(slot)));
        }

        super.onCreate();
    }

    public void join(GamePlayer player, Team team) {
        String[] args = new String[0];
        if (team != null) {
            args = new String[] {team.getName()};
        }

        GameCommands.JoinCommandEvent event = new GameCommands.JoinCommandEvent(
                this.game.getPlugin(), player, CommandContext.parse(null, null, args), team == null);
        this.game.getPlugin().getEventBus().publish(event);

        if (!event.isCanceled() && !event.hasJoined()) {
            if (event.isAuto()) {
                this.game.autoJoinTeam(player);
            } else {
                this.game.joinTeam(player, args[0]);
            }
        }
    }

    private ItemStack buildTeamItem(Team team) {
        return new ItemStackBuilder()
                .type(Material.WOOL)
                .durability(team.getColor().getWoolData())
                .displayName(ChatColor.YELLOW + "Join " + ChatColor.BOLD + team.getPrettyName())
                .build();
    }
}
