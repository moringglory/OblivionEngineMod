package oblivionengine.content.core;

import arc.graphics.Color;
import mindustry.type.Item;

public class OEItems {
    public static Item Uranium,Item;
    public static void load(){
        Uranium = new Item("Uranium"){{
            alwaysUnlocked = false;
            cost = 0.5f;
            hardness = 5;
            color = Color.valueOf("#54D77D");
        }};
        Item = new Item("testitem"){{
            alwaysUnlocked = false;
            cost = 0.01f;
            hardness = 255;
            color = Color.valueOf("#800080");
        }};
    }
}
