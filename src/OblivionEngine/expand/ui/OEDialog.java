package oblivionengine.expand.ui;

import mindustry.type.Category;
import oblivionengine.OECategory;
import oblivionengine.content.OEColor;
import oblivionengine.content.OENoise;
import oblivionengine.content.OEStyle;
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
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.fragments.MenuFragment;

import java.lang.reflect.Field;

import static oblivionengine.content.OEColor.techBlue;
import static mindustry.Vars.*;
import static mindustry.ui.dialogs.PlanetDialog.Mode.look;

public class OEDialog {

    public OEDialog() {

    }

    public static void load() {//这是添加触发到已有按钮中
        ui.settings.addCategory("@oblivine-engine.setting", Icon.settings, table -> {
            table.checkPref("@oblivine-engine.DebugSwitch", false);
//            table.sliderPref("oe-particles", 50, 0, 100, 5, i -> i + "%");
//            table.textPref("oe-server", "127.0.0.1");
        });
//        ui.settings.buttons.button("@oblivine-engine.OEDebugPanelDialog", Icon.settings, () -> {OEUI.debugpanel.show();});
    }

    public static class OEUI implements ApplicationListener {
        private static Table cachedBlockCatTable, cachedCategories, customCategories = null;
        public static BaseDialog m_theorem = new TheoremDialog();
        public static BaseDialog IQC = new IQCdialog();
        public static BaseDialog exportOverview = new PlanetPreviewDialog();
        public static BaseDialog debugpanel = new OEDebugPanelDialog();
        boolean added;

        private static void addOEUI() {
            Events.on(EventType.ClientLoadEvent.class, e -> {
                ui.menuGroup.fill(c -> {OEUITools.setRelativeBounds(c,-0.85f,0f,0.05f,0.02f);c.button("@oblivine-engine.OEDebugPanelDialog",Icon.bookOpen, () -> {OEUI.debugpanel.show();});});

                for (MenuFragment.MenuButton button : ui.menufrag.desktopButtons) {
                    if (button != null && "@database.button".equals(button.text) && button.submenu != null) {
                        Seq<MenuFragment.MenuButton> newSubmenu = new Seq<>();

                        for (MenuFragment.MenuButton subButton : button.submenu) {
                            if (!"@database".equals(subButton.text)) {
                                newSubmenu.add(subButton);
                            }
                        }
                        MenuFragment.MenuButton newDatabaseButton = new MenuFragment.MenuButton(
                                "@database.button",
                                Icon.menu,
                                () -> {
                                },
                                newSubmenu.toArray(MenuFragment.MenuButton.class)
                        );
                        int index = ui.menufrag.desktopButtons.indexOf(button);
                        ui.menufrag.desktopButtons.set(index, newDatabaseButton);
                        break;
                    }
                }
                for (MenuFragment.MenuButton button : ui.menufrag.desktopButtons) {
                    if (button != null && "@database.button".equals(button.text) && button.submenu != null) {
                        Seq<MenuFragment.MenuButton> newSubmenu = new Seq<>();
                        for (MenuFragment.MenuButton subButton : button.submenu) {
                            newSubmenu.add(subButton);
                            if ("@schematics".equals(subButton.text)) {
                                newSubmenu.add(new MenuFragment.MenuButton("@theorem", Icon.bookOpen, () -> {
                                    m_theorem.show();
                                }));
                                newSubmenu.add(new MenuFragment.MenuButton("@boot.IQC", Icon.commandAttack, () -> {
                                    IQC.show();
                                }));
                            }
                        }
                        MenuFragment.MenuButton newDatabaseButton = new MenuFragment.MenuButton(
                                "@database.button",
                                Icon.menu,
                                () -> {
                                },
                                newSubmenu.toArray(MenuFragment.MenuButton.class)
                        );
                        boolean menuadded = false;
                        if (!menuadded) {
                            menuadded = true;
                            int index = ui.menufrag.desktopButtons.indexOf(button);
                            ui.menufrag.desktopButtons.set(index, newDatabaseButton);
                        }
                        break;
                    }
                }
            });
        }

        public static Table getCachedBlockCatTable() {
            Object frag = ui.hudfrag.blockfrag;

            Class<?> clazz = frag.getClass();
            Field f = null;
            try {
                f = clazz.getDeclaredField("blockCatTable");
            } catch (NoSuchFieldException err) {
                throw new RuntimeException(err);
            }
            f.setAccessible(true);
            try {
                cachedBlockCatTable = (Table) f.get(frag);
            } catch (IllegalAccessException err) {
                throw new RuntimeException(err);
            }
            return null;
        }

        public static Table getCachedCategoriesTable() {
            try {
                Object frag = ui.hudfrag.blockfrag;
                if (frag == null) return null;

                Field f1 = frag.getClass().getDeclaredField("blockCatTable");
                f1.setAccessible(true);
                Table blockCatTable = (Table) f1.get(frag);
                if (blockCatTable == null) return null;

                for (Element e : blockCatTable.getChildren()) {
                    if (e instanceof Table t) {
                        for (Element child : t.getChildren()) {
                            if (child.name != null && child.name.startsWith("category-")) {
                                return t;
                            }
                        }
                    }
                }
                return null;
            } catch (Throwable t) {
                return null;
            }
        }

        public static void rebuildCategories() {
            try {
                Object frag = Vars.ui.hudfrag.blockfrag;
                if (frag == null) return;

                Field f = frag.getClass().getDeclaredField("blockCatTable");
                f.setAccessible(true);
                Table blockCatTable = (Table) f.get(frag);
                if (blockCatTable == null) return;

                cachedBlockCatTable = blockCatTable;

                Table blocksSelect = null;
                for (Element e : blockCatTable.getChildren()) {
                    if (e instanceof Table t && t != customCategories) {
                        blocksSelect = t;
                        break;
                    }
                }

                blockCatTable.clearChildren();

                if (blocksSelect != null) {
                    blockCatTable.add(blocksSelect).fillY().bottom().touchable(Touchable.enabled);
                }

                if (customCategories != null) {
                    customCategories.clearChildren();
                }

                customCategories = new Table();
                customCategories.background(Styles.black6);
                customCategories.top();
                customCategories.touchable = Touchable.enabled;
                customCategories.visible = true;
                customCategories.defaults().size(50f);

                blockCatTable.add(customCategories).fillY().bottom().touchable(Touchable.enabled);

                blockCatTable.invalidate();
            } catch (Throwable t) {
                arc.util.Log.err("[OE] rebuildCategories failed", t);
            }
        }

        public static void addCategory(Category cat) {
            if (customCategories == null) {
                arc.util.Log.err("[OE] call clearCategories() first");
                return;
            }

            Object frag = Vars.ui.hudfrag.blockfrag;

            customCategories.button(
                    Vars.ui.getIcon(cat.name()),
                    Styles.clearTogglei,
                    () -> {
                        try {
                            Field cf = frag.getClass().getDeclaredField("currentCategory");
                            cf.setAccessible(true);
                            cf.set(frag, cat);
                        } catch (Throwable ignored) {}

                        ((mindustry.ui.fragments.PlacementFragment) frag).rebuild();
                    }
            ).update(i -> {
                try {
                    Field cf = frag.getClass().getDeclaredField("currentCategory");
                    cf.setAccessible(true);
                    i.setChecked(cf.get(frag) == cat);
                } catch (Throwable ignored) {}
            }).name("category-" + cat.name()).size(50f);
        }

        public static void nextCategoryRow() {
            if (customCategories != null) customCategories.row();
        }

        private static void buildCustomCategories() {
            OEUI.rebuildCategories();
            OEUI.addCategory(Category.turret);
            OEUI.addCategory(Category.production);
            OEUI.addCategory(Category.distribution);
            OEUI.nextCategoryRow();
            OEUI.addCategory(Category.liquid);
            OEUI.addCategory(Category.power);
            OEUI.addCategory(Category.defense);
            OEUI.nextCategoryRow();
            OEUI.addCategory(Category.crafting);
            OEUI.addCategory(Category.units);
            OEUI.addCategory(Category.effect);
            OEUI.nextCategoryRow();
            OEUI.addCategory(Category.logic);
            OEUI.addCategory(OECategory.parts);
        }

