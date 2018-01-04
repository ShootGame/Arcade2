package pl.themolka.arcade.goal;

import org.bukkit.ChatColor;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.GamePlayerSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GoalContributionContext {
    private final List<GoalContributor> contributors = new ArrayList<>();

    public boolean addContributor(GamePlayer player) {
        boolean result = true;

        GoalContributor contributor = this.getContributor(player.getUuid());
        if (contributor == null) {
            contributor = new GoalContributor(player);
            result = this.contributors.add(contributor);
        }

        contributor.incrementTouch();
        Collections.sort(this.contributors);
        return result;
    }

    public void clearContributors() {
        this.contributors.clear();
    }

    public GoalContributor getContributor(GamePlayer player) {
        return this.getContributor(player.getUuid());
    }

    public GoalContributor getContributor(GamePlayerSnapshot player) {
        return this.getContributor(player.getUuid());
    }

    public GoalContributor getContributor(UUID uuid) {
        for (GoalContributor contributor : this.contributors) {
            if (contributor.getUuid().equals(uuid)) {
                return contributor;
            }
        }

        return null;
    }

    public List<GoalContributor> getContributors() {
        return this.contributors;
    }

    public String getContributorsPretty() {
        if (this.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder(" by ");
        int max = 5;
        int size = this.size();

        int touches = 0;
        for (GoalContributor contributor : this.contributors) {
            touches += contributor.getTouches();
        }

        for (int i = 0; i < size; i++) {
            builder.append(ChatColor.RESET);

            GoalContributor contributor = this.contributors.get(i);
            String name = contributor.getDisplayName();
            int percentage = Math.round((100F / touches) * contributor.getTouches());

            if (i != 0) {
                builder.append(ChatColor.YELLOW);
                if (i + 1 == size) {
                    builder.append(" and ");
                } else if (i + 1 == max) {
                    builder.append(" and ").append(ChatColor.GOLD).append(size - max)
                            .append(ChatColor.YELLOW).append(" more..");
                    break;
                } else {
                    builder.append(", ");
                }
            }

            builder.append(ChatColor.GOLD).append(name).append(ChatColor.RESET)
                    .append(ChatColor.YELLOW).append(" (").append(ChatColor.GREEN)
                    .append(percentage).append("%").append(ChatColor.YELLOW).append(")");
        }

        return builder.toString();
    }

    public boolean isEmpty() {
        return this.contributors.isEmpty();
    }

    public boolean removeContributor(GamePlayer player) {
        GoalContributor contributor = this.getContributor(player);
        return contributor != null && this.removeContributor(contributor);
    }

    public boolean removeContributor(GoalContributor contributor) {
        return this.contributors.remove(contributor);
    }

    public int size() {
        return this.contributors.size();
    }
}
