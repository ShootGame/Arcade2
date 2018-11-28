package pl.themolka.arcade.dom;

import pl.themolka.arcade.ArcadePlugin;

import java.util.Objects;

/**
 * Representation of a DOM element namespace.
 */
public class Namespace implements Content {
    public static final String DEFAULT_ID = "arcade";
    public static final String DEFAULT_MANUAL_URI = ArcadePlugin.PROJECT_WEBSITE;

    private static final Namespace defaultNamespace = new Namespace(DEFAULT_ID, DEFAULT_MANUAL_URI) {
        @Override
        public String toShortString() {
            // This is the default namespace, so skip printing it.
            return null;
        }
    };

    private final String id;
    private final String manualUri;

    private Namespace(String id, String manualUri) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.manualUri = manualUri;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public Selection select() {
        throw new UnsupportedOperationException("Namespace may not be selected");
    }

    @Override
    public String toShortString() {
        return this.id;
    }

    public String format(Element element) {
        return this.format(Objects.requireNonNull(element, "element cannot be null").getName());
    }

    public String format(String name) {
        return format(this.toShortString(), name);
    }

    public String getId() {
        return this.id;
    }

    public String getManualUri() {
        return this.manualUri;
    }

    @Override
    public String toString() {
        return this.format(this.manualUri);
    }

    public static String format(String namespace, String name) {
        String prefix = namespace != null && !namespace.isEmpty() ? namespace + ":" : "";
        return prefix + Objects.requireNonNull(name, "name cannot be null");
    }

    //
    // Instancing
    //

    public static Namespace getDefault() {
        return defaultNamespace;
    }

    public static Namespace of(String id) {
        return of(id, null);
    }

    public static Namespace of(String id, String manualUri) {
        return new Namespace(id, manualUri);
    }
}
