package oblivionengine;

import arc.Core;
import oblivionengine.content.OEPlanets;
import oblivionengine.content.OETechTree;
import arc.Events;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;

import oblivionengine.content.core.Items.OEItems;
import oblivionengine.content.core.Blcoks.OEBlock;
import oblivionengine.content.core.Units.OEUnits;
import oblivionengine.content.OEContent;

public class OblivionEngine extends Mod {
    public static final String MOD_NAME = "oblivionengine";
    public OblivionEngine() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            Time.run(1f, () -> {
                BaseDialog dialog = new BaseDialog("Mod 已加载");
                dialog.cont.image(Core.atlas.find("oblivionengine-boot")).pad(20f).row();
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
