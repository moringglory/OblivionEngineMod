package oblivionengine.content;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.graphics.Pal;

public class OEColor {
    public static Color
            highwhite = new Color(255, 255, 255),
            ancient = Pal.sapBullet.cpy().mul(1.075f).lerp(Color.white, 0.075f),
            ancientLight = ancient.cpy().lerp(Color.white, 0.7f),
            darkEnrColor = Items.surgeAlloy.color.cpy().lerp(Pal.accent, 0.115f).a(0.4F);
}