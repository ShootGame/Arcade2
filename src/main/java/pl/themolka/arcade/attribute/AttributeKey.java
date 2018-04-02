package pl.themolka.arcade.attribute;

import java.util.Objects;

public abstract class AttributeKey {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AttributeKey && this.key().equals(((AttributeKey) obj).key());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key());
    }

    public abstract String key();

    @Override
    public String toString() {
        return this.key();
    }
}
