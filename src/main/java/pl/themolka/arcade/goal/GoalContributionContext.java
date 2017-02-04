package pl.themolka.arcade.goal;

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

    public boolean isEmpty() {
        return this.contributors.isEmpty();
    }

    public boolean removeContributor(GamePlayer player) {
        GoalContributor contributor = this.getContributor(new GamePlayerSnapshot(player));
        return contributor != null && this.removeContributor(player);
    }

    public boolean removeContributor(GoalContributor contributor) {
        return this.contributors.remove(contributor);
    }
}
