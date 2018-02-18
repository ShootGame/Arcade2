package pl.themolka.arcade.score;

import org.jdom2.Element;
import pl.themolka.arcade.config.SimpleConfig;
import pl.themolka.arcade.xml.XMLParser;

public class ScoreConfig extends SimpleConfig<ScoreConfig> {
    public static final double NULL_LIMIT = Score.MAX;
    public static final ScoreConfig NULL_CONFIG = new ScoreConfig(null, null,
            Score.DEFAULT_GOAL_NAME, Score.ZERO, Score.ZERO, Score.ZERO, NULL_LIMIT);

    private final String name;
    private final Double initialScore;
    private final Double killReward;
    private final Double deathLoss;
    private final Double limit;

    public ScoreConfig(String id, ScoreConfig parent,
                       String name, Double initialScore, Double killReward, Double deathLoss, Double limit) {
        super(id, parent);

        this.name = name;
        this.initialScore = initialScore;
        this.killReward = killReward;
        this.deathLoss = deathLoss;
        this.limit = limit;
    }

    public String getName() {
        if (this.name != null) {
            return this.name;
        } else if (this.hasParent()) {
            return this.getParent().getName();
        }

        return NULL_CONFIG.getName();
    }

    public double getInitialScore() {
        if (this.initialScore != null) {
            return this.initialScore;
        } else if (this.hasParent()) {
            return this.getParent().getInitialScore();
        }

        return NULL_CONFIG.getInitialScore();
    }

    public double getKillReward() {
        if (this.killReward != null) {
            return this.killReward;
        } else if (this.hasParent()) {
            return this.getParent().getKillReward();
        }

        return NULL_CONFIG.getKillReward();
    }

    public double getDeathLoss() {
        if (this.deathLoss != null) {
            return this.deathLoss;
        } else if (this.hasParent()) {
            return this.getParent().getDeathLoss();
        }

        return NULL_CONFIG.getDeathLoss();
    }

    public double getLimit() {
        if (this.limit != null) {
            return this.limit;
        } else if (this.hasParent()) {
            return this.getParent().getLimit();
        }

        return NULL_CONFIG.getLimit();
    }

    public boolean hasLimit() {
        return this.limit != NULL_LIMIT;
    }

    public static ScoreConfig parse(Element xml) {
        return parse(xml, NULL_CONFIG);
    }

    public static ScoreConfig parse(Element xml, ScoreConfig def) {
        String id = xml.getAttributeValue("id");
        String name = XMLParser.parseMessage(xml.getAttributeValue("name"));
        Double initialScore = XMLParser.parseDouble(xml.getAttributeValue("initial-score"));
        Double killReward = XMLParser.parseDouble(xml.getAttributeValue("kill-reward"));
        Double deathLoss = XMLParser.parseDouble(xml.getAttributeValue("death-loss"));
        Double limit = XMLParser.parseDouble(xml.getAttributeValue("limit"));

        return new ScoreConfig(id, def, name, initialScore, killReward, deathLoss, limit);
    }
}
