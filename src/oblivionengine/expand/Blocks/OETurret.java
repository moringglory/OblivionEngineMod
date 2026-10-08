package oblivionengine.expand.Blocks;

import arc.Core;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Lightning;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.meta.Stat;
import oblivionengine.content.OEIcon;
import oblivionengine.content.OEWeather;
import oblivionengine.expand.UI.OEStatValues;

import static oblivionengine.content.OEBlocks.isUnderShield;

public class OETurret extends ItemTurret {
    public float clearReload = 12f;
    public float rainReload = clearReload * coolantMultiplier / 2;
    public float snowReload = (clearReload / coolantMultiplier) * 2;

    public OETurret(String name){
        super(name);

        reload = clearReload;
    }

    @Override
    public void setStats() {
        super.setStats();

        String persecond = Core.bundle.get("unit.persecond");
        stats.replace(Stat.reload, OEStatValues.IconTable(
                OEIcon.clear, ":" + (int) (60f / (clearReload + (!reloadWhileCharging ? shoot.firstShotDelay : 0f)) * shoot.shots) + persecond,
                OEIcon.rain, ":" + (int) (60f / (rainReload + (!reloadWhileCharging ? shoot.firstShotDelay : 0f)) * shoot.shots) + persecond,
                OEIcon.snow, ":" + (int) (60f / (snowReload + (!reloadWhileCharging ? shoot.firstShotDelay : 0f)) * shoot.shots) + persecond
        ));
    }

    public class OETurretBuild extends ItemTurretBuild {

        @Override
        protected float baseReloadSpeed() {
            float parent = super.baseReloadSpeed();

            if (isUnderShield(x, y)) return parent * (reload / clearReload);
            if (OEWeather.isRaining() || OEWeather.isSporestorm()) return parent * (reload / rainReload);
            if (OEWeather.isSnowing()) return parent * (reload / snowReload);
            return parent * (reload / clearReload);
        }

        @Override
        public void update() {
            super.update();

            if (!isUnderShield(x, y) && OEWeather.isRaining()) {
                if (Mathf.chanceDelta(0.5f)) {
                    Fx.lightningShoot.create(x, y, Mathf.random(360f), Pal.accent, 1);
                    Damage.damage(x, y, 1f, 1.5f);
                }
            }
        }
    }
}