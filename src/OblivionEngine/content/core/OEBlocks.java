package OblivionEngine.content.core;

import OblivionEngine.expand.Blocks.OEExcavator;
import OblivionEngine.expand.Blocks.T_EnvironmentTransformation;
import OblivionEngine.expand.Payload.OEPayloadBlock;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.content.*;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.ShapePart;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import OblivionEngine.content.OEFx;
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
import OblivionEngine.expand.Blocks.OEIntelligentQuantumComputer;

import static mindustry.type.ItemStack.with;

public class OEBlocks {
    public static Block
            //炮台
            precursor,
            //工厂
            rockcrusher,centrifuge,excavator,
            //矿石
            UraniumOre,
            //智能量子计算机
            IQC_980D,ICQ_380D,T1,
            //载荷产物(方块实现方法)
            stone_materiala;
    public static @Nullable ItemStack outputItem;
    public static void load(){
        precursor=new ItemTurret("precursor"){{
            requirements(Category.turret, BuildVisibility.shown,with(Items.titanium, 150));
            alwaysUnlocked=false;//默认解锁
            size=2;//边长
            health = 480 * size;//血量
            reload = 7.5f;//射击间隔
            inaccuracy = 0.75f;//偏差
            recoil = 2f;//后坐力
            coolant = consumeCoolant(0.2F);
            consumePower(512 / 60f);
            coolantMultiplier = 2.5f;
            shootSound = Sounds.swish;
            velocityRnd = 0.075f;//速度变化
            range = 250f;// 伤害
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
        stone_materiala = new OEPayloadBlock("stone_materiala"){{
            requirements(Category.units, BuildVisibility.editorOnly,with(Items.titanium, 150));
            size = 3;
            health = 512 * size;//血量
            alwaysUnlocked=false;//默认解锁
        }};

        rockcrusher = new UnitFactory("rockcrusher"){{
            requirements(Category.units, with(Items.copper, 50, Items.lead, 120, Items.silicon, 80));
            plans = Seq.with(
                    new UnitPlan(OEUnits.depleted_uranium, 60f * 15, with(Items.silicon, 10, Items.lead, 10))
//                    new UnitPlan(UnitTypes.crawler, 60f * 10, with(Items.silicon, 8, Items.coal, 10)),
//                    new UnitPlan(UnitTypes.nova, 60f * 40, with(Items.silicon, 30, Items.lead, 20, Items.titanium, 20))
            );
            size = 3;
            consumePower(1.2f);
            consumeLiquid(Liquids.water,1f);
            researchCostMultiplier = 0.5f;
        }};
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
        IQC_980D = new OEIntelligentQuantumComputer("IntelligentQuantumComputer_980D"){{
            requirements(Category.logic, with(Items.silicon, 50, Items.beryllium, 75, Items.tungsten, 40));
            hasPower = true;
            consumePower(2.5f);
            size = 3;
            alwaysUnlocked = false;
        }};
        T1 = new T_EnvironmentTransformation("T1"){{
            requirements(Category.logic, with(Items.silicon, 50, Items.beryllium, 75, Items.tungsten, 40));
            size = 3;
            alwaysUnlocked = false;
        }};
        UraniumOre = new OreBlock(OEItems.Uranium){{
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 26.680953f;
        }};
        excavator = new OEExcavator("excavator"){{
            requirements(Category.units,with(Items.titanium, 150));
            size = 16;
//            maxBlockSize = 9;
//            minBlockSize = 1;
            health = 480*size;
//            Seq.with(stone_materiala,IQC_980D);
            requirements(Category.units, with(Items.titanium, 150));
            size = 16;
            health = 480 * size;
            hasPower = true;
            consumePower(512 / 60f);
            consumeCoolant(0.2f);
            buildTime = 600f;
        }};
    }
}