package pl.themolka.arcade.score;

import org.jdom2.Element;
import pl.themolka.arcade.xml.XMLParser;

public class ScoreConfig {
    public static final double NULL_LIMIT = Score.MAX;
    public static final ScoreConfig NULL_CONFIG = new ScoreConfig(
            Score.DEFAULT_GOAL_NAME, Score.ZERO, Score.ZERO, Score.ZERO, NULL_LIMIT);

    private final String name;
    private final double initialScore;
    private final double killReward;
    private final double deathLoss;
    private final double limit;

    public ScoreConfig(String name, double initialScore, double killReward, double deathLoss, double limit) {
        this.name = name;
        this.initialScore = initialScore;
        this.killReward = killReward;
        this.deathLoss = deathLoss;
        this.limit = limit;
    }

    public String getName() {
        return this.name;
    }

    public double getInitialScore() {
        return this.initialScore;
    }

    public double getKillReward() {
        return this.killReward;
    }

    public double getDeathLoss() {
        return this.deathLoss;
    }

    public double getLimit() {
        return this.limit;
    }

    public boolean hasLimit() {
        return this.limit != NULL_LIMIT;
    }

    public static ScoreConfig parse(Element xml) {
        return parse(xml, NULL_CONFIG);
    }

    public static ScoreConfig parse(Element xml, ScoreConfig def) {
        String name = XMLParser.parseMessage(xml.getAttributeValue("name", def.name));
        double initialScore = XMLParser.parseDouble(xml.getAttributeValue("initial-score"), def.initialScore);
        double killReward = XMLParser.parseDouble(xml.getAttributeValue("kill-reward"), def.killReward);
        double deathLoss = XMLParser.parseDouble(xml.getAttributeValue("death-loss"), def.deathLoss);
        double limit = XMLParser.parseDouble(xml.getAttributeValue("limit"), def.limit);

        return new ScoreConfig(name, initialScore, killReward, deathLoss, limit);
    }
}
