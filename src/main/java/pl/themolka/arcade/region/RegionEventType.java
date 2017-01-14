package pl.themolka.arcade.region;

public enum RegionEventType {
    BLOCK("build"),
    BLOCK_BREAK("block-break"),
    BLOCK_PLACE("block-place"),
    BLOCK_PLACE_AGAINST("block-place-against"),
    BLOCK_PHYSICS("block-physics"),
    ENTER("enter"),
    LEAVE("leave"),
    MESSAGE("message"),
    USE("use"),
    ;

    private final String attribute;

    RegionEventType(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return this.attribute;
    }
}
