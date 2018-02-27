package pl.themolka.arcade.map;

@Deprecated
public interface MapError {
    String getError();

    ArcadeMap getMap();

    String getProvider();
}
