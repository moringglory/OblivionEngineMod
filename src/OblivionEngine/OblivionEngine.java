package OblivionEngine;

import OblivionEngine.content.core.OEStatusEffects;
import OblivionEngine.expand.ui.OEUITools;
import arc.Core;
import OblivionEngine.content.OEPlanets;
import OblivionEngine.content.OETechTree;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;

import OblivionEngine.content.core.OEItems;
import OblivionEngine.content.core.OEBlocks;
import OblivionEngine.content.core.OEUnits;
import OblivionEngine.content.OEContent;

public class OblivionEngine extends Mod {
    public static final String MOD_NAME = "oblivion-engine";
    public OblivionEngine() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
//            Time.run(1f, () -> {}
            OEUITools.PrintOEInformation("Loaded OblivionEngine version: "+Vars.mods.locateMod(MOD_NAME).meta.version);
            BaseDialog dialog = new BaseDialog("Mod 已加载");
            dialog.cont.image(Core.atlas.find("oblivion-engine-frog")).pad(20f).row();
            dialog.cont.add("OblivionEngine 模组已成功加载！");
            dialog.buttons.button("goon",()->{dialog.hide();}).size(100f,50f);
            dialog.show();
            dialog.closeOnBack();
        });
    }

    public static String name(String name) {
        return MOD_NAME + "-" + name;
    }

    @Override
    public void loadContent() {
        OEStatusEffects.load();
        OEContent.load();
        OEItems.load();
        OEUnits.load();
        OEBlocks.load();
        OEPlanets.load();
        OETechTree.load();
    }

    @Override
    public void init() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            if (Vars.netServer != null) {
                Vars.netServer.admins.addChatFilter((player, text) -> text.replace("java", "jvav"));
            }
        });
        OEVars.init();
    }
}
