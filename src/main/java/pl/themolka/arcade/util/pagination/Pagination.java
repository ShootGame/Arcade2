/*
 * Copyright 2018 Aleksander Jagiełło
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
