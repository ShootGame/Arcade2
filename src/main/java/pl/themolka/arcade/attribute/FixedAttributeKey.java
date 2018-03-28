package pl.themolka.arcade.attribute;

import java.util.Objects;

public class FixedAttributeKey extends AttributeKey {
    public static final String DEFAULT_NAMESPACE = "arcade";
    public static final String NAMESPACE_SEPARATOR = ".";

    private final String namespace;
    private final String key;

    protected FixedAttributeKey(String namespace, String key) {
        this.namespace = requireNamespace(namespace);
        this.key = requireKey(key);
    }

    @Override
    public String key() {
        return computeKey(this.namespace, this.key);
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getKey() {
        return this.key;
    }

    public static String computeKey(String key) {
        return computeKey(DEFAULT_NAMESPACE, key);
    }

    public static String computeKey(String namespace, String key) {
        return computeKey(namespace, NAMESPACE_SEPARATOR, key);
    }

    public static String computeKey(String namespace, String separator, String key) {
        return requireNamespace(namespace) + Objects.requireNonNull(separator, "separator cannot be null") + requireKey(key);
    }

    private static String requireNamespace(String namespace) {
        return Objects.requireNonNull(namespace, "namespace cannot be null");
    }

    private static String requireKey(String key) {
        return Objects.requireNonNull(key, "key cannot be null");
    }
}
