package OblivionEngine.content.core.Units;

//distributionUrl=https\://services.gradle.org/distributions/gradle-8.10.2-bin.zip
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.BlockFlag;

public class OEUnits {
    public static UnitType awa;
    //public static OEPayloadItems qwq;
    public static UnitType depleted_uranium,ingot;

    public static void load(){
//        qwq = new OEPayloadItems() {
//
//        };
        depleted_uranium = new UnitType("depleted_uranium") {
            {
                health = 1f;
                speed = 0f;
                researchCostMultiplier = 0.5f;
                weapons.clear();
                targetAir = false;
                targetGround = false;
                itemCapacity = 0;
                hitSize = 16f;
                drawCell = false;
                flying = false;
                researchCostMultiplier = 0.5f;
                drawSoftShadow = false;
                autoFindTarget = false;
                useUnitCap = false;
                logicControllable = false;
                playerControllable = false;
                controlSelectGlobal = false;
            }
        };
        depleted_uranium.constructor = UnitEntity::create;
        ingot = new UnitType("ingot") {
            {
                health = 50f;
                speed = 0f;
                researchCostMultiplier = 0.5f;
                weapons.clear();
                targetAir = false;
                targetGround = false;
                itemCapacity = 0;
                hitSize = 8f;
                drawCell = false;
                flying = false;
                researchCostMultiplier = 0.5f;
            }
        };
        ingot.constructor = UnitEntity::create;
        awa = new UnitType("awa"){
            {
                this.speed = 1.14F;
                this.accel = 0.04F;
                this.drag = 0.04F;
                this.rotateSpeed = 1.0F;
                this.flying = true;
                this.lowAltitude = true;
                this.health = 22000.0F;
                this.engineOffset = 38.0F;
                this.engineSize = 7.3F;
                this.hitSize = 20F;
                this.armor = 13.0F;
                this.targetFlags = new BlockFlag[]{BlockFlag.reactor, BlockFlag.battery, BlockFlag.core, null};
                this.ammoType = new ItemAmmoType(Items.surgeAlloy);
                final BulletType fragBullet = new FlakBulletType(4.0F, 15.0F) {
                    {
                        this.shootEffect = Fx.shootBig;
                        this.ammoMultiplier = 5.5F;
                        this.splashDamage = 65.0F;
                        this.splashDamageRadius = 25.0F;
                        this.collidesGround = true;
                        this.lifetime = 47.0F;
                        this.status = StatusEffects.blasted;
                        this.statusDuration = 60.0F;
                    }
                };
                this.weapons.add(new Weapon("large-laser-mount") {
                    {
                        this.shake = 4.0F;
                        this.shootY = 9.0F;
                        this.x = 18.0F;
                        this.y = 5.0F;
                        this.rotateSpeed = 2.0F;
                        this.reload = 45.0F;
                        this.recoil = 4.0F;
                        this.shootSound = Sounds.laser;
                        this.shadow = 20.0F;
                        this.rotate = true;
                        this.bullet = new LaserBulletType() {
                            {
                                this.damage = 115.0F;
                                this.sideAngle = 20.0F;
                                this.sideWidth = 1.5F;
                                this.sideLength = 80.0F;
                                this.width = 25.0F;
                                this.length = 2300.0F;
                                this.shootEffect = Fx.shockwave;
                                this.colors = new Color[]{Color.valueOf("ec7458aa"), Color.valueOf("ff9c5a"), Color.white};
                            }
                        };
                    }
                }, new Weapon("large-artillery") {
                    {
                        this.x = 11.0F;
                        this.y = 27.0F;
                        this.rotateSpeed = 2.0F;
                        this.reload = 9.0F;
                        this.shootSound = Sounds.shoot;
                        this.shadow = 7.0F;
                        this.rotate = true;
                        this.recoil = 0.5F;
                        this.shootY = 7.25F;
                        this.bullet = fragBullet;
                    }
                }, new Weapon("large-artillery") {
                    {
                        this.y = -13.0F;
                        this.x = 20.0F;
                        this.reload = 12.0F;
                        this.ejectEffect = Fx.casing1;
                        this.rotateSpeed = 7.0F;
                        this.shake = 1.0F;
                        this.shootSound = Sounds.shoot;
                        this.rotate = true;
                        this.shadow = 12.0F;
                        this.shootY = 7.25F;
                        this.bullet = fragBullet;
                    }
                });
            }
        };
        awa.constructor = UnitEntity::create;
    }
}
