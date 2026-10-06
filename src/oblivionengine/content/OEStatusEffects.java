package oblivionengine.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

public class OEStatusEffects {
    static StatusEffect irradiation;

    public static void load() {
        irradiation = new StatusEffect("irradiation"){
            {
                color = Color.valueOf("46382a");
                speedMultiplier = 5f;
                effect = Fx.blastsmoke;
                effectChance = 0.15f;
            }
//            @Override
//            public void update(Unit unit, float time) {
//                super.update(unit, time);
//            }
        };
    }
}
