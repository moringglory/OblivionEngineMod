package oblivionengine.content;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import oblivionengine.OblivionEngine;

public class OEContent {
    public static TextureRegion boot,exit;
    public static void load() {
        boot = Core.atlas.find(OblivionEngine.name("boot"));
        exit = Core.atlas.find(OblivionEngine.name("exit"));
    }
}
