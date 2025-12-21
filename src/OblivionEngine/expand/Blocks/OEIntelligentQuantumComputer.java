package OblivionEngine.expand.Blocks;

import OblivionEngine.content.OEColor;
import arc.graphics.Color;
import arc.scene.ui.TextArea;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.*;
import mindustry.world.blocks.logic.MessageBlock;

import javax.swing.*;

import static mindustry.Vars.state;

public class OEIntelligentQuantumComputer extends Block {
    public int maxTextLength = 220;
    public int maxNewlines = 24;
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
        String text="Please select :\n   Do you want to start 980D?(y/n)\n";
        public StringBuilder message = new StringBuilder();
        int time = 0;
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.fileText, Styles.cleari, () -> {
                BaseDialog dialog = new BaseDialog("INFORMATION");
                dialog.setFillParent(false);
                dialog.cont.row();
                TextArea a = dialog.cont.add(new TextArea(message.toString().replace("\r", "\n"))).size(768f, 75f).get();
                dialog.cont.row();
                TextArea inf = dialog.cont.add(new TextArea(text)).size(768f, 432f).get();
                a.setFilter((textField, c) -> {
                    if(c == '\n'){
                        int count = 0;
                        for(int i = 0; i < textField.getText().length(); i++){
                            if(textField.getText().charAt(i) == '\n'){
                                count++;
                            }
                        }
                        return count < maxNewlines;
                    }
                    return true;
                });
                a.setMaxLength(maxTextLength);
                dialog.cont.row();
                dialog.cont.label(() -> "TIME:"+time).color(OEColor.highwhite);
//                dialog.cont.label(() -> a.getText().length() + " / " + maxTextLength).color(Color.lightGray);
                dialog.buttons.button("EXIT", () -> {
                    if(!a.getText().equals(message.toString())) configure(a.getText());
                    message = new StringBuilder(a.getText());
                    dialog.hide();
                }).size(100f, 40f);
                dialog.buttons.button("DONE", () -> {
                    String n=a.getText();
                    if(n.equals("Y")||n.equals("y")||n.equals("y\n")||n.equals("Y\n")){
                        inf.setColor(Color.blue.g(150).r(150));
                        for(float i=30f;i<=570f;i+=30f){
                            StringBuilder str= new StringBuilder();
                            for(int j=0;j<=i/20-3;j++) str.append("|");
                            for(int j=0;j<=20-i/30-1;j++) str.append(" ");
                            String finalStr = str.toString();
                            Time.run(i,()->inf.setText("BOOTING...\n[ "+ finalStr +" ]"));
                        }
                        Time.run(620f, ()->inf.setColor(Color.white.b(150)));
                        Time.run(620f, ()->inf.setText("BOOTING...\n[ |||||||||||||||||||||||||||| ]\nSUCCESS!"));
//                        Time.run(680f, dialog::hide);
                    } else if(n.equals("n")||n.equals("N")||n.equals("n\n")||n.equals("N\n")) {
                        dialog.hide();
                    } else {
                        inf.setText(text+"Please select");
                        inf.setColor(Color.red);
                    }
                    a.setText("");
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