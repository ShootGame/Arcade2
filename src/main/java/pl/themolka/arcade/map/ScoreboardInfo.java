package pl.themolka.arcade.map;

public class ScoreboardInfo implements Cloneable {
    private String title;

    @Override
    public ScoreboardInfo clone() {
        try {
            ScoreboardInfo clone = (ScoreboardInfo) super.clone();
            clone.title = this.title;
            return clone;
        } catch (CloneNotSupportedException clone) {
            throw new Error(clone);
        }
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
