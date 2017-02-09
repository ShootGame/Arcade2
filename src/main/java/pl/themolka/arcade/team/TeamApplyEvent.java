package pl.themolka.arcade.team;

public enum TeamApplyEvent {
    JOIN("on-join"),
    RESPAWN("on-respawn"),
    START("on-start"),
    ;

    private final String[] code;

    TeamApplyEvent(String... code) {
        this.code = code;
    }

    public boolean contains(String query) {
        for (String code : this.getCode()) {
            if (code.equalsIgnoreCase(query)) {
                return true;
            }
        }

        return false;
    }

    public String[] getCode() {
        return this.code;
    }

    public static TeamApplyEvent ofCode(String code) {
        for (TeamApplyEvent value : values()) {
            if (value.contains(code)) {
                return value;
            }
        }

        return null;
    }
}
