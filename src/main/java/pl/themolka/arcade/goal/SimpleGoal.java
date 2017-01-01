package pl.themolka.arcade.goal;

public class SimpleGoal implements Goal {
    private String name;
    private boolean scored;

    public SimpleGoal() {
    }

    public SimpleGoal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isScored() {
        return this.scored;
    }

    @Override
    public void setScored(boolean scored) {
        this.scored = scored;
    }

    public void setName(String name) {
        this.name = name;
    }
}
