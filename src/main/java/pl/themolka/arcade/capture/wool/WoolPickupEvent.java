package pl.themolka.arcade.capture.wool;

import org.bukkit.inventory.ItemStack;
import pl.themolka.arcade.ArcadePlugin;
import pl.themolka.arcade.event.Cancelable;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.game.Participator;

public class WoolPickupEvent extends WoolEvent implements Cancelable {
    private boolean cancel;
    private final Participator competitor;
    private final boolean firstPickup;
    private final boolean firstCompetitorPickup;
    private final ItemStack item;
    private final GamePlayer picker;

    public WoolPickupEvent(ArcadePlugin plugin, Wool wool, Participator competitor, boolean firstPickup,
                           boolean firstCompetitorPickup, ItemStack item, GamePlayer picker) {
        super(plugin, wool);

        this.competitor = competitor;
        this.firstPickup = firstPickup;
        this.firstCompetitorPickup = firstCompetitorPickup;
        this.item = item;
        this.picker = picker;
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public Participator getCompetitor() {
        return this.competitor;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public GamePlayer getPicker() {
        return this.picker;
    }

    public boolean isFirstPickup() {
        return this.firstPickup;
    }

    public boolean isFirstCompetitorPickup() {
        return this.firstCompetitorPickup;
    }
}
