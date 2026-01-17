package OblivionEngine.content.core;

import mindustry.game.Team;
import mindustry.type.StatusEffect;

public class OEEffect {
    static StatusEffect xs;

    public static void load() {
        xs = new StatusEffect("xs"){{
            color = Team.crux.color;
            permanent = true;
            damageMultiplier = 1.3f;
            healthMultiplier = 1.5f;
            show=true;
        }};
    }
}
