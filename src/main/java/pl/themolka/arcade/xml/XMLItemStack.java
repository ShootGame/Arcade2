package pl.themolka.arcade.xml;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

public class XMLItemStack extends XMLParser {
    public static final String ATTRIBUTE_AMOUNT = "amount";

    public static final int AMOUNT = 1;

    public static ItemStack parse(Element xml) throws DataConversionException {
        if (xml != null) {
            Material paramMaterial = XMLMaterial.parse(xml);
            int paramAmount = getAttribute(xml, ATTRIBUTE_AMOUNT, AMOUNT).getIntValue();

            ItemStack item = new ItemStack(paramMaterial);
            item.setAmount(paramAmount);
            return item;
        }

        return null;
    }
}
