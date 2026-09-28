package oblivionengine.content;

import oblivionengine.OblivionEngine;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import mindustry.gen.Icon;

public class OEContent {
    public static TextureRegion boot, exit, parts;

    public static void load() {
        parts = Core.atlas.find("oblivion-engine-parts");
        boot = Core.atlas.find("oblivion-engine-boot");
        exit = Core.atlas.find("oblivion-engine-exit");

        if (parts.found()) Icon.icons.put("oblivion-engine-parts", new TextureRegionDrawable(parts));
        if (boot.found())      Icon.icons.put("oblivion-engine-boot",  new TextureRegionDrawable(boot));
        if (exit.found())      Icon.icons.put("oblivion-engine-exit",  new TextureRegionDrawable(exit));
    }
}