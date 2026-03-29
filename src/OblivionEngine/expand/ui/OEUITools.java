package OblivionEngine.expand.ui;

import arc.Core;
import arc.scene.Element;
import arc.util.Log;

public class OEUITools {
    public static void PrintOEInformation(String information) {
        Log.info("[OE][info] "+information+".");
    }

    public static void setRelativeBounds(Element container, Element element, float xPercent, float yPercent, float width, float height) {
        float containerWidth = container.getWidth();
        float containerHeight = container.getHeight();

        float x = (containerWidth / 2f) + (xPercent * containerWidth / 2f);
        float y = (containerHeight / 2f) + (yPercent * containerHeight / 2f);

        x -= width / 2f;
        y -= height / 2f;

        element.setBounds(x, y, width, height);
    }

    public static void setRelativeBounds(Element element, float xPercent, float yPercent, float width, float height) {
        float containerWidth = Core.graphics.getWidth();
        float containerHeight = Core.graphics.getHeight();
        float x = xPercent * containerWidth / 2f;
        float y = yPercent * containerHeight / 2f;

        x -= width / 2f;
        y -= height / 2f;

        element.setBounds(x, y, width, height);
    }
}
