package pl.themolka.arcade.kit;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import pl.themolka.arcade.game.GamePlayer;
import pl.themolka.arcade.xml.XMLParser;

public class PermissionContent implements KitContent<String> {
    private final boolean reset;
    private final String result;
    private final boolean value;

    public PermissionContent(String result) {
        this(result, true);
    }

    public PermissionContent(boolean reset) {
        this(reset, null, false);
    }

    public PermissionContent(String result, boolean value) {
        this(false, result, value);
    }

    private PermissionContent(boolean reset, String result, boolean value) {
        this.reset = reset;
        this.result = result;
        this.value = value;
    }

    @Override
    public void apply(GamePlayer player) {
        // TODO
    }

    @Override
    public String getResult() {
        return this.result;
    }

    public boolean isReset() {
        return this.reset;
    }

    public boolean isValue() {
        return this.value;
    }

    public static class Parser implements KitContentParser<PermissionContent> {
        @Override
        public PermissionContent parse(Element xml) throws DataConversionException {
            Attribute reset = xml.getAttribute("reset");
            if (reset != null && XMLParser.parseBoolean(reset.getValue())) {
                return new PermissionContent(true);
            }

            Attribute value = xml.getAttribute("value");
            return new PermissionContent(xml.getTextNormalize(), value == null || XMLParser.parseBoolean(value.getValue()));
        }
    }
}
