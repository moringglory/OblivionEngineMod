package OblivionEngine.expand.Blocks;

import OblivionEngine.content.OEColor;
import arc.graphics.Color;
import arc.scene.ui.TextArea;
import arc.scene.ui.layout.Table;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.*;
import mindustry.world.blocks.logic.MessageBlock;

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
        String text="Please select :\n   Do you want to start 980D?(y/n)\n    ";
        public StringBuilder message = new StringBuilder();
        int time = 0;
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.fileText, Styles.cleari, () -> {
                BaseDialog dialog = new BaseDialog("INFORMATION");
                dialog.setFillParent(false);
                dialog.cont.row();
                TextArea inf = dialog.cont.add(new TextArea(text)).size(768f, 65f).get();
                dialog.cont.row();
                TextArea a = dialog.cont.add(new TextArea(message.toString().replace("\r", "\n"))).size(768f, 432f).get();
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
                    if(a.getText().equals("y")){
                        inf.setText("BOOTING...");
                    } else if(a.getText().equals("n")) {
                        dialog.hide();
                    }
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