package pl.themolka.arcade.objective.core;

import org.bukkit.block.Block;
import pl.themolka.arcade.event.Cancelable;

public class CoreLeakEvent extends CoreEvent implements Cancelable {
    private final Block block;
    private boolean cancel;

    public CoreLeakEvent(Core core, Block block) {
        super(core);

        this.block = block;
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
}
