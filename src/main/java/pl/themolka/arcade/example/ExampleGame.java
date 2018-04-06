package pl.themolka.arcade.example;

import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.GameModule;
import pl.themolka.arcade.game.IGameModuleConfig;

public class ExampleGame extends GameModule {
    protected ExampleGame() {
    }

    public interface Config extends IGameModuleConfig<ExampleGame> {
        @Override
        default ExampleGame create(Game game, Library library) {
            return new ExampleGame();
        }
    }
}
