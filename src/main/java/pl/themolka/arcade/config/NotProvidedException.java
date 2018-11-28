package pl.themolka.arcade.config;

public class NotProvidedException extends RuntimeException {
    private final Ref<?> ref;

    public NotProvidedException(Ref<?> ref) {
        super();

        this.ref = ref;
    }

    public NotProvidedException(Ref<?> ref, String message) {
        super(message);

        this.ref = ref;
    }

    public Ref<?> getRef() {
        return this.ref;
    }
}
