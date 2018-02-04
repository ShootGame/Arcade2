package pl.themolka.arcade.team.apply;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.MatchApplyContext;
import pl.themolka.arcade.team.Team;

public class TeamApplyContext extends MatchApplyContext {
    private final Team team;

    public TeamApplyContext(Team team) {
        this.team = team;
    }

    @Override
    protected void applyContent(GamePlayer player, EventType event, Content content) {
        switch (event) {
            case MATCH_START:
                for (GamePlayer member : this.team.getOnlineMembers()) {
                    content.apply(member);
                }
                break;

            case PLAYER_PLAY:
            case PLAYER_RESPAWN:
                content.apply(player);
                break;
        }
    }

    public Team getTeam() {
        return this.team;
    }
}
