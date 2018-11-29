package pl.themolka.arcade.team;

import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.dom.Node;
import pl.themolka.arcade.game.GameModuleParser;
import pl.themolka.arcade.parser.InstallableParser;
import pl.themolka.arcade.parser.Parser;
import pl.themolka.arcade.parser.ParserContext;
import pl.themolka.arcade.parser.ParserException;
import pl.themolka.arcade.parser.ParserNotSupportedException;
import pl.themolka.arcade.parser.ParserUtils;
import pl.themolka.arcade.parser.Produces;
import pl.themolka.arcade.parser.Result;

import java.util.LinkedHashSet;
import java.util.Set;

@Produces(TeamsGame.Config.class)
public class TeamsGameParser extends GameModuleParser<TeamsGame, TeamsGame.Config>
                             implements InstallableParser {
    private Parser<Team.Config> teamParser;

    public TeamsGameParser() {
        super(TeamsGame.class);
    }

    @Override
    public Node define(Node source) {
        return source.firstChild("teams", "team");
    }

    @Override
    public void install(ParserContext context) throws ParserNotSupportedException {
        this.teamParser = context.type(Team.Config.class);
    }

    @Override
    protected Result<TeamsGame.Config> parseNode(Node node, String name, String value) throws ParserException {
        Set<Team.Config> teams = new LinkedHashSet<>();
        for (Node teamNode : node.children("team")) {
            teams.add(this.teamParser.parse(teamNode).orFail());
        }

        if (ParserUtils.ensureNotEmpty(teams)) {
            throw this.fail(node, name, value, "No teams defined");
        } else if (teams.size() > TeamsGame.Config.TEAMS_LIMIT) {
            throw this.fail(node, name, value, "Too many teams, reached limit of " + TeamsGame.Config.TEAMS_LIMIT + " teams");
        }

        return Result.fine(node, name, value, new TeamsGame.Config() {
            public Ref<Set<Team.Config>> teams() { return Ref.ofProvided(teams); }
        });
    }
}
