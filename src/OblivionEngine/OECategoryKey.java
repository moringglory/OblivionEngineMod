package oblivionengine;

import arc.Core;
import arc.input.KeyBind;
import arc.input.KeyCode;

public class OECategoryKey {
    public static final KeyBind oeSelectPart = KeyBind.add("oe-select-part", KeyCode.num1, "oblivion-engine");

    private static boolean wasDown = false;

    public static void tick() {
        boolean now = Core.input.keyDown(oeSelectPart);
        if (now && !wasDown) {
            OECategoryUI.openParts();
        }
        wasDown = now;
    }
}