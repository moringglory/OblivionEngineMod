package OblivionEngine;

import arc.Core;
import OblivionEngine.content.OEPlanets;
import OblivionEngine.content.OETechTree;
import arc.Events;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;

import OblivionEngine.content.core.Items.OEItems;
import OblivionEngine.content.core.Blcoks.OEBlock;
import OblivionEngine.content.core.Units.OEUnits;
import OblivionEngine.content.OEContent;

public class OblivionEngine extends Mod {
    public static final String MOD_NAME = "OblivionEngine";
    public OblivionEngine() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            Time.run(1f, () -> {
                BaseDialog dialog = new BaseDialog("Mod 已加载");
                dialog.cont.image(Core.atlas.find("OblivionEngine-boot")).pad(20f).row();
                dialog.cont.add("Oblivion Engine 模组已成功加载！");
                Time.run(100f, dialog::addCloseButton);
                dialog.show();
            });
        });
    }

    public static String name(String name) {
        return MOD_NAME + "-" + name;
    }

    @Override
    public void loadContent() {
        OEContent.load();
        OEItems.load();
        OEUnits.load();
        OEBlock.load();
        OEPlanets.load();
        OETechTree.load();
    }
}
