package OblivionEngine.expand.ui;

import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.graphics.Camera;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.ui.Dialog;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.fragments.MenuFragment;

import static arc.graphics.g2d.Draw.getColor;
import static mindustry.Vars.*;
import static mindustry.ui.dialogs.PlanetDialog.Mode.look;

public class OEDialog {
    public OEDialog() {

    }


    public static void load() {//这是添加触发到已有按钮中
        Vars.ui.settings.addCategory("@oblivine-engine.setting", Icon.settings, Table::clearChildren);

    }

    public static class OEUI implements ApplicationListener {
        public BaseDialog m_theorem = new TheoremDialog();
        public BaseDialog exportOverview = new PlanetPreviewDialog();
        boolean added;

        @Override
        public void init() {
            Events.on(EventType.ClientLoadEvent.class, e -> {
                for (MenuFragment.MenuButton button : ui.menufrag.desktopButtons) {
                    if (button != null && "@database.button".equals(button.text) && button.submenu != null) {
                        // 创建新的子菜单，排除 @database
                        Seq<MenuFragment.MenuButton> newSubmenu = new Seq<>();

                        for (MenuFragment.MenuButton subButton : button.submenu) {
                            if (!"@database".equals(subButton.text)) {  // 跳过 @database
                                newSubmenu.add(subButton);
                            }
                        }

                        // 创建新的 menu button
                        MenuFragment.MenuButton newDatabaseButton = new MenuFragment.MenuButton(
                                "@database.button",
                                Icon.menu,
                                () -> {
                                },  // 主按钮点击事件
                                newSubmenu.toArray(MenuFragment.MenuButton.class)
                        );

                        // 替换原有按钮
                        int index = ui.menufrag.desktopButtons.indexOf(button);
                        ui.menufrag.desktopButtons.set(index, newDatabaseButton);

                        break;
                    }
                }
                for (MenuFragment.MenuButton button : ui.menufrag.desktopButtons) {
                    if (button != null && "@database.button".equals(button.text) && button.submenu != null) {
                        Seq<MenuFragment.MenuButton> newSubmenu = new Seq<>();// 找到 database.button 的 submenu
                        for (MenuFragment.MenuButton subButton : button.submenu) {// 遍历原有子菜单
                            newSubmenu.add(subButton);// 添加原有按钮
                            if ("@schematics".equals(subButton.text)) {// 在 @schematics 之后添加自定义按钮
                                newSubmenu.add(new MenuFragment.MenuButton("@theorem", Icon.bookOpen, () -> {
                                    m_theorem.show();
                                }));
                                newSubmenu.add(new MenuFragment.MenuButton("@boot.IQC", Icon.redo, () -> {
                                }));
                            }
                        }
                        MenuFragment.MenuButton newDatabaseButton = new MenuFragment.MenuButton(// 创建新的 database.button
                                "@database.button",
                                Icon.menu,
                                () -> {
                                },  // 主按钮点击事件（通常为空）
                                newSubmenu.toArray(MenuFragment.MenuButton.class)
                        );
                        boolean menuadded = false;
                        if (!menuadded) {
                            menuadded = true;
                            int index = ui.menufrag.desktopButtons.indexOf(button);// 替换原有的 database.button
                            ui.menufrag.desktopButtons.set(index, newDatabaseButton);
                        }
                        break;
                    }
                }
            });

        }

        @Override
        public void update() {
            if (ui.planet != null) {
                ui.planet.shown(() -> {
                    added = false;
                });
                if (!added) {
                    ui.planet.buttons.button("@planet.preview", Icon.fileText, () -> {
                        exportOverview.show();
                    }).size(200f, 54f).pad(2).visible(() -> ui.planet.mode == look).bottom();
                    added = true;
                }
            }
        }
    }

    public static class PlanetPreviewDialog extends BaseDialog {
        public Camera cam;
        public float scaling = 5f;
        private static final Seq<Vec2> points = new Seq<>();

        public PlanetPreviewDialog() {
            super("@planet.preview");
            addCloseButton();
            dragged((cx, cy) -> {
                //no multitouch drag
                if (Core.input.getTouches() > 1) return;
                cam.position.add(-cx / Mathf.pow(2, scaling), -cy / Mathf.pow(2, scaling));
            });

            addListener(new InputListener() {
                @Override
                public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                    if (event.targetActor == PlanetPreviewDialog.this) {
                        scaling = Mathf.clamp(scaling - amountY / 3, Mathf.log(2, 10 / Vars.ui.planet.state.planet.solarSystem.totalRadius), Mathf.log(2, 20f));
                    }
                    return true;
                }
            });

