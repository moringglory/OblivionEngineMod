package oblivionengine.expand.Blocks;

import arc.Core;
import arc.Input;
import arc.graphics.Color;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.GlyphLayout;
import arc.math.geom.Vec2;
import arc.scene.ui.TextArea;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import arc.util.io.*;
import arc.util.pooling.Pools;
import mindustry.core.UI;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.world.meta.Stat.memoryCapacity;

public class OEIntelligentQuantumComputer extends Block {

    public OEIntelligentQuantumComputer(String name) {
        super(name);

        configurable = true;
        destructible = true;
        canOverdrive = false;
        solid = true;
    }

    public class Booting extends Building {
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.pencil, Styles.cleari, () -> {
                BaseDialog dialog = new BaseDialog("INFORMATION");
                dialog.setFillParent(false);
//                TextArea a = dialog.cont.add(new TextArea(message.toString().replace("\r", "\n"))).size(380f, 160f).get();
//                a.setFilter((textField, c) -> {
//                    if(c == '\n'){
//                        int count = 0;
//                        for(int i = 0; i < textField.getText().length(); i++){
//                            if(textField.getText().charAt(i) == '\n'){
//                                count++;
//                            }
//                        }
//                        return count < maxNewlines;
//                    }
//                    return true;
//                });
//                a.setMaxLength(maxTextLength);
//                dialog.cont.row();
//                dialog.cont.label(() -> a.getText().length() + " / " + maxTextLength).color(Color.lightGray);
                dialog.buttons.button("DONE", () -> {
                    dialog.hide();
                }).size(100f, 40f);
                dialog.update(() -> {
                    if(tile.build != this){
                        dialog.hide();
                    }
                });
                dialog.closeOnBack();
                dialog.show();
            }).size(40f);
        }
    }
}