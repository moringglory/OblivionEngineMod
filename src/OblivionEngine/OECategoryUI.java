package oblivionengine;

import arc.Core;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Table;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.Block;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class OECategoryUI {
    private static ImageButton dragBtn;
    private static ImageButton mainBtn;
    private static final ArrayList<ImageButton> blockBtns = new ArrayList<>();
    private static boolean panelOpen = false;
    private static boolean added = false;
    private static boolean databaseInjected = false;

    private static float baseX = 20;
    private static float baseY = 20;
    private static final float BTN = 50;
    private static final float GAP = 4;

    public static void fix() {
        Timer.schedule(() -> {
            if (added) return;
            if (Vars.ui == null || Vars.ui.hudGroup == null) return;
            try {
                build();
                added = true;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0f, 1f);
    }

    public static void tick() {
        tryDatabaseButton();
    }

    public static void openParts() {
        try {
            if (Vars.ui == null || Vars.ui.hudfrag == null) return;
            Object frag = Vars.ui.hudfrag.blockfrag;
            if (frag == null) return;

            Field f = frag.getClass().getDeclaredField("currentCategory");
            f.setAccessible(true);
            f.set(frag, OECategory.parts);

            Method m = frag.getClass().getDeclaredMethod("rebuild");
            m.setAccessible(true);
            m.invoke(frag);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void openSpecial() {
        try {
            if (Vars.ui == null || Vars.ui.hudfrag == null) return;
            Object frag = Vars.ui.hudfrag.blockfrag;
            if (frag == null) return;

            Field f = frag.getClass().getDeclaredField("currentCategory");
            f.setAccessible(true);
            f.set(frag, OECategory.OBLIVION_SPECIAL);

            Method m = frag.getClass().getDeclaredMethod("rebuild");
            m.setAccessible(true);
            m.invoke(frag);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void tryDatabaseButton() {
        try {
            if (Vars.ui == null) return;
            Object db = Vars.ui.database;
            if (db == null) return;

            boolean shown = false;
            try {
                Method m = db.getClass().getMethod("isShown");
                shown = (boolean) m.invoke(db);
            } catch (Throwable ignored) {}

            if (!shown) {
                databaseInjected = false;
                return;
            }
            if (databaseInjected) return;
            if (injectDatabaseButton(db)) databaseInjected = true;
        } catch (Throwable ignored) {}
    }

    private static boolean injectDatabaseButton(Object db) {
        try {
            Field contField = null;
            Class<?> c = db.getClass();
            while (c != null && contField == null) {
                try {
                    contField = c.getDeclaredField("cont");
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                }
            }
            if (contField == null) return false;
            contField.setAccessible(true);
            Object contObj = contField.get(db);
            if (!(contObj instanceof Table)) return false;
            Table cont = (Table) contObj;
            if (cont.find("oe-db-btn") != null) return true;

            ImageButton btn = new ImageButton(Icon.menu, Styles.clearNonei);
            btn.name = "oe-db-btn";
            btn.clicked(OECategoryUI::openParts);
            cont.add(btn).size(50).pad(4);
            cont.invalidateHierarchy();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void build() {
        dragBtn = new ImageButton(Icon.move, Styles.defaulti);
        dragBtn.setSize(BTN, BTN);
        dragBtn.addListener(new ElementGestureListener() {
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                float groupW = getGroupWidth();
                float groupH = getGroupHeight();

                baseX += deltaX;
                baseY += deltaY;

                baseX = Math.max(0, Math.min(baseX, Core.graphics.getWidth() - groupW));
                baseY = Math.max(groupH - BTN, Math.min(baseY, Core.graphics.getHeight() - BTN));

                updatePositions();
            }
        });
        Vars.ui.hudGroup.addChild(dragBtn);

        mainBtn = new ImageButton(Icon.menu, Styles.defaulti);
        mainBtn.setSize(BTN, BTN);
        mainBtn.clicked(OECategoryUI::togglePanel);
        Vars.ui.hudGroup.addChild(mainBtn);

        for (Block b : OECategory.specialBlocks) {
            ImageButton btn = new ImageButton(b.uiIcon, Styles.nodei);
            btn.setSize(BTN, BTN);
            btn.clicked(() -> Vars.control.input.block = b);
            btn.visible = false;
            Vars.ui.hudGroup.addChild(btn);
            blockBtns.add(btn);
        }

        updatePositions();
        Vars.ui.hudGroup.invalidate();
    }

    private static float getGroupWidth() {
        float topRow = BTN * 2 + GAP;
        if (!panelOpen || blockBtns.isEmpty()) return topRow;
        int n = blockBtns.size();
        float bottomRow = n * BTN + (n - 1) * GAP;
        return Math.max(topRow, bottomRow);
    }

    private static float getGroupHeight() {
        if (!panelOpen || blockBtns.isEmpty()) return BTN;
        return BTN * 2 + GAP;
    }

    private static void updatePositions() {
        dragBtn.setPosition(baseX, baseY);
        mainBtn.setPosition(baseX + BTN + GAP, baseY);

        float rowY = baseY - BTN - GAP;
        for (int i = 0; i < blockBtns.size(); i++) {
            blockBtns.get(i).setPosition(baseX + i * (BTN + GAP), rowY);
        }
    }

    private static void togglePanel() {
        panelOpen = !panelOpen;
        for (ImageButton b : blockBtns) {
            b.visible = panelOpen;
        }
        float groupW = getGroupWidth();
        float groupH = getGroupHeight();
        baseX = Math.max(0, Math.min(baseX, Core.graphics.getWidth() - groupW));
        baseY = Math.max(groupH - BTN, Math.min(baseY, Core.graphics.getHeight() - BTN));
        updatePositions();
        Vars.ui.hudGroup.invalidate();
    }
}