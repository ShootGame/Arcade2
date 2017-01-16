package pl.themolka.arcade.team;

import net.engio.mbassy.listener.Handler;
import org.bukkit.entity.Player;
import org.jdom2.Element;
import pl.themolka.arcade.event.Priority;
import pl.themolka.arcade.filter.AbstractFilter;
import pl.themolka.arcade.filter.FilterParseEvent;
import pl.themolka.arcade.filter.FilterResult;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.session.ArcadePlayer;

public class TeamFilters {
    private final TeamsGame teams;

    public TeamFilters(TeamsGame teams) {
        this.teams = teams;
    }

    @Handler(priority = Priority.NORMAL)
    public void onFilterParse(FilterParseEvent event) {
        if (event.hasResult()) {
            return;
        }

        Element xml = event.getXml();
        switch (event.getName()) {
            case "player-team":
        }
    }

    public class PlayerTeamFilter extends AbstractFilter {
        private Team team;

        public PlayerTeamFilter(Element xml) {
            String teamAttribute = xml.getAttributeValue("member");
            if (teamAttribute != null) {
                this.team = teams.getTeam(teamAttribute);
            }
        }

        @Override
        public FilterResult filter(Object object) {
            if (team == null) {
                return FilterResult.ABSTAIN;
            } else if (object instanceof ArcadePlayer) {
                return FilterResult.of(teams.getTeam(teams.getGame().getPlayer(((ArcadePlayer) object).getUuid())).equals(this.team));
            } else if (object instanceof GamePlayer) {
                return FilterResult.of(teams.getTeam((GamePlayer) object).equals(this.team));
            } else if (object instanceof Player) {
                return FilterResult.of(teams.getTeam(teams.getGame().getPlayer(((Player) object).getUniqueId())).equals(this.team));
            }

            return FilterResult.ABSTAIN;
        }
    }
}
