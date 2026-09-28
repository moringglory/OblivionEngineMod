package oblivionengine;

import arc.Core;
import arc.input.KeyBind;
import arc.input.KeyCode;

public class OECategoryKey {
    public static final KeyBind oeSelectPart = KeyBind.add("oe-select-part", KeyCode.num1, "oblivion-engine");
    public static final KeyBind oeSelectSpecial = KeyBind.add("oe-select-special", KeyCode.num8, "oblivion-engine");

    private static boolean wasDownPart = false;
    private static boolean wasDownSpecial = false;

    public static void register() {}

    public static void tick() {
        boolean nowPart = Core.input.keyDown(oeSelectPart);
        if (nowPart && !wasDownPart) OECategoryUI.openParts();
        wasDownPart = nowPart;

        boolean nowSpecial = Core.input.keyDown(oeSelectSpecial);
        if (nowSpecial && !wasDownSpecial) OECategoryUI.openSpecial();
        wasDownSpecial = nowSpecial;
    }
}