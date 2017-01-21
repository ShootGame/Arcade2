package pl.themolka.arcade.util.pagination;

import pl.themolka.arcade.command.Sender;

import java.util.List;

public interface Pagination<T> {
    int EXTRA_ITEMS_PER_PAGE = 2;
    int ITEMS_PER_PAGE = 8;

    default void display(Sender sender) {
        this.display(sender, 1);
    }

    void display(Sender sender, int page);

    String formatHeader(int page);

    String formatItem(int index, T item);

    default int getExtraItemsPerPage() {
        return EXTRA_ITEMS_PER_PAGE;
    }

    default T getItem(int index) {
        return this.getItems().get(index);
    }

    List<T> getItems();

    List<T> getItems(int page);

    default int getItemsPerPage() {
        return ITEMS_PER_PAGE;
    }

    default int getItemsSize() {
        return this.getItems().size();
    }

    int getPages();

    void setItems(List<T> items);
}
