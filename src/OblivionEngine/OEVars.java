package OblivionEngine;

import OblivionEngine.expand.ui.OEDialog;

import static arc.Core.app;

public class OEVars {
    public static OEDialog.OEUI OEui = new OEDialog.OEUI();
    public static void init() {
        OEui.init();
        app.addListener(OEui);
        OEDialog.load();
    }
}
