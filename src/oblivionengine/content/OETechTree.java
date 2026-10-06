package oblivionengine.content;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;

import static mindustry.content.TechTree.node;

public class OETechTree {
    public static ObjectMap<UnitType, ItemStack[]> unitBuildCost = new ObjectMap();

    public static void nodeUnit(UnitType type, Runnable children) {
        node(type, (ItemStack[])unitBuildCost.get(type), children);
    }

    public static void nodeUnit(UnitType type, Seq<Objectives.Objective> objectives, Runnable children) {
        node(type, (ItemStack[])unitBuildCost.get(type), objectives, children);
    }

    public static void load() {
        TechTree.TechNode root = TechTree.nodeRoot("@planet.oblivion-engine-tome.name", OEPlanets.tome, () -> {
            TechTree.node(OEItems.Uranium, () -> {});
            TechTree.nodeProduce(OEBlocks.IQC_980D, () -> {});
            //TechTree.node(OEUnits.depleted_uranium, () -> {});
        });
        root.planet = OEPlanets.tome;

        root.children.each((c) -> c.planet = OEPlanets.tome);
    }
}