            addCaptureListener(new ElementGestureListener() {
                float lastZoom = -1f;

                @Override
                public void zoom(InputEvent event, float initialDistance, float distance) {
                    if (lastZoom < 0) {
                        lastZoom = scaling;
                    }

                    scaling = (initialDistance / distance * lastZoom);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                    lastZoom = scaling;
                }
            });
            shown(this::setup);
        }

        void setup() {
            cam = new Camera();
            cam.position.set(0, 0);
        }

        @Override
        public void draw() {
            super.draw();
            cam.resize(Core.graphics.getWidth() / Mathf.pow(2, scaling),
                    Core.graphics.getHeight() / Mathf.pow(2, scaling));
            Draw.proj(cam);
            Vars.content.planets().each(p -> {
                if (!(p.solarSystem == Vars.ui.planet.state.planet.solarSystem)) return;
                if (p.accessible || p.solarSystem == p) {
                    if (p.parent != null && p.drawOrbit) {
                        Draw.color(Pal.gray);
                        Lines.stroke(0.2f);
                        Lines.circle(p.parent.position.x, p.parent.position.z, p.orbitRadius);
                    }
                    Draw.color(p.iconColor);
                    Fill.circle(p.position.x, p.position.z, p.radius);
                }
            });
        }

//        public void drawArc(Vec2 a, Vec2 b, Color from, Color to, float timeScale, int pointCount) {
//            //increase curve height when on opposite side of planet, so it doesn't tunnel through
//            points.clear();
//
//            Tmp.v1.set(a).setAngle(Mathf.slerp(a.angle(), b.angle(), 1f / 3f)).setLength(lerp(a.len(), b.len(), 1f / 3f) * 1.1f);
//            Tmp.v2.set(a).setAngle(Mathf.slerp(a.angle(), b.angle(), 2f / 3f)).setLength(lerp(a.len(), b.len(), 2f / 3f) * 1.1f);
//            points.addAll(a, Tmp.v1, Tmp.v2, b);
//            Tmp.bz2.set(points);
//            for (int i = 0; i < pointCount + 1; i++) {
//                float f = i / (float) pointCount;
//                float f1 = (i + 1) / (float) pointCount;
//                Tmp.c1.set(from).lerp(to, (f + Time.globalTime / timeScale) % 1f);
//                Draw.color(Tmp.c1);
//                Lines.line(Tmp.bz2.valueAt(Tmp.v3, f).x, Tmp.bz2.valueAt(Tmp.v3, f).y, Tmp.bz2.valueAt(Tmp.v3, f1).x, Tmp.bz2.valueAt(Tmp.v3, f1).y, false);
//            }
//            Draw.flush();
//        }
    }
//    public static class TheoremDialog extends BaseDialog {
//        public TheoremDialog() {
//            super("@planet.preview");
//            touchable = Touchable.enabled;
//            cont.add(new Label("q", Styles.outlineLabel){{
//                setColor(Color.blue);
//                setFontScale(1.6f);
//                setText("@theorem");
//            }});
//            cont.add(new ImageButton(Icon.cancel, Styles.clearNonei){{
//                update(() -> {
//                    getStyle().imageUpColor = Color.white;
//                });
//                clicked(() -> hide());
//            }});
//            cont.add(new Element(){
//                Color cyan = Color.cyan;
//                float time = 0f;
//                @Override
//                public void draw(){
//                    time += Time.delta;
//                    float progress = (time % 3f) / 3f;
//
//                    Draw.color(cyan);
//                    Draw.alpha(0.8f);
//
//                    // 流动光条
//                    float barWidth = width * 0.3f;
//                    float xPos = x + progress * (width - barWidth);
//
//                    Fill.rect(xPos, y + height/2f, barWidth, 2f);
//
//                    // 光晕效果
//                    Draw.alpha(0.2f);
//                    Fill.rect(xPos, y + height/2f, barWidth + 20f, 4f);
//
//                    Draw.reset();
//                }
//            });
//            cont.add(new Element(){
//                @Override
//                public void draw(){
//                    // 半透明暗色背景
//                    Draw.color(darkGray);
//                    Draw.alpha(0.7f);
//                    Fill.crect(255, 255, 7, 8);
//
//                    // 网格线
//                    Draw.color(techBlue);
//                    Draw.alpha(0.1f);
//                    Lines.stroke(0.5f);
//
//                    float gridSize = 20f;
//                    for(float i = x; i < x + 7; i += gridSize){
//                        Lines.line(i, y, i, y + 8);
//                    }
//                    for(float j = y; j < y + 8; j += gridSize){
//                        Lines.line(x, j, x + 7, j);
//                    }
//
//                    // 闪烁的数据点
//                    Draw.alpha(0.3f);
//                    float time = Time.time;
//                    for(int i = 0; i < 20; i++){
//                        float px = x + Mathf.random(width);
//                        float py = y + Mathf.random(height);
//                        float size = 1f + Mathf.absin(time + i, 2f);
//                        float alpha = 0.2f + Mathf.absin(time * 2f + i, 0.8f);
//
//                        Draw.color(Color.gold);
//                        Draw.alpha(alpha);
//                        Fill.circle(px, py, size);
//                    }
//
//                    Draw.reset();
//                }
//            });
//            closeOnBack();
//        }
//    }
public static class TheoremDialog extends BaseDialog {
    private Color techBlue = Color.valueOf("#50FFFB");
    private Color matrixGreen = Color.valueOf("#00ff99");
    private Color darkGray = Color.valueOf("#1a1a2e");
    private float timeAccumulator = 0f;
    private boolean isFlashing = true;
    private float flashInterval = 1.5f;
    public TheoremDialog() {
        super("@theorem");
        title.remove();
        Seq<Element> children = titleTable.getChildren();
        for (int i = children.size - 1; i >= 0; i--) {
            Element child = children.get(i);
            if (child instanceof Image) {
                Image image = (Image) child;
                if (Mathf.equal(image.getHeight(), 3f)) {
                    titleTable.removeChild(image);
                }
            }
        }
        shouldPause = true;
        addCloseButton();
        touchable = Touchable.enabled;
        buildInterface();
        closeOnBack();
    }

