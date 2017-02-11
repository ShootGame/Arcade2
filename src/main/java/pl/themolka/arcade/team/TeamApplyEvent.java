package pl.themolka.arcade.team;

import org.jdom2.Attribute;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;

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

    public static TeamApplyEvent[] ofCodeMany(List<Attribute> xml) {
        List<TeamApplyEvent> results = new ArrayList<>();
        for (Attribute attribute : xml) {
            TeamApplyEvent event = ofCode(attribute.getName());
            if (event != null && XMLParser.parseBoolean(attribute.getValue())) {
                results.add(event);
            }
        }

        return results.toArray(new TeamApplyEvent[results.size()]);
    }
}
