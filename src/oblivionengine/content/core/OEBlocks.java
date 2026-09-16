package oblivionengine.content.core;

import mindustry.world.blocks.defense.Wall;
import oblivionengine.OECategory;
import oblivionengine.expand.Blocks.*;
import oblivionengine.expand.Payload.OEPayloadBlock;
import oblivionengine.content.OESounds;
import arc.graphics.Color;
import arc.util.Nullable;
import mindustry.content.*;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.ShapePart;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.units.Reconstructor;
import oblivionengine.content.OEFx;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootMulti;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class OEBlocks {
    public static Block
            //炮台
            precursor,
            //工厂
            centrifuge,excavator,
            //矿石
            UraniumOre,
            //智能量子计算机
            IQC_980D,ICQ_380D,T1,
            //核心
            core,
            //地形
            icefloor,
            //载荷产物(方块实现方法)
            stone_materiala,
            //零件
            small_crashing_wheel,
            //核心部件
            rockcrusher;
    public static @Nullable ItemStack outputItem;
    public static void load(){

        //炮台
        precursor=new ItemTurret("precursor"){{
            requirements(Category.turret, BuildVisibility.shown,with(Items.titanium, 150));
            alwaysUnlocked=false;
            size=2;
            health = 480 * size;
            reload = 7.5f;
            inaccuracy = 0.75f;
            recoil = 2f;
            coolant = consumeCoolant(0.2F);
            consumePower(512 / 60f);
            coolantMultiplier = 2.5f;
            shootSound = Sounds.shoot;
            velocityRnd = 0.075f;
            range = 250f;
            shoot = new ShootMulti(
                    new ShootPattern(),
                    new ShootBarrel() {{
                        barrels = new float[]{-6.5f, 3f, 0f};
                    }},
                    new ShootBarrel() {{
                        barrels = new float[]{6.5f, 3f, 0f};
                    }}
            );
            drawer = new DrawTurret() {{
                parts.add(new RegionPart("-shooter") {{
                    under = true;
                    outline = true;
                    moveY = -3f;
                    progress = PartProgress.recoil;
                }});
                parts.addAll(
                        new ShapePart(){{
//                            progress = circleProgress;
                            color = Color.blue;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 4f;
                            radius = 5f;
                            layer = Layer.effect;
                            y =12;
                            x = 11;
                        }},
                        new HaloPart(){{
//                            progress = circleProgress;
                            color = Color.blue;
                            tri = true;
                            shapes = 3;
                            triLength = 0f;
                            triLengthTo = 5f;
                            radius = 6f;
                            haloRadius = 11f;
                            haloRotateSpeed = 9f;
                            shapeRotation = 180f;
                            haloRotation = 180f;
                            layer = Layer.effect;
                            y = 1;
                            x = 12;
                        }}
                );
            }};
            ammo(
                    Items.surgeAlloy, new PointBulletType() {{
                        shootEffect = Fx.instShoot;
                        hitEffect = OEFx.triSpark1;
                        smokeEffect = Fx.smoke;
                        trailEffect = Fx.instTrail;
                        despawnEffect = Fx.instBomb;
                        trailSpacing = 20f;
                        damage = 320;
                        buildingDamageMultiplier = 1.5f;//对建筑的伤害
                        speed = 2;
                        hitShake = 6f;
                        ammoMultiplier = 1f;
                    }}
            );
        }};

        //工厂
        centrifuge = new Reconstructor("centrifuge"){{
            requirements(Category.units, with(Items.copper, 200, Items.lead, 120, Items.silicon, 90));

            size = 3;
            consumePower(3f);
            consumeItems(with(Items.silicon, 40, Items.graphite, 40));

            constructTime = 60f * 10f;

            upgrades.addAll(
                    new UnitType[]{OEUnits.depleted_uranium}
            );
        }};
        excavator = new OEExcavator("excavator"){{
//            Seq.with(stone_materiala,IQC_980D);
            requirements(Category.units, with(Items.titanium, 150));
            health = 480 * size;
            buildTime = 400f;
        }};

        //矿石
        UraniumOre = new OreBlock(OEItems.Uranium){{
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 26.680953f;
        }};

        //智能量子计算机
        IQC_980D = new OEIntelligentQuantumComputer("IntelligentQuantumComputer_980D"){{
            requirements(Category.logic, with(Items.silicon, 50, Items.beryllium, 75, Items.tungsten, 40));
            hasPower = true;
            size = 3;
            alwaysUnlocked = false;
        }};
        T1 = new T_EnvironmentTransformation("T1"){{
            requirements(Category.logic, with(Items.silicon, 50, Items.beryllium, 75, Items.tungsten, 40));
            size = 3;
            alwaysUnlocked = false;
        }};

        //核心
        core = new OELiquidCore("core"){{
            requirements(Category.production, with(Items.titanium, 150));
            size = 4;
            health = 480 * size;
            liquidCapacity = 700f;

        }};

        //地形
        icefloor = new Floor("ice-floor"){{
            variants = 0;
            walkSound = OESounds.ice_walk;
        }};

        //载荷产物(方块实现方法)
        stone_materiala = new OEPayloadBlock("stone_materiala"){{
            requirements(OECategory.OBLIVION_SPECIAL, BuildVisibility.hidden,with(OEItems.Item, 1));
            size = 3;
            health = 512 * size;
            alwaysUnlocked=false;
            breakable = false;
        }};

        //parts
        small_crashing_wheel = new Wall("Small_Crashing_Wheel"){{
            requirements(OECategory.OBLIVION_PARTS, BuildVisibility.shown, with(Items.copper, 50, Items.lead, 120, Items.silicon, 80));
            hideDatabase = false;
            databaseCategory = "parts";
            size = 3;
            health = 10000;
        }};

        //核心部件
        rockcrusher = new OEBlockReconstructor("rockcrusher"){{
            requirements(OECategory.OBLIVION_SPECIAL, BuildVisibility.hidden, with(OEItems.Item, 1));
            size = 3;
            researchCostMultiplier = 0.5f;
        }};

        //add to specialBlocks category
        OECategory.specialBlocks.add(small_crashing_wheel);
    }
}