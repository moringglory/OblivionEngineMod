package oblivionengine;

import arc.Core;
import arc.Events;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.mod.Mod;
import mindustry.net.Net;
import mindustry.ui.dialogs.BaseDialog;

import oblivionengine.content.OEPlanets;
import oblivionengine.content.OESounds;
import oblivionengine.content.OETechTree;
import oblivionengine.expand.ui.OEUITools;
import oblivionengine.content.core.OEStatusEffects;
import oblivionengine.content.core.OEItems;
import oblivionengine.content.core.OEBlocks;
import oblivionengine.content.core.OEUnits;
import oblivionengine.content.OEContent;

public class OblivionEngine extends Mod {
    public static final String MOD_NAME = "oblivion-engine";

    public OblivionEngine() {
        super();
        OECategory.class.getName();
        Events.on(EventType.ClientLoadEvent.class, e -> {
            OEUITools.PrintOEInformation("Loaded OblivionEngine version: " + Vars.mods.locateMod(MOD_NAME).meta.version);
            BaseDialog dialog = new BaseDialog("Mod 已加载");
            dialog.cont.image(Core.atlas.find("oblivion-engine-parts")).pad(20f).row();
            dialog.cont.add("OblivionEngineMod 已成功加载！");
            dialog.buttons.button("goon", () -> dialog.hide()).size(100f, 50f);
            dialog.show();
            dialog.closeOnBack();
        });
    }

    public static String name(String name) {
        return MOD_NAME + "-" + name;
    }

    @Override
    public void loadContent() {
        OESounds.load();
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
        OECategoryKey.register();
        Timer.schedule(OECategoryKey::tick, 0f, 1f / 60f);

        OEVars.init();
    }
}