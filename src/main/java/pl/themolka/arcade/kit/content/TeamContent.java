package pl.themolka.arcade.kit.content;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.match.Observers;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.NestedParserName;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;
import pl.themolka.arcade.team.Team;
import pl.themolka.arcade.util.Nulls;

public class TeamContent implements RemovableKitContent<Team> {
    private final Team result;
    private final boolean announce;

    protected TeamContent(Game game, IGameConfig.Library library, Config config) {
        this.result = Nulls.defaults(library.getOrDefine(game, config.result().getIfPresent()), this.defaultValue());
        this.announce = config.announce().get();
    }

    @Override
    public boolean isApplicable(GamePlayer player) {
        return KitContent.test(player);
    }

    @Override
    public void attach(GamePlayer player, Team value) {
        if (value != null) {
            value.join(player, this.announce(), true);
        }
    }

    @Override
    public Team defaultValue() {
        return null;
    }

    @Override
    public Team getResult() {
        return this.result;
    }

    public boolean announce() {
        return this.announce;
    }

    @NestedParserName("team")
    @Produces(Config.class)
    public static class ContentParser extends BaseRemovableContentParser<Config>
                                      implements InstallableParser {
        private Parser<Boolean> announceParser;
        private Parser<Ref> teamParser;

        @Override
        public void install(ParserContext context) throws ParserNotSupportedException {
            super.install(context);
            this.announceParser = context.type(Boolean.class);
            this.teamParser = context.type(Ref.class);
        }

        @Override
        protected Result<Config> parseNode(Node node, String name, String value) throws ParserException {
            Ref<Team.Config> team = this.reset(node) ? Config.DEFAULT_TEAM
                                                     : this.teamParser.parseWithDefinition(node, name, value).orFail();
            boolean announce = this.announceParser.parse(node.property("announce", "message")).orDefault(Config.DEFAULT_ANNOUNCE);

            return Result.fine(node, name, value, new Config() {
                public Ref<Team.Config> result() { return team; }
                public Ref<Boolean> announce() { return Ref.ofProvided(announce); }
            });
        }
    }

    public interface Config extends RemovableKitContent.Config<TeamContent, Team.Config> {
        Ref<Team.Config> DEFAULT_TEAM = Ref.of(Observers.OBSERVERS_TEAM_ID);
        boolean DEFAULT_ANNOUNCE = true;

        default Ref<Team.Config> result() { return DEFAULT_TEAM; }
        default Ref<Boolean> announce() { return Ref.ofProvided(DEFAULT_ANNOUNCE); }

        @Override
        default TeamContent create(Game game, Library library) {
            return new TeamContent(game, library, this);
        }
    }
}
