package OblivionEngine.expand.Blocks;

import arc.scene.ui.layout.Table;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.*;

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