package oblivionengine.content;

import arc.Core;
import arc.input.KeyBind;
import arc.input.KeyCode;
import mindustry.Vars;

import java.lang.reflect.Field;

public class OEKey {
    public static final KeyBind oeSelectPart = KeyBind.add("oe-select-part", KeyCode.num1, "oblivion-engine");
    public static final KeyBind oeSelectSpecial = KeyBind.add("oe-select-special", KeyCode.num8, "oblivion-engine");

    private static boolean wasDownPart = false;
    private static boolean wasDownSpecial = false;

    public static void register() {}

    public static void tick() {
        if (Core.scene.hasKeyboard()) return;

        boolean nowPart = Core.input.keyDown(oeSelectPart);
        if (nowPart && !wasDownPart) {
            select(OECategory.parts);
        }
        wasDownPart = nowPart;

        boolean nowSpecial = Core.input.keyDown(oeSelectSpecial);
        if (nowSpecial && !wasDownSpecial) {
            select(OECategory.oblivion_special);
        }
        wasDownSpecial = nowSpecial;
    }

    private static void select(mindustry.type.Category cat) {
        if (cat == null) return;
        Object frag = Vars.ui.hudfrag.blockfrag;
        if (frag == null) return;

        try {
            Field cf = frag.getClass().getDeclaredField("currentCategory");
            cf.setAccessible(true);
            cf.set(frag, cat);
        } catch (Throwable t) {
            arc.util.Log.err("[OE] set currentCategory failed", t);
            return;
        }

        ((mindustry.ui.fragments.PlacementFragment) frag).rebuild();
        OEDialog.OEUI.buildCustomCategories();
    }
}