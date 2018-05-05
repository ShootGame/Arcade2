package pl.themolka.arcade.objective.core;

import org.bukkit.block.Block;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public class CoreBreakEvent extends CoreEvent implements Cancelable {
    private final Block block;
    private final GamePlayer breaker;
    private boolean cancel;
    private final Participator participator;

    public CoreBreakEvent(Core core, Block block, GamePlayer breaker, Participator participator) {
        super(core);

        this.block = block;
        this.breaker = breaker;
        this.participator = participator;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Block getBlock() {
        return this.block;
    }

    public GamePlayer getBreaker() {
        return this.breaker;
    }

    public Participator getParticipator() {
        return this.participator;
    }
}
