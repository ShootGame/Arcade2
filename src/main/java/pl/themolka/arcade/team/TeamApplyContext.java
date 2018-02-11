package pl.themolka.arcade.team;

import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.MatchApplyContext;

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

            default:
                throw new IllegalArgumentException("Illegal event on content.");
        }
    }

    public Team getTeam() {
        return this.team;
    }
}
