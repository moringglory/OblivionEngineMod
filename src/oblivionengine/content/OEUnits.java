package oblivionengine.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.entities.bullet.*;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.type.Weapon;
import mindustry.gen.Sounds;
import mindustry.content.Fx;

public class OEUnits {
    public static UnitType depleted_uranium, pilot;

    public static void updateUnitOnIce(UnitType unit){
        if(unit == null) return;
    }


    public static void load(){
        depleted_uranium = new UnitType("depleted-uranium") {
            {
                health = 11f;
                speed = 0f;
                weapons.clear();
                targetAir = false;
                targetGround = false;
                itemCapacity = 0;
                hitSize = 10f;
                drawCell = false;
                flying = false;
                drawSoftShadow = false;
                autoFindTarget = false;
                useUnitCap = false;
                logicControllable = false;
                playerControllable = false;
                controlSelectGlobal = false;
            }
        };
        depleted_uranium.constructor = UnitEntity::create;

        pilot = new UnitType("pilot") {
            boolean is;
            public Seq<Weapon> tempWeapons = new Seq<>();
            {
                speed = 10f;
                accel = 0.06f;
                drag = 0.017f;
                lowAltitude = true;
                health = 1200f;
//                ammoType = new PowerAmmoType(110);
                engineOffset = 10.5f;
                buildSpeed = 2.6f;
                engineSize = 4f;
                targetAir = true;
                targetGround = false;
                itemCapacity = 600;
                hitSize = 16f;
                flying = true;
                weapons.add(new RepairBeamWeapon("repair-beam-weapon-center"){{
                    x = 0f;
                    y = -5.5f;
                    shootY = 6f;
                    beamWidth = 0.8f;
                    mirror = false;
                    repairSpeed = 0.75f;

                    bullet = new BulletType(){{
                        maxRange = 120f;
                    }};
                }});
                if(is) {
                    addTempWeapons();
                }
            }

            void addTempWeapons(){
                this.weapons.add(new Weapon("repair-beam-weapon-center"){{
                    x = 0f;
                    y = 0f;
                    mirror = false;
                    shootY = 5f;
                    reload = 40f;
                    inaccuracy = 10f;
                    shootSound = Sounds.shoot;
                    bullet = new BulletType(){{
                        speed = 4f;
                        damage = 8f;
                        lifetime = 30f;
                        hitEffect = Fx.hitBulletSmall;
                        despawnEffect = Fx.hitBulletSmall;
                    }};
                }});
            }

            void removeTempWeapons(){
                if(!tempWeapons.isEmpty()){
                    this.weapons.remove(Integer.parseInt("repair-beam-weapon-center"));
                    tempWeapons.clear();
                }
            }

            @Override
            public void update(Unit unit) {
                super.update(unit);

                if(unit.isShooting){
                    is = true;
                } else {
//                    removeTempWeapons();
                }
                updateUnitOnIce(this);
            }

            @Override
            public void draw(Unit unit){
                super.draw(unit);

                if(unit.isShooting()){
                    Log.debug("is shooting");

                    float x = unit.x;
                    float y = unit.y;
                    float time = Time.time;

                    Draw.z(Layer.bullet - 0.001f);

                    Draw.color(Pal.remove);
                    Lines.stroke(3f);
                    float pulseSize = 40f + Mathf.absin(time * 3f, 5f);
                    Lines.circle(x, y, pulseSize);

                    Draw.color(Pal.accent);
                    Lines.stroke(2f);
                    Lines.square(x, y, 35f, time * 2f);

                    Draw.reset();
                }
            }
        };
        pilot.constructor = UnitEntity::create;
    }
}