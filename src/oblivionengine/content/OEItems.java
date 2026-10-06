package oblivionengine.content;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;

public class OEItems {
    public static Item Uranium,Sulfur,Item;
    public static void load(){
        Uranium = new Item("Uranium"){{
            alwaysUnlocked = false;
            cost = 0.5f;
            hardness = 5;
            color = Color.valueOf("#54D77D");
        }};

        Sulfur = new Item("Sulfur"){{
            alwaysUnlocked = false;
            cost = 0.5f;
            hardness = 1;
            color = Items.carbide.color;
        }};

        Item = new Item("testitem"){{
            alwaysUnlocked = false;
            cost = 0.01f;
            hardness = 255;
            color = Color.valueOf("#800080");
        }};
    }
}
