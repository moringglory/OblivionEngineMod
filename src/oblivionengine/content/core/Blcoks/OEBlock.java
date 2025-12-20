package oblivionengine.content.core.Blcoks;

import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.Units;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.payloads.Constructor;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
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
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.BuildVisibility;
import oblivionengine.content.core.Items.OEItems;
import oblivionengine.content.core.Units.OEUnits;
import oblivionengine.expand.Blocks.OEIntelligentQuantumComputer;

import static mindustry.type.ItemStack.with;

public class OEBlock {
    public static Block
            //炮台
            precursor,
            //工厂
            rockcrusher,centrifuge,
            //矿石
            UraniumOre,
            //智能量子计算机
            IQC_980D,ICQ_380D;
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
                    new UnitType[]{OEUnits.depleted_uranium, OEUnits.ingot}
            );
        }};
        UraniumOre = new OreBlock(OEItems.Uranium){{
            oreDefault = true;
            oreThreshold = 0.882f;
            oreScale = 26.680953f;
        }};
        IQC_980D = new OEIntelligentQuantumComputer("IntelligentQuantumComputer_980D"){{
            requirements(Category.logic, with(Items.silicon, 50, Items.beryllium, 75, Items.tungsten, 40));
            hasPower = true;
            consumePower(2.5f);
            size = 3;
        }};
    }
}