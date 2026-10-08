package oblivionengine.expand.UI;

import arc.graphics.g2d.TextureRegion;
import mindustry.ui.Styles;
import mindustry.world.meta.StatValue;

public class OEStatValues {

    /** 文本 */
    public static StatValue StringTable(String text) {
        return table -> {
            table.row();
            table.table(Styles.grayPanel, inner -> {
                inner.left().top().defaults().padRight(3).left();
                inner.add(text).padLeft(5).padTop(5).padBottom(5).growX();
            }).padLeft(5).padTop(5).padBottom(5).growX().margin(10);
            table.row();
        };
    }

    /** 图标 + 前缀文字 */
    public static StatValue IconTable(Object... pairs) {
        return table -> {
            table.row();
            table.table(Styles.grayPanel, inner -> {
                inner.left().top().defaults().padRight(3).left();

                for (int i = 0; i < pairs.length; i += 2) {
                    TextureRegion icon = (pairs[i] instanceof TextureRegion) ? (TextureRegion) pairs[i] : null;
                    Object text = i + 1 < pairs.length ? pairs[i + 1] : "";

                    if (icon != null) {
                        inner.image(icon).size(24).padRight(8);
                    }
                    inner.add(String.valueOf(text)).growX();
                    inner.row();
                }
            }).padLeft(5).padTop(5).padBottom(5).growX().margin(10);
            table.row();
        };
    }
}