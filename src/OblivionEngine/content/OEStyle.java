package OblivionEngine.content;

import arc.graphics.Color;
import arc.scene.ui.Dialog;
import arc.scene.ui.TextField;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

public class OEStyle {
    public static TextField.TextFieldStyle CommandTextFieldStyle = new TextField.TextFieldStyle(){{
        font = Fonts.def;
        fontColor = Color.white;
        disabledFontColor = Color.white;
        background = Styles.black3;
        disabledBackground = Styles.black3;
    }};
    public static Dialog.DialogStyle PlanetView = new Dialog.DialogStyle(){{
        background = Styles.black;
        titleFont = Fonts.def;
        titleFontColor = OEColor.highwhite;
        stageBackground = null;
    }};
    public static Dialog.DialogStyle TheoremDialog = new Dialog.DialogStyle(){{
        background = null;
        titleFont = Fonts.def;
        titleFontColor = OEColor.highwhite;
        stageBackground = null;
    }};
}
