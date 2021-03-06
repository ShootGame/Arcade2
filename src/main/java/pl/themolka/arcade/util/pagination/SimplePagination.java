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

import java.util.ArrayList;
import java.util.List;

public abstract class SimplePagination<T> implements Pagination<T> {
    private int extraItemsPerPage = EXTRA_ITEMS_PER_PAGE;
    private final List<T> items = new ArrayList<>();
    private int itemsPerPage = ITEMS_PER_PAGE;

    public SimplePagination() {
        this(ITEMS_PER_PAGE);
    }

    public SimplePagination(int itemsPerPage) {
        this(EXTRA_ITEMS_PER_PAGE, itemsPerPage);
    }

    public SimplePagination(int extraItemsPerPage, int itemsPerPage) {
        this.extraItemsPerPage = extraItemsPerPage;
        this.itemsPerPage = itemsPerPage;
    }

    @Override
    public void display(Sender sender, int page) {
        String header = this.formatHeader(page);
        if (header != null) {
            sender.send(header);
        }

        List<T> items = this.getItems(page);
        for (int i = 0; i < items.size(); i++) {
            String item = this.formatItem(i + ((page - 1) * this.getItemsPerPage() + 1), items.get(i));
            if (item != null) {
                sender.send(item);
            }
        }
    }

    @Override
    public int getExtraItemsPerPage() {
        return this.extraItemsPerPage;
    }

    @Override
    public List<T> getItems() {
        return this.items;
    }

    @Override
    public List<T> getItems(int page) {
        int firstIndex = (page - 1) * this.getItemsPerPage();
        int lastIndex = firstIndex + this.getItemsPerPage();

        if (lastIndex + this.getExtraItemsPerPage() >= this.getItemsSize() + 1) {
            lastIndex = this.getItemsSize();
        }

        return this.getItems().subList(firstIndex, lastIndex);
    }

    @Override
    public int getItemsPerPage() {
        return this.itemsPerPage;
    }

    @Override
    public int getPages() {
        int pages = (this.getItemsSize() / this.getItemsPerPage()) + 2;
        if (pages != 1) {
            int extra = this.getItemsSize() - (pages * this.getItemsPerPage());
            if (extra <= this.getExtraItemsPerPage()) {
                pages--;
            }
        }
        return pages;
    }

    @Override
    public void setItems(List<T> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void setExtraItemsPerPage(int extraItemsPerPage) {
        this.extraItemsPerPage = extraItemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }
}
