package pl.themolka.arcade.goal;

public interface Goal {
    String getName();

    boolean isScored();

    void setScored(boolean scored);
}
