package pl.themolka.arcade.util.pagination;

import org.bukkit.ChatColor;
import pl.themolka.arcade.command.CommandUtils;
import pl.themolka.arcade.command.Sender;

public abstract class PrettyPagination<T> extends SimplePagination<T> {
    private String title;
    private String description;

    public PrettyPagination() {
        this(null);
    }

    public PrettyPagination(String title) {
        this(title, null);
    }

    public PrettyPagination(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public void display(Sender sender, int page) {
        super.display(sender, page);

        if (this.hasDescription() && page != this.getPages()) {
            sender.send(ChatColor.ITALIC + this.getDescription());
        }
    }

    @Override
    public String formatHeader(int page) {
        String title = "Results";
        if (this.hasTitle()) {
            title = this.getTitle();
        }

        return CommandUtils.createTitle(title, "Page " + page + "/" + this.getPages());
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean hasTitle() {
        return this.title != null;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
