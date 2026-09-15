package oblivionengine.content;

import oblivionengine.content.core.OEBlocks;
import oblivionengine.content.core.OEItems;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.game.Objectives;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;

import static mindustry.content.TechTree.node;
//
//public class OETechTree {
//    private static TechNode context = null;
//    public static Seq<TechNode> all = new Seq();
//    public static Seq<TechNode> roots = new Seq();
//
//    public static void load(){
//        Planets.serpulo.techTree = nodeRoot("TestTechTree",Blocks.coreShard,() -> {
//            nodeProduce(OEItems.Uranium);
//
//            node(OEBlocks.precursor);
//        });
//
////        addToNext(Blocks.graphitePress, () -> {
////            node(OETurretBlock.precursor,() -> {
////                node(OETurretBlock.precursoe);
////            });
////        });
//
////        addToNext(Items.copper,() -> {
////            nodeProduce(OEItems.Uranium);
////        });
//    }
//
//    /**
//     * 找到上一个父节点content 并添加
//     * @param content 父节点
//     * @param run 子节点后的
//     */
//    public static void addToNext(UnlockableContent content,Runnable run){
//        context = TechTree.all.find(t -> t.content == content);
//        run.run();
//    }
//
//    public static TechNode nodeRoot(String name, UnlockableContent content, Runnable children) {
//        return nodeRoot(name, content, false, children);
//    }
//
//    public static TechNode nodeRoot(String name, UnlockableContent content, boolean requireUnlock, Runnable children) {
//        TechNode root = node(content, content.researchRequirements(), children);
//        root.name = name;
//        root.requiresUnlock = requireUnlock;
//        roots.add(root);
//        return root;
//    }
//
//    public static TechNode node(UnlockableContent content, Runnable children) {
//        return node(content, content.researchRequirements(), children);
//    }
//
//    public static TechNode node(UnlockableContent content, ItemStack[] requirements, Runnable children) {
//        return node(content, requirements, (Seq)null, children);
//    }
//
//    public static TechNode node(UnlockableContent content, ItemStack[] requirements, Seq<Objectives.Objective> objectives, Runnable children) {
//        TechNode node = new TechNode(context, content, requirements);
//        if (objectives != null) {
//            node.objectives.addAll(objectives);
//        }
//
//        TechNode prev = context;
//        context = node;
//        children.run();
//        context = prev;
//        return node;
//    }
//
//    public static TechNode node(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children) {
//        return node(content, content.researchRequirements(), objectives, children);
//    }
//
//    public static TechNode node(UnlockableContent block) {
//        return node(block, () -> {
//        });
//    }
//
//    public static TechNode nodeProduce(UnlockableContent content, Seq<Objectives.Objective> objectives, Runnable children) {
//        return node(content, content.researchRequirements(), objectives.add(new Objectives.Produce(content)), children);
//    }
//
//    public static TechNode nodeProduce(UnlockableContent content, Runnable children) {
//        return nodeProduce(content, new Seq(), children);
//    }
//
//    public static TechNode nodeProduce(UnlockableContent content){
//        return nodeProduce(content,() -> {});
//    }
//}
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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
