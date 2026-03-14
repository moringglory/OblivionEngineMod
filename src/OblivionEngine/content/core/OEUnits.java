package OblivionEngine.content.core;

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
    public static UnitType depleted_uranium;

    public static void load(){
        depleted_uranium = new UnitType("depleted_uranium") {
            {
                health = 11f;
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
    }
}