    private void buildInterface() {
        //标题
        Stack stack = new Stack();
        stack.add(new Label("@theorem.title", Styles.outlineLabel){{//cont.add(new Label("@theorem.title", Styles.outlineLabel){{
            setColor(matrixGreen);
            setFontScale(1.8f);
            setAlignment(Align.center);
//            // 标题闪烁效果
//            update(() -> {
//                if (TheoremDialog.this.isShown()) {
//                    float pulse = 0.7f + Mathf.absin(Time.time * 1.5f, 0.3f);
//                    getColor().a = pulse;
//                } else {
//                    getColor().a = 1f;
//                }
//            });
        }});//.growX().center().padBottom(15f);
        stack.add(new ImageButton(Icon.cancel, Styles.clearNonei){{
            setPosition(getWidth() - 7f, getHeight() - 7f);
//            setSize(100f, 50f);
            update(() -> {
                getStyle().imageUpColor = isOver() ? Color.white : techBlue;
            });
            clicked(() -> {
                hide();
            });
        }});
        cont.add(stack).row();
        //关闭
//        cont.add(new ImageButton(Icon.cancel, Styles.clearNonei){{
//            update(() -> {
//                getStyle().imageUpColor = isOver() ? Color.white : techBlue;
//            });
//            clicked(() -> TheoremDialog.this.hide());
//        }}).size(0f).pad(0f).right().row();
        //闪烁长条
        cont.add(new Element(){
            @Override
            public void draw(){
//                // 更新计时器
//                timeAccumulator += Time.delta;
//
//                // 控制闪烁状态
////                if (timeAccumulator >= flashInterval) {
////                    isFlashing = !isFlashing;
////                    timeAccumulator = 0f;
////
////                    // 随机改变闪烁间隔
////                    flashInterval = Mathf.random(1.0f, 2.0f);
////                }
//
//                // 计算闪烁强度
//                float flashIntensity = 0f;
//                if (isFlashing) {
//                    // 闪烁阶段的强度变化
//                    float progress = timeAccumulator / flashInterval;
//                    flashIntensity = Mathf.curve(progress, 0.1f, 0.8f);
//                }
//
//                if (flashIntensity > 0.01f) {
//                    // 绘制闪烁长条
                    Draw.color(techBlue);
//                    Draw.alpha(flashIntensity);

                    //长条参数
                    float barHeight = 0f;
                    float padding = 20f;
                    //顶部
                    Fill.crect(x + padding, y + height - barHeight - 5f, width - padding * 2f, barHeight);
                    //底部
                    Fill.crect(x + padding, y + 5f, width - padding * 2f, barHeight);
                    //侧边
                    Fill.crect(x + 5f, y + padding, barHeight, height - padding * 2f);
                    Fill.crect(x + width - barHeight - 5f, y + padding, barHeight, height - padding * 2f);
                    //光晕
                    Draw.alpha(0.35f);
                    Fill.crect(x + padding - 2f, y + height - barHeight - 7f, width - (padding - 2f) * 2f, barHeight + 4f);
                    Fill.crect(x + padding - 2f, y + 3f, width - (padding - 2f) * 2f, barHeight + 4f);
                    Draw.reset();
                }
//            }
        }).growX().height(0f).pad(0f).row();
        //定理
        cont.add(new Table(){{
            margin(15f);

            add(new Label("@theorem.content"){{
                setWrap(true);
                setAlignment(Align.center, Align.left);
                setColor(Color.white);
                //文字呼吸
                update(() -> {
                    if (TheoremDialog.this.isShown()) {
                        float breath = 0.8f + Mathf.absin(Time.time * 0.8f, 0.2f);
                        getColor().a = breath;
                    } else {
                        getColor().a = 1f;
                    }
                });
            }}).grow().pad(20f);
        }}).grow().pad(10f).row();
        //网格背景
        cont.addChild(new Element(){
            @Override
            public void draw(){
                if (!TheoremDialog.this.isShown()) return;
                //半透明暗色背景
                Draw.color(Color.blue);
                Draw.alpha(0.3f);
                Fill.crect(x, y, width, height);
                //网格线
                Draw.color(techBlue);
                Draw.alpha(0.08f);
                Lines.stroke(0.5f);
                float gridSize = 25f;
                for(float i = x; i < x + width; i += gridSize){
                    Lines.line(i, y, i, y + height);
                }
                for(float j = y; j < y + height; j += gridSize){
                    Lines.line(x, j, x + width, j);
                }
                //动态数据点
                float time = Time.time;
                for(int i = 0; i < 15; i++){
                    float offset = i * 0.7f;
                    float px = x + Mathf.sin(time + offset) * 10f + width * 0.5f;
                    float py = y + Mathf.cos(time * 1.3f + offset) * 8f + height * 0.5f;
                    float size = 0.5f + Mathf.absin(time * 2f + i, 1.5f);
                    float alpha = 0.1f + Mathf.absin(time * 3f + i, 0.9f);

                    Draw.color(i % 2 == 0 ? matrixGreen : techBlue);
                    Draw.alpha(alpha);
                    Fill.circle(px, py, size);
                }
                Draw.reset();
            }
        });
        //控制按钮
        Table buttonTable = new Table(){{
            defaults().size(120f, 40f).pad(5f);
            // 上一个定理按钮
            button("@theorem.prev", Icon.left, Styles.flatTogglet, () -> {
                Vars.ui.showInfo("@theorem.nav.prev");
            }).update(b -> {
                b.getStyle().fontColor = b.isOver() ? matrixGreen : Color.lightGray;
            });
            // 下一个定理按钮
            button("@theorem.next", Icon.right, Styles.flatTogglet, () -> {
                Vars.ui.showInfo("@theorem.nav.next");
            }).update(b -> {
                b.getStyle().fontColor = b.isOver() ? matrixGreen : Color.lightGray;
            });
            // 证明按钮
            button("@theorem.prove", Icon.zoom, Styles.flatTogglet, () -> {
                Vars.ui.showInfo("@theorem.proof.show");
            }).update(b -> {
                b.getStyle().fontColor = b.isOver() ? Color.valueOf("#ff66cc") : Color.lightGray;
            });
        }};
        cont.add(buttonTable).growX().pad(10f).row();
        // 状态显示
        cont.add(new Table(){{
            background(Tex.button);
            add(new Label("@theorem.status.active", Styles.outlineLabel){{
                setColor(matrixGreen);
                setFontScale(0.9f);
                update(() -> {
                    if (TheoremDialog.this.isShown()) {
                        // 动态状态文本
                        float time = Time.time;
                        String[] statuses = {
                                "@theorem.status.processing",
                                "@theorem.status.analyzing",
                                "@theorem.status.verified",
                                "@theorem.status.active"
                        };
                        int index = ((int)(time * 0.3f)) % statuses.length;
                        setText(statuses[index]);

                        // 状态文本闪烁
                        float flash = Mathf.absin(time * 2f, 0.5f);
                        getColor().a = 0.7f + flash;
                    } else {
                        getColor().a = 1f;
                        setText("@theorem.status.ready");
                    }
                });
            }}).pad(5f);
        }}).growX().height(30f).pad(5f);
    }

    // 对话框显示时的特效
//    @Override
//    public Dialog show() {
//        super.show();
//
//        // 重置动画状态
//        timeAccumulator = 0f;
//        isFlashing = true;
//        flashInterval = 1.5f;
//
//        // 播放显示音效
//        Sounds.message.play();
//        return null;
//    }

    // 对话框隐藏时的清理
    @Override
    public void hide() {
        super.hide();
        //Sounds.click.play();
    }
}
}
