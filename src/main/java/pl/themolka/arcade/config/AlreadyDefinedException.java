package pl.themolka.arcade.config;

public class AlreadyDefinedException extends RuntimeException {
    public AlreadyDefinedException() {
        this(null);
    }

    public AlreadyDefinedException(String id) {
        super(idToString(id) + " is already defined");
    }

    static String idToString(String id) {
        return id != null ? "'" + id + "'"
                          : "Given variable";
    }
}
