//package OblivionEngine.expand.Blocks;
//
//import OblivionEngine.content.OEColor;
//import arc.graphics.Color;
//import arc.scene.ui.Label;
//import arc.scene.ui.TextArea;
//import arc.scene.ui.layout.Table;
//import arc.util.Time;
//import mindustry.gen.*;
//import mindustry.gen.Icon;
//import mindustry.ui.Styles;
//import mindustry.ui.dialogs.BaseDialog;
//import mindustry.world.*;
//import mindustry.world.blocks.logic.MessageBlock;
//
//import javax.swing.*;
//
//import static mindustry.Vars.state;
//
//public class OEIntelligentQuantumComputer extends Block {
//    public int maxTextLength = 220;
//    public int maxNewlines = 24;
//    public OEIntelligentQuantumComputer(String name) {
//        super(name);
//
//        configurable = true;
//        destructible = true;
//        canOverdrive = false;
//        solid = true;
//    }
//
//    public boolean accessible(){
//        return !privileged || state.rules.editor || state.rules.allowEditWorldProcessors;
//    }
//
//    public class Booting extends Building {
//        String text="Please select :\n   Do you want to start 980D?(y/n)\n";
//        public StringBuilder message = new StringBuilder();
//        int time = 0;
//        @Override
//        public void buildConfiguration(Table table) {
//            table.button(Icon.fileText, Styles.cleari, () -> {
//                BaseDialog dialog = new BaseDialog("INFORMATION");
//                dialog.setFillParent(false);
//                dialog.cont.row();
//                dialog.cont.add(new Label("C:\\:"));
//                TextArea a = dialog.cont.add(new TextArea(message.toString().replace("\r", "\n"))).size(768f, 75f).get();
//                dialog.cont.row();
//                TextArea inf = dialog.cont.add(new TextArea(text)).size(768f, 432f).get();
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
//                dialog.cont.label(() -> "TIME:"+time).color(OEColor.highwhite);
////                dialog.cont.label(() -> a.getText().length() + " / " + maxTextLength).color(Color.lightGray);
//                dialog.buttons.button("EXIT", () -> {
//                    if(!a.getText().equals(message.toString())) configure(a.getText());
//                    message = new StringBuilder(a.getText());
//                    dialog.hide();
//                }).size(100f, 40f);
//                dialog.buttons.button("DONE", () -> {
//                    String n=a.getText();
//                    if(n.equals("Y")||n.equals("y")||n.equals("y\n")||n.equals("Y\n")){
//                        inf.setColor(Color.blue.g(150).r(150));
//                        for(float i=30f;i<=570f;i+=30f){
//                            StringBuilder str= new StringBuilder();
//                            for(int j=0;j<=i/20-3;j++) str.append("|");
//                            for(int j=0;j<=20-i/30-1;j++) str.append(" ");
//                            String finalStr = str.toString();
//                            Time.run(i,()->inf.setText("BOOTING...\n[ "+ finalStr +" ]"));
//                        }
//                        Time.run(620f, ()->inf.setColor(Color.white.b(150)));
//                        Time.run(620f, ()->inf.setText("BOOTING...\n[ |||||||||||||||||||||||||||| ]\nSUCCESS!"));
////                        Time.run(680f, dialog::hide);
//                    } else if(n.equals("n")||n.equals("N")||n.equals("n\n")||n.equals("N\n")) {
//                        dialog.hide();
//                    } else {
//                        inf.setText(text+"Please select");
//                        inf.setColor(Color.red);
//                    }
//                    a.setText("");
//                }).size(100f, 40f);
//                dialog.update(() -> {
//                    if(tile.build != this){
//                        dialog.hide();
//                    }
//                });
//                dialog.closeOnBack();
//                dialog.show();
//            }).size(40f);
//        }
//        @Override
//        public boolean onConfigureBuildTapped(Building other) {
//            if(this == other || !accessible()){
//                deselect();
//                return false;
//            }
//
//            return true;
//        }
//    }
//}
package OblivionEngine.expand.Blocks;

import OblivionEngine.content.OEColor;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.TextArea;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.gen.Icon;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.*;

import static mindustry.Vars.state;
import static mindustry.gen.Tex.cursor;
import static mindustry.gen.Tex.whiteui;

public class OEIntelligentQuantumComputer extends Block {
    public OEIntelligentQuantumComputer(String name) {
        super(name);

        configurable = true;
        destructible = true;
        canOverdrive = false;
        solid = true;
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
            table.button(Icon.admin, Styles.cleari, () -> {
                BaseDialog dialog = new BaseDialog("INFORMATION");
                dialog.setFillParent(false);

                // 创建自定义样式的矩形背景
                Table contentTable = new Table();

                // 添加标题栏
                contentTable.rect((x, y, width, height) -> {
                    Draw.color(Color.blue);
                    Fill.rect(x, y, width, height);
                    Draw.color(Color.white);

                    // 添加边框
                    Draw.color(Color.blue);
                    Lines.stroke(2f);
                    Lines.rect(x + 1, y + 1, width - 2, height - 2);
                }).height(50f).width(768f);

                contentTable.add(new Label("QUANTUM COMPUTER 980D")).center().expandX();

                // 添加信息区域
                contentTable.row();
                contentTable.rect((x, y, width, height) -> {
                    Draw.color(Color.blue);
                    Fill.rect(x, y, width, height);
                    Draw.color(Color.cyan);
                    Lines.stroke(1f);
                    Lines.rect(x + 2, y + 2, width - 4, height - 4);
                }).height(200f).width(768f);

                contentTable.add(new Label("System Information")).center().expandX().pad(10f);

                // 添加按钮区域
                contentTable.row();
                contentTable.rect((x, y, width, height) -> {
                    Draw.color(OEColor.ancientLight);
                    Fill.rect(x, y, width, height);
                    Draw.color(OEColor.highwhite);
                    Lines.stroke(1f);
                    Lines.rect(x + 1, y + 1, width - 2, height - 2);
                }).height(80f).width(768f);

                // 添加按钮
                Table buttonTable = new Table();
                buttonTable.defaults().size(100f, 40f).pad(10f);
                buttonTable.button("START", () -> {
                    // 启动逻辑
                });
                buttonTable.button("CANCEL", () -> dialog.hide());

                contentTable.add(buttonTable).center();

                dialog.cont.add(contentTable);
                dialog.show();
            });
            table.button(Icon.fileText, Styles.cleari, () -> {
                TextField.TextFieldStyle IQCStyle = new TextField.TextFieldStyle();
                IQCStyle.font = Fonts.tech; // 你的字体
                IQCStyle.fontColor = Color.white; // 正常状态颜色
                IQCStyle.disabledFontColor = Color.white; // 禁用状态颜色
                IQCStyle.background = Styles.black3; // 背景
                IQCStyle.disabledBackground = Styles.black3; // 禁用状态背景
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
                    IQCStyle.disabledFontColor = OEColor.ancientLight1;
                    dialog.cont.draw();
                    boot.setText("BOOTING...");
//                    Time.run(150f,);
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
                dialog.closeOnBack();
            }).size(40f);
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