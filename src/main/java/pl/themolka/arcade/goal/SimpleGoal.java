package pl.themolka.arcade.goal;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.ChatColor;
import pl.themolka.arcade.config.Ref;
import pl.themolka.arcade.game.Game;
import pl.themolka.arcade.game.IGameConfig;
import pl.themolka.arcade.game.Participator;
import pl.themolka.arcade.game.ParticipatorResolver;

public abstract class SimpleGoal implements Goal, ParticipatorResolver.Injector {
    // The "ToStringStyle.SHORT_PREFIX_STYLE" strings are long and unreadable here.
    public static final ToStringStyle TO_STRING_STYLE = ToStringStyle.MULTI_LINE_STYLE;

    private final Game game;

    private boolean completed = false;
    private Participator completedBy = null;
    private String name;
    private Participator owner;
    private ParticipatorResolver participatorResolver = ParticipatorResolver.NULL;
    private boolean touched = false;

    @Deprecated
    public SimpleGoal(Game game, Participator owner) {
        this.game = game;

        this.owner = owner;
    }

    protected SimpleGoal(Game game, IGameConfig.Library library, Config<?> config) {
        this.game = game;
        this.name = config.name().get();
        this.owner = library.getOrDefine(game, config.owner().get());
    }

    @Override
    public String getColoredName() {
         return (this.hasOwner() ? this.getOwner().getColor().toChat().toString() : "") + this.getName();
    }

    @Override
    public Game getGame() {
        return this.game;
    }

    @Override
    public String getName() {
        return ChatColor.stripColor(this.hasName() ? this.name : this.getDefaultName());
    }

    @Override
    public Participator getOwner() {
        return this.owner;
    }

    @Override
    public void injectParticipatorResolver(ParticipatorResolver participatorResolver) {
        this.participatorResolver = participatorResolver;
    }

    @Override
    public boolean isCompletableBy(GoalHolder completer) {
        return Goal.completableByNonOwner(this, completer);
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public boolean isCompleted(GoalHolder completer) {
        return this.isCompleted() && (this.completedBy == null || this.completedBy.equals(completer));
    }

    @Override
    public boolean isUntouched() {
        return !this.isCompleted() && !this.touched;
    }

    @Override
    public void setCompleted(boolean completed, Participator completer) {
        if (this.completed != completed) {
            if (this.completed = completed) {
                this.completedBy = completer;
                this.complete(completer);
            } else {
                this.setCompleted(false);
                this.reset();
            }
        }
    }

    protected abstract void complete(Participator completer);

    public Participator getCompletedBy() {
        return this.completedBy;
    }

    public abstract String getDefaultName();

    public ParticipatorResolver getParticipatorResolver() {
        return this.participatorResolver;
    }

    public boolean hasOwner() {
        return this.owner != null;
    }

    public boolean hasName() {
        return this.name != null;
    }

    public void setCompleted(boolean completed) {
        this.setCompleted(completed, null);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(Participator owner) {
        this.owner = owner;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public interface Config<T extends SimpleGoal> extends IGameConfig<T> {
        Ref<String> name();
        Ref<Participator.Config<?>> owner();
    }
}