        @Override
        public void init() {
            getCachedBlockCatTable();
            getCachedCategoriesTable();

            addOEUI();
        }

        private static Table debugPanelBtn = null;

        @Override
        public void update() {
            boolean want = Core.settings.getBool("@oblivine-engine.DebugSwitch", false);

            if (ui.hudfrag != null && ui.hudfrag.blockfrag != null) {
                try {
                    Field f = ui.hudfrag.blockfrag.getClass().getDeclaredField("blockCatTable");
                    f.setAccessible(true);
                    Table bct = (Table) f.get(ui.hudfrag.blockfrag);
                    if (bct != null) {
                        boolean present = customCategories != null
                                && customCategories.parent == bct
                                && customCategories.getChildren().size > 0;
                        if (!present) {
                            buildCustomCategories();
                        }
                    }
                } catch (Throwable ignored) {}
            }

            if (want && debugPanelBtn == null) {
                debugPanelBtn = new Table() {{
                    OEUITools.setRelativeBounds(this, 0.1f, 0.8f, 0f, 0f);
                    button("@oblivine-engine.OEDebugPanelDialog", Icon.bookOpen, () -> {
                        OEUI.debugpanel.show();
                    });
                }};
                Core.scene.add(debugPanelBtn);
            } else if (!want && debugPanelBtn != null) {
                debugPanelBtn.remove();
                debugPanelBtn = null;
            }

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
                        scaling = Mathf.clamp(scaling - amountY / 3, Mathf.log(2, 10 / ui.planet.state.planet.solarSystem.totalRadius), Mathf.log(2, 20f));
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
            content.planets().each(p -> {
                if (!(p.solarSystem == ui.planet.state.planet.solarSystem)) return;
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

    public static class TheoremDialog extends BaseDialog {
        Element temp,temp1;
        String therom = "rational_number";
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
//            addCloseButton();
            touchable = Touchable.enabled;
            buildInterface();
            closeOnBack();
        }

        private void buildInterface() {
            WidgetGroup group = new WidgetGroup();
//            group.addChild(new Button(){{OEUITools.setRelativeBounds(this,0f,0f,90,90);}});
//            group.addChild(new Label("@definition."+therom+".name"){{setBounds(-500,-400,50,50);}})
            group.addChild(new Label("@definition."+therom+".name"){{
                OEUITools.setRelativeBounds(this,0f,0.945f,50,50);
                setColor(techBlue.a(1f));
//                setFontScale(1.8f);
                setAlignment(Align.center);
            }});
            group.addChild(
                temp = new Element(){
                    @Override
                    public void draw(){
                        Draw.color(techBlue);
                        float barHeight = 0f;
                        float padding = 1920f;
                        Draw.alpha(0.35f);
                        Fill.crect(x + padding - 2f, y + height - barHeight - 7f, width - (padding - 2f) * 2f, barHeight + 4f);
                        Draw.reset();
                    }
                }
            );
            OEUITools.setRelativeBounds(temp,0f,0.867f,50,50);
            group.addChild(
                new Table(){{
                    OEUITools.setRelativeBounds(this,0f,0.868f,50,50);
                    margin(15f);
                    add(new Label("@definition."+therom+".content"){{
                        setWrap(true);
                        setAlignment(Align.center, Align.left);
                        setColor(OEColor.highwhite);
                    }});
                }}
            );
            group.addChild(new Table(){{
                OEUITools.setRelativeBounds(this,-0.976f,0.956f,1,1);
                add(new ImageButton(Icon.cancel, Styles.clearNonei){{
                    update(() -> {
                        getStyle().imageUpColor = isOver() ? Color.white : techBlue;
                    });
                    clicked(() -> hide());
                }}).size(25f);
            }});
            group.addChild(new Table(){{
                add(new Element() {
                    private float time = 0f;
                    private Color[] chessPattern = {Color.blue.mulA(0.3F).g(200f).b(200f),techBlue.mulA(0.3F)};  // 棋盘图案
                    private Color lineColor = Color.darkGray;  // 线框颜色

                    @Override
                    public void draw() {
                        time += Time.delta;
                        // 在Element的局部坐标系中，中心是它的中间
                        float centerX = 111f;
                        float centerY = 111f;
                        float radius = 90f;  // 减小半径适应50x50大小
                        // 减慢旋转速度
                        float slowFactor = 0.333f;
                        float rotationX = time * 0.2f * slowFactor;  // X轴旋转
                        float rotationY = time * 0.15f * slowFactor; // Y轴旋转
                        float rotationZ = time * 0.1f * slowFactor;  // Z轴旋转
                        // 绘制3D球体的线框
                        Draw.color(lineColor);
                        Draw.alpha(0.6f);
                        Lines.stroke(1f);  // 减细线宽
                        // 简化网格，适应小尺寸
                        for (int lon = 0; lon < 180; lon += 30) {
                            Lines.beginLine();
                            for (int lat = 0; lat <= 360; lat += 10) {
                                float x = get3DX(lat, lon, radius, rotationX, rotationY, rotationZ);
                                float y = get3DY(lat, lon, radius, rotationX, rotationY, rotationZ);
                                Lines.linePoint(centerX + x, centerY + y);
                            }
                            Lines.endLine();
                        }
                        // 绘制纬线
                        for (int lat = 0; lat < 180; lat += 30) {
                            Lines.beginLine();
                            for (int lon = 0; lon <= 360; lon += 10) {
                                float x = get3DX(lon, lat, radius, rotationX, rotationY, rotationZ);
                                float y = get3DY(lon, lat, radius, rotationX, rotationY, rotationZ);
                                Lines.linePoint(centerX + x, centerY + y);
                            }
                            Lines.endLine();
                        }
                        // 简化棋盘图案
                        int segments = 6;
                        float angleStep = 360f / segments;

                        for (int i = 0; i < segments; i++) {
                            for (int j = 0; j < segments; j++) {
                                float angle1 = i * angleStep;
                                float angle2 = (i + 1) * angleStep;
                                float angle3 = j * angleStep;
                                float angle4 = (j + 1) * angleStep;
                                // 计算四个顶点
                                float[] points = new float[8];
                                int idx = 0;

                                for (float lat = angle3; lat <= angle4; lat += angleStep) {
                                    for (float lon = angle1; lon <= angle2; lon += angleStep) {
                                        points[idx++] = centerX + get3DX(lon, lat, radius, rotationX, rotationY, rotationZ);
                                        points[idx++] = centerY + get3DY(lon, lat, radius, rotationX, rotationY, rotationZ);
                                    }
                                }
                                // 绘制棋盘方格
                                if ((i + j) % 2 == 0) {
                                    Draw.color(chessPattern[0]);
                                } else {
                                    Draw.color(chessPattern[1]);
                                }
                                Draw.alpha(0.6f);  // 增加透明度
                                // 绘制四边形
                                if (points.length >= 8) {
                                    Fill.quad(
                                            points[0], points[1],
                                            points[2], points[3],
                                            points[4], points[5],
                                            points[6], points[7]
                                    );
                                }
                            }
                        }
                        // 绘制中心点标记
                        Draw.color(Color.red);
                        Fill.circle(centerX, centerY, 2f);

                        Draw.reset();
                    }
                    // 3D坐标转换函数
                    private float get3DX(float lon, float lat, float radius, float rx, float ry, float rz) {
                        float lonRad = Mathf.degRad * lon;
                        float latRad = Mathf.degRad * lat;

                        float x = radius * Mathf.sin(latRad) * Mathf.cos(lonRad);
                        float y = radius * Mathf.sin(latRad) * Mathf.sin(lonRad);
                        float z = radius * Mathf.cos(latRad);

                        float tempY = y * Mathf.cos(rx) - z * Mathf.sin(rx);
                        float tempZ = y * Mathf.sin(rx) + z * Mathf.cos(rx);
                        y = tempY;
                        z = tempZ;

                        float tempX = x * Mathf.cos(ry) + z * Mathf.sin(ry);
                        tempZ = -x * Mathf.sin(ry) + z * Mathf.cos(ry);
                        x = tempX;
                        z = tempZ;

                        tempX = x * Mathf.cos(rz) - y * Mathf.sin(rz);
                        tempY = x * Mathf.sin(rz) + y * Mathf.cos(rz);
                        x = tempX;
                        y = tempY;

                        return x;
                    }

                    private float get3DY(float lon, float lat, float radius, float rx, float ry, float rz) {
                        float lonRad = Mathf.degRad * lon;
                        float latRad = Mathf.degRad * lat;

                        float x = radius * Mathf.sin(latRad) * Mathf.cos(lonRad);
                        float y = radius * Mathf.sin(latRad) * Mathf.sin(lonRad);
                        float z = radius * Mathf.cos(latRad);

                        float tempY = y * Mathf.cos(rx) - z * Mathf.sin(rx);
                        float tempZ = y * Mathf.sin(rx) + z * Mathf.cos(rx);
                        y = tempY;
                        z = tempZ;

                        float tempX = x * Mathf.cos(ry) + z * Mathf.sin(ry);
                        tempZ = -x * Mathf.sin(ry) + z * Mathf.cos(ry);
                        x = tempX;
                        z = tempZ;

                        tempX = x * Mathf.cos(rz) - y * Mathf.sin(rz);
                        tempY = x * Mathf.sin(rz) + y * Mathf.cos(rz);
                        x = tempX;
                        y = tempY;

                        return y;
                    }
                }).grow();
            }});

            cont.add(group);
        }

        @Override
        public void hide() {
            super.hide();
//            Sounds.click.play();
        }
    }

    public static class IQCdialog extends BaseDialog {
        public IQCdialog() {
            super("");
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
            closeOnBack();
            setStyle(OEStyle.TheoremDialog);

            WidgetGroup container = new WidgetGroup();

        }
    }

    public static class OEDebugPanelDialog extends BaseDialog {
        public OEDebugPanelDialog() {
            super("");

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
            closeOnBack();
            setStyle(OEStyle.DebugPanel);
            addCloseButton();
            WidgetGroup container = new WidgetGroup();

            Table noise = new Table(){
                @Override
                public void draw() {
                    super.draw();

                    float centerX = getX(Align.center);
                    float centerY = getY(Align.center);

                    float leftBottomX = -150f;
                    float leftBottomY = -260f;
                    float rightTopX = 190f;
                    float rightTopY = 360f;

                    float actualLeftBottomX = centerX + leftBottomX;
                    float actualLeftBottomY = centerY + leftBottomY;
                    float actualRightTopX = centerX + rightTopX;
                    float actualRightTopY = centerY + rightTopY;

                    float width = actualRightTopX - actualLeftBottomX;
                    float height = actualRightTopY - actualLeftBottomY;

                    Draw.color(techBlue);
                    Lines.stroke(2f);
                    Lines.rect(actualLeftBottomX, actualLeftBottomY, width, height);
                    Draw.reset();
                }
                {
                    OENoise.PerlinNoiseElement noiseElement = new OENoise.PerlinNoiseElement(12345, 100, 100, 0.1f, 6, 0.5);
                    Slider scaleSlider,octavesSlider,persistenceSlider;
                    TextField seedField;

                    OEUITools.setRelativeBounds(this,-0.7f,0f,0f,0f);// 调整位置
                    add(noiseElement).size(200f).pad(5f).row();
                    row();
                    add(new Label("柏林噪声控制")).color(techBlue).row();
                    add(new Label("种子:"));
                    add(seedField = new TextField("12345"){{setMaxLength(10);}}).pad(5f).row();
                    add(new Label("缩放:"));
                    add(scaleSlider = new Slider(0.01f, 1.0f, 0.01f, false){{setValue(0.1f);}}).pad(5f).row();
                    add(new Label("层数:"));
                    add(octavesSlider = new Slider(1, 10, 1, false){{setValue(6);}}).pad(5f).row();
                    add(new Label("持久度:"));
                    add(persistenceSlider = new Slider(0.1f, 1.0f, 0.1f, false){{setValue(0.5f);}}).pad(5f).row();
                    button("重新生成噪声", () -> {
                        try {
                            int newSeed = Integer.parseInt(seedField.getText());
                            float newScale = scaleSlider.getValue();
                            int newOctaves = (int)octavesSlider.getValue();
                            double newPersistence = persistenceSlider.getValue();

                            Log.info("[OE][Debug] 柏林噪声：种子: "+seedField.getText()+", 缩放: "+scaleSlider.getValue()+", 层数: "+octavesSlider.getValue()+", 持久度: "+persistenceSlider.getValue());

                            if (noiseElement != null) {
                                noiseElement.regenerate(newSeed, newScale, newOctaves, newPersistence);
                            }
                        } catch (NumberFormatException e) {

                        }
                    }).size(90f, 50f).pad(10f).row();
                }
            };
            Table weather = new Table() {
                @Override
                public void draw() {
                    super.draw();

                    float centerX = getX(Align.center);
                    float centerY = getY(Align.center);

                    float leftBottomX = -150f;
                    float leftBottomY = -260f;
                    float rightTopX = 190f;
                    float rightTopY = 360f;

                    float actualLeftBottomX = centerX + leftBottomX;
                    float actualLeftBottomY = centerY + leftBottomY;
                    float actualRightTopX = centerX + rightTopX;
                    float actualRightTopY = centerY + rightTopY;

                    float width = actualRightTopX - actualLeftBottomX;
                    float height = actualRightTopY - actualLeftBottomY;

                    Draw.color(techBlue);
                    Lines.stroke(2f);
                    Lines.rect(actualLeftBottomX, actualLeftBottomY, width, height);
                    Draw.reset();
                }
                {
                TextField time;
                Color borderColor = techBlue;
                float borderThickness = 2f;
                float padding = 0f;

                float x = getX(0) - padding;
                float y = getY(0) - padding;
                float width = getWidth() + padding * 2;
                float height = getHeight() + padding * 2;
                Draw.color(borderColor);
                Lines.stroke(borderThickness);
                Lines.rect(x, y, width, height);
                Draw.reset();

                OEUITools.setRelativeBounds(this,-0.3f,0f,0f,0f);// 调整位置
                add(new Label("天气控制器")).color(techBlue).padTop(20f).row();
                add(new Label("持续时间(s):")).left().padLeft(10f);
                add(time = new TextField("30"){{setMaxLength(10);}}).width(100f).pad(5f).row();
                button("晴天  ", () -> {mods.getScripts().runConsole("Groups.weather.each(w => w.remove());");}).width(100f).pad(5f).row();
                button("雪天  ", () -> {mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'snowing').create(1.0, "+String.valueOf(60 * Integer.parseInt(time.getText()))+")");}).width(100f).pad(5f).row();
                button("雨天  ", () -> {mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'rain').create(1.0, "+String.valueOf(60 * Integer.parseInt(time.getText()))+")");}).width(100f).pad(5f).row();
                button("沙尘暴", () -> {mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'sandstorm').create(1.0, "+String.valueOf(60 * Integer.parseInt(time.getText()))+")");}).width(100f).pad(5f).row();
                button("孢子雨", () -> {mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'sporestorm').create(1.0, "+String.valueOf(60 * Integer.parseInt(time.getText()))+")");}).width(100f).pad(5f).row();
                button("雾    ", () -> {mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'fog').create(1.0, "+String.valueOf(60 * Integer.parseInt(time.getText()))+")");}).width(100f).pad(5f).row();
                button("粒子  ", () -> {mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'suspend-particles').create(1.0, "+String.valueOf(60 * Integer.parseInt(time.getText()))+")");}).width(100f).pad(5f).row();
            }};
            Table objRender = new Table() {
                @Override
                public void draw() {
                    super.draw();

                    float centerX = getX(Align.center);
                    float centerY = getY(Align.center);

                    float leftBottomX = -150f;
                    float leftBottomY = -260f;
                    float rightTopX = 190f;
                    float rightTopY = 360f;

                    float actualLeftBottomX = centerX + leftBottomX;
                    float actualLeftBottomY = centerY + leftBottomY;
                    float actualRightTopX = centerX + rightTopX;
                    float actualRightTopY = centerY + rightTopY;

                    float width = actualRightTopX - actualLeftBottomX;
                    float height = actualRightTopY - actualLeftBottomY;

                    Draw.color(techBlue);
                    Lines.stroke(2f);
                    Lines.rect(actualLeftBottomX, actualLeftBottomY, width, height);
                    Draw.reset();
                }
                {
                    // 创建OBJ渲染器
                    OBJRenderer objRenderer = new OBJRenderer();
                    TextField pathField;
                    Slider rotXSlider, rotYSlider, rotZSlider, scaleSlider;
                    TextField rotXField, rotYField, rotZField, scaleField;

                    OEUITools.setRelativeBounds(this,0.1f,0f,0f,0f);

                    // 标题
                    add(new Label("OBJ模型渲染器")).color(techBlue).padTop(20f).row();

                    // 创建渲染区域
                    Table renderArea = new Table();
                    renderArea.background(Styles.black6);
                    renderArea.add(objRenderer).size(200f, 200f);
                    add(renderArea).pad(5f).row();

                    // 文件路径输入
                    Table pathRow = new Table();
                    pathRow.add(new Label("OBJ路径:")).width(60f).padRight(5f);
                    pathRow.add(pathField = new TextField(""){{setMessageText("输入OBJ文件路径");}}).width(150f);
                    pathRow.button("加载模型", () -> {
                        String path = pathField.getText();
                        if(!path.isEmpty()) {
                            objRenderer.loadOBJ(path);
                        }
                    }).size(120f, 35f).pad(5f).row();
                    add(pathRow).pad(5f).row();

                    // 旋转控制
                    add(new Label("旋转控制")).color(techBlue).width(60f).padRight(5f).padTop(10f).row();

                    // X旋转
                    Table rotXRow = new Table();
                    rotXRow.defaults().pad(2f);
                    rotXRow.add(new Label("X:")).width(60f).padRight(10f);
                    rotXRow.add(rotXSlider = new Slider(-180f, 180f, 1f, false){{
                        setValue(0f);
                    }}).width(100f);
                    rotXField = new TextField("0"){{
                        setMessageText("角度");
                        setMaxLength(6);
                    }};
                    rotXRow.add(rotXField).width(100f).padLeft(5f);
                    add(rotXRow).pad(3f).row();

                    // Y旋转
                    Table rotYRow = new Table();
                    rotYRow.defaults().pad(2f);
                    rotYRow.add(new Label("Y:")).width(60f).padRight(10f);
                    rotYRow.add(rotYSlider = new Slider(-180f, 180f, 1f, false){{
                        setValue(0f);
                    }}).width(100f);
                    rotYField = new TextField("0"){{
                        setMessageText("角度");
                        setMaxLength(6);
                    }};
                    rotYRow.add(rotYField).width(100f).padLeft(5f);
                    add(rotYRow).pad(3f).row();

                    // Z旋转
                    Table rotZRow = new Table();
                    rotZRow.defaults().pad(2f);
                    rotZRow.add(new Label("Z:")).width(60f).padRight(10f);
                    rotZRow.add(rotZSlider = new Slider(-180f, 180f, 1f, false){{
                        setValue(0f);
                    }}).width(100f);
                    rotZField = new TextField("0"){{
                        setMessageText("角度");
                        setMaxLength(6);
                    }};
                    rotZRow.add(rotZField).width(100f).padLeft(5f);
                    add(rotZRow).pad(3f).row();

                    // 缩放控制
                    Table scaleRow = new Table();
                    scaleRow.defaults().pad(2f);
                    scaleRow.add(new Label("缩放:")).width(60f).padRight(10f);
                    scaleRow.add(scaleSlider = new Slider(0.1f, 30f, 0.1f, false){{
                        setValue(1f);
                    }}).width(100f);
                    scaleField = new TextField("1.0"){{
                        setMessageText("倍数");
                        setMaxLength(5);
                    }};
                    scaleRow.add(scaleField).width(100f).padLeft(5f);
                    add(scaleRow).pad(3f).row();

                    // 滑块变化时，只更新文本框（单向）
                    rotXSlider.changed(() -> {
                        rotXField.setText(String.format("%.1f", rotXSlider.getValue()));
                    });

                    rotYSlider.changed(() -> {
                        rotYField.setText(String.format("%.1f", rotYSlider.getValue()));
                    });

                    rotZSlider.changed(() -> {
                        rotZField.setText(String.format("%.1f", rotZSlider.getValue()));
                    });

                    scaleSlider.changed(() -> {
                        scaleField.setText(String.format("%.2f", scaleSlider.getValue()));
                    });

                    // 应用旋转和缩放按钮
                    Table controlButtons = new Table();
                    controlButtons.defaults().size(60f, 25f).pad(2f);

                    controlButtons.button("应用", () -> {
                        // 从文本框读取值并设置到滑块
                        try {
                            float xVal = Float.parseFloat(rotXField.getText());
                            if (xVal >= -180f && xVal <= 180f) {
                                rotXSlider.setValue(xVal);
                            } else {
                                rotXField.setText(String.format("%.1f", rotXSlider.getValue()));
                            }
                        } catch (NumberFormatException e) {
                            rotXField.setText(String.format("%.1f", rotXSlider.getValue()));
                        }

                        try {
                            float yVal = Float.parseFloat(rotYField.getText());
                            if (yVal >= -180f && yVal <= 180f) {
                                rotYSlider.setValue(yVal);
                            } else {
                                rotYField.setText(String.format("%.1f", rotYSlider.getValue()));
                            }
                        } catch (NumberFormatException e) {
                            rotYField.setText(String.format("%.1f", rotYSlider.getValue()));
                        }

                        try {
                            float zVal = Float.parseFloat(rotZField.getText());
                            if (zVal >= -180f && zVal <= 180f) {
                                rotZSlider.setValue(zVal);
                            } else {
                                rotZField.setText(String.format("%.1f", rotZSlider.getValue()));
                            }
                        } catch (NumberFormatException e) {
                            rotZField.setText(String.format("%.1f", rotZSlider.getValue()));
                        }

                        try {
                            float scaleVal = Float.parseFloat(scaleField.getText());
                            if (scaleVal >= 0.1f && scaleVal <= 3f) {
                                scaleSlider.setValue(scaleVal);
                            } else {
                                scaleField.setText(String.format("%.2f", scaleSlider.getValue()));
                            }
                        } catch (NumberFormatException e) {
                            scaleField.setText(String.format("%.2f", scaleSlider.getValue()));
                        }

                        // 应用旋转和缩放
                        float x = rotXSlider.getValue() * Mathf.degRad;
                        float y = rotYSlider.getValue() * Mathf.degRad;
                        float z = rotZSlider.getValue() * Mathf.degRad;
                        objRenderer.setRotation(x, y, z);
                        objRenderer.setScale(scaleSlider.getValue());
                    });

                    controlButtons.button("重置", () -> {
                        rotXSlider.setValue(0f);
                        rotYSlider.setValue(0f);
                        rotZSlider.setValue(0f);
                        scaleSlider.setValue(1f);

                        rotXField.setText("0.0");
                        rotYField.setText("0.0");
                        rotZField.setText("0.0");
                        scaleField.setText("1.00");

                        objRenderer.setRotation(0f, 0f, 0f);
                        objRenderer.setScale(1f);
                    });

                    add(controlButtons).pad(5f).row();

                    // 显示模式切换
                    Table modeButtons = new Table();
                    modeButtons.defaults().size(70f, 25f).pad(2f);

                    modeButtons.button("线框", () -> {
                        objRenderer.setWireframe(true);
                    });

                    modeButtons.button("填充", () -> {
                        objRenderer.setWireframe(false);
                    });

                    add(modeButtons).pad(5f);

                    // 初始化应用旋转
                    objRenderer.setRotation(0f, 0f, 0f);
                }
            };
            Table temp = new Table() {{
                this.button("启动", () -> {
                }).size(120f, 35f).pad(5f).row();
            }};

            container.addChild(noise);
            container.addChild(weather);
            container.addChild(objRender);
            container.addChild(temp);

            cont.add(container);

        }
    }
}
