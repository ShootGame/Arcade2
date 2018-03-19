package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserResult;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.team.Team;
import pl.themolka.arcade.team.TeamsGame;
import pl.themolka.arcade.team.TeamsModule;

public class TeamContent implements RemovableKitContent<Ref<Team>> {
    public static final Ref<Team> DEFAULT_TEAM = Ref.of(Observers.OBSERVERS_TEAM_ID);

    private final Ref<Team> result;
    private final boolean announce;

    public TeamContent(String teamId, boolean announce) {
        this(Ref.of(teamId), announce);
    }

    public TeamContent(Team team, boolean announce) {
        this(Ref.ofProvided(team), announce);
    }

    private TeamContent(Ref<Team> result, boolean announce) {
        this.result = result;
        this.announce = announce;
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void attach(GamePlayer player, Ref<Team> value) {
        if (value.isProvided()) {
            this.attachProvided(player, value.get());
        } else {
            this.attachId(player, value.getId());
        }
    }

    private void attachId(GamePlayer player, String teamId) {
        TeamsGame module = (TeamsGame) player.getGame().getModule(TeamsModule.class);
        if (module != null && module.isEnabled()) {
            Team team = module.getTeam(teamId);
            if (team == null) {
                player.getGame().getPlugin().getLogger().severe("No team found for ID '" + teamId + "'");
                return;
            }

            this.attachProvided(player, team);
        }
    }

    private void attachProvided(GamePlayer player, Team team) {
        team.join(player, this.announce(), true);
    }

    @Override
    public Ref<Team> defaultValue() {
        return DEFAULT_TEAM;
    }

    @Override
    public Ref<Team> getResult() {
        return this.result;
    }

    public boolean announce() {
        return this.announce;
    }

    @NestedParserName("team")
    @Produces(TeamContent.class)
    public static class ContentParser extends BaseRemovableContentParser<TeamContent>
                                      implements InstallableParser {
        public static final boolean DEFAULT_ANNOUNCE = true;

        private Parser<Boolean> announceParser;
        private Parser<Ref> teamParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.announceParser = context.type(Boolean.class);
            this.teamParser = context.type(Ref.class);
        }

        @Override
        protected ParserResult<TeamContent> parsePrimitive(Node node, String name, String value) throws ParserException {
            boolean announce = this.announceParser.parse(node.property("announce", "message")).orDefault(DEFAULT_ANNOUNCE);
            Ref<Team> ref = this.reset(node) ? DEFAULT_TEAM : this.teamParser.parse(node).orFail();
            return ParserResult.fine(node, name, value, new TeamContent(ref, announce));
        }
    }
}
