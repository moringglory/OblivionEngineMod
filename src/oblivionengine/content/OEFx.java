package oblivionengine.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Rand;
import mindustry.entities.Effect;

import static arc.math.Angles.randLenVectors;

public class OEFx {
    private static final Rand rand = new Rand();
    public static Effect
    triSpark1 = new Effect(26, e -> {
        rand.setSeed(e.id);
        Draw.color(OEColor.highwhite);
        randLenVectors(e.id, 3, 3f + 24f * e.fin(), 5f, (x, y) -> {
            float randN = rand.random(120f);
            Fill.poly(e.x + x, e.y + y, 3, e.fout() * 8f * rand.random(0.8f, 1.2f), e.rotation + randN * e.fin());
        });
    });
}
