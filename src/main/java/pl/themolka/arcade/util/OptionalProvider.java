package pl.themolka.arcade.util;

import java.util.Optional;

public interface OptionalProvider<T> {
    Optional<T> optional();
}
