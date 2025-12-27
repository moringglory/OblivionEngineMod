package OblivionEngine.expand.Blocks;

import OblivionEngine.content.OEColor;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.ui.Label;
import arc.scene.ui.TextArea;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.gen.Icon;
import mindustry.graphics.Layer;
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
        float a = 0;
        String text="Please select :\n\n   Do you want to start 980D?(y/n)\n";
        public StringBuilder message = new StringBuilder();
        int time = 0;
        @Override
        public void buildConfiguration(Table table) {
//            table.button(Icon.fileText, Styles.cleari, () -> {
//                BaseDialog dialog = new BaseDialog("INFORMATION");
//                dialog.setFillParent(false);
//
//                // 创建自定义样式的矩形背景
//                Table contentTable = new Table();
//
//                // 添加标题栏
//                contentTable.rect((x, y, width, height) -> {
//                    Draw.color(Color.cyan);
//                    Fill.rect(x, y, width, height);
//                    Draw.color(Color.white);
//
//                    // 添加边框
//                    Draw.color(Color.blue);
//                    Lines.stroke(2f);
//                    Lines.rect(x + 1, y + 1, width - 2, height - 2);
//                }).height(50f).width(768f);
//
//                contentTable.add(new Label("QUANTUM COMPUTER 980D")).center().expandX();
//
//                // 添加信息区域
//                contentTable.row();
//                contentTable.rect((x, y, width, height) -> {
//                    Draw.color(OEColor.ancientLight);
//                    Fill.rect(x, y, width, height);
//                    Draw.color(Color.cyan);
//                    Lines.stroke(1f);
//                    Lines.rect(x + 2, y + 2, width - 4, height - 4);
//                }).height(200f).width(768f);
//
//                contentTable.add(new Label("System Information")).center().expandX().pad(10f);
//
//                // 添加按钮区域
//                contentTable.row();
//                contentTable.rect((x, y, width, height) -> {
//                    Draw.color(OEColor.powerArea);
//                    Fill.rect(x, y, width, height);
//                    Draw.color(OEColor.highwhite);
//                    Lines.stroke(1f);
//                    Lines.rect(x + 1, y + 1, width - 2, height - 2);
//                }).height(80f).width(768f);
//
//                // 添加按钮
//                Table buttonTable = new Table();
//                buttonTable.defaults().size(100f, 40f).pad(10f);
//                buttonTable.button("START", () -> {
//                    // 启动逻辑
//                });
//                buttonTable.button("CANCEL", () -> dialog.hide());
//
//                contentTable.add(buttonTable).center();
//
//                dialog.cont.add(contentTable);
//                dialog.show();
//
//            }).size(40f);
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
                                draw();
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
        @Override
        public void draw(){
            super.draw();

            float x = tile.drawx();
            float y = tile.drawy();
            float size = tilesize * 4f;
            Draw.z(Layer.block + 0.1f);
            Draw.color(Color.cyan);
            Lines.stroke(1f);
            Lines.rect(x-20,y-20,size,size);

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
