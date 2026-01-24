package OblivionEngine.expand.Blocks;

import OblivionEngine.content.OEDrawBlock;
import OblivionEngine.content.OEColor;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.ui.TextArea;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.gen.Icon;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.*;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class OEIntelligentQuantumComputer extends Block {
    public OEIntelligentQuantumComputer(String name) {
        super(name);

        configurable = true;
        destructible = true;
        canOverdrive = false;
        solid = true;
        update = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
    }
    @Override
    public void setBars(){
        super.setBars();

    }
    public boolean accessible(){
        return !privileged || state.rules.editor || state.rules.allowEditWorldProcessors;
    }

    public class Booting extends Building {
        String text="Please select :\n\n   Do you want to start 980D?(y/n)\n";
        boolean boot_ = false;
        int boottime,time = 0;
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.fileText, Styles.cleari, () -> {
                TextField.TextFieldStyle IQCStyle = new TextField.TextFieldStyle();
                IQCStyle.font = Fonts.def;
                IQCStyle.fontColor = Color.white;
                IQCStyle.disabledFontColor = Color.white;
                IQCStyle.background = Styles.black3;
                IQCStyle.disabledBackground = Styles.black3;
                //光标IQCStyle.cursor;
                //聚焦颜色IQCStyle.focusedFontColor = Color.blue;

                BaseDialog dialog = new BaseDialog("INFORMATION");
                dialog.setFillParent(false);
                dialog.cont.row();
                TextArea boot = dialog.cont.add(new TextArea(text)).size(768f, 432f).get();
                boot.setDisabled(true);
                boot.setStyle(IQCStyle);
                dialog.cont.row();
                dialog.cont.label(() -> "TIME:"+time).color(OEColor.highwhite);
                dialog.buttons.button("EXIT", () -> {
                    dialog.hide();
                }).size(100f, 40f);
                dialog.buttons.button("Y", () -> {
                    IQCStyle.disabledFontColor = Color.blue;
                    boot.setText("PREPAREING VIRTUAL ENVIRONMENT...");
                    dialog.buttons.remove();
                    dialog.cont.row();
                    dialog.cont.label(() -> "").size(40);
                    Time.run(500f, () -> {
                        Core.app.post(() -> {
                            boot.setText("PREPAREING VIRTUAL ENVIRONMENT...\nSUCCESS!\nSTART TO BOOTING 980D...");
                            Time.run(200f, () -> {
                                dialog.hide();

                                //开始启动
                                boot_=true;
                            });
                        });
                    });
                }).size(40f);
                dialog.buttons.button("N", ()-> {
                    dialog.hide();
                }).size(40f);
                dialog.update(() -> {
                    if(tile.build != this){
                        dialog.hide();
                    }
                });
                dialog.show();
//                dialog.closeOnBack();
            }).size(40f);
        }
//        @Override
//        public void draw(){
//            super.draw();
//
//            float x = tile.drawx();
//            float y = tile.drawy();
//            float size = tilesize * 4f;
//            Draw.z(Layer.block + 0.1f);
//            Draw.color(Color.cyan);
//            Lines.stroke(1f);
//            Lines.rect(x-16,y-16,size,size);
//
//        }
//        @Override
//        public void draw(){
//            super.draw();
//
//            if(boot_==true){
//                float rad = size * tilesize / 2f * 0.74f;
//
//                Draw.z(Layer.bullet - 0.0001f);
//                Lines.stroke(1.75f, Pal.accent);
//                Lines.square(x, y, rad,122f);
//                Draw.color(team.color);
//                Lines.square(x, y, rad+100,122f);
//            } else {
//
//            }
//        }
        @Override
        public void draw() {
            super.draw();

            if (boot_==true) {
                float rad = size * tilesize / 2f * 0.74f;

                Draw.z(Layer.bullet - 0.0001f);

                // 第一个正方形：外圈，颜色为 Pal.accent，带旋转动画
                Lines.stroke(5.5f, Pal.accent);
                float rotation1 = Time.time * 2f; // 每秒旋转 30 度
                Lines.square(x, y, rad, rotation1); // 旋转角度是 rotation1

                // 第二个正方形：内圈或主体，颜色为 team.color，也带旋转（可以不同速度）
                Draw.color(team.color);
                float rotation2 = Time.time * 1f; // 每秒旋转 20 度，可以与上面不同
                Lines.square(x, y, rad + 100, rotation2); // 旋转角度是 rotation2
                Lines.square(x, y, rad + 100, rotation2*-1);
                OEDrawBlock.parts.addAll(
                new OEDrawBlock(){{
                    color = Color.blue;
                    circle = true;
                    hollow = true;
                    stroke = 0f;
                    strokeTo = 4f;
                    radius = 5f;
                    layer = Layer.effect;
                    y =12;
                    x = 11;
                }});
            } else {
                // boot 为 false 时，什么都不绘制，或者绘制默认状态
                
            }
        }
        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if(this == other || !accessible()){
                deselect();
                return false;
            }

            return true;
        }
    }
}
