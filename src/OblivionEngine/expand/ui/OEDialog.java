package OblivionEngine.expand.ui;

import OblivionEngine.content.OEColor;
import OblivionEngine.content.OENoise;
import OblivionEngine.content.OEStyle;
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
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.fragments.MenuFragment;

import static OblivionEngine.content.OEColor.techBlue;
import static mindustry.Vars.*;
import static mindustry.ui.dialogs.PlanetDialog.Mode.look;

public class OEDialog {

    public OEDialog() {

    }

    public static void load() {//这是添加触发到已有按钮中
        Vars.ui.settings.addCategory("@oblivine-engine.setting", Icon.settings, Table::clearChildren);
//        ui.settings.buttons.button("@oblivine-engine.OEDebugPanelDialog", Icon.settings, () -> {OEUI.debugpannel.show();});
    }

    public static class OEUI implements ApplicationListener {
        public BaseDialog m_theorem = new TheoremDialog();
        public BaseDialog IQC = new IQCdialog();
        public BaseDialog exportOverview = new PlanetPreviewDialog();
        public static BaseDialog debugpanel = new OEDebugPanelDialog();
        boolean added;

        @Override
        public void init() {
            Events.on(EventType.ClientLoadEvent.class, e -> {
//                Vars.ui.menuGroup.fill(c -> {OEUITools.setRelativeBounds(c,-0.85f,0f,0.05f,0.02f);c.button("@oblivine-engine.OEDebugPanelDialog",Icon.bookOpen, () -> {OEUI.debugpanel.show();});});// 调试界面
                Core.scene.add(new Table(){{OEUITools.setRelativeBounds(this,0.1f,0.8f,0f,0f); button("@oblivine-engine.OEDebugPanelDialog",Icon.bookOpen, () -> {OEUI.debugpanel.show();});}});

                for (MenuFragment.MenuButton button : ui.menufrag.desktopButtons) {
                    if (button != null && "@database.button".equals(button.text) && button.submenu != null) {
                        Seq<MenuFragment.MenuButton> newSubmenu = new Seq<>();

                        for (MenuFragment.MenuButton subButton : button.submenu) {
                            if (!"@database".equals(subButton.text)) {  // 跳过 @database
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
                        Seq<MenuFragment.MenuButton> newSubmenu = new Seq<>();// 找到 database.button 的 submenu
                        for (MenuFragment.MenuButton subButton : button.submenu) {// 遍历原有子菜单
                            newSubmenu.add(subButton);// 添加原有按钮
                            if ("@schematics".equals(subButton.text)) {// 在 @schematics 之后添加自定义按钮
                                newSubmenu.add(new MenuFragment.MenuButton("@theorem", Icon.bookOpen, () -> {
                                    m_theorem.show();
                                }));
                                newSubmenu.add(new MenuFragment.MenuButton("@boot.IQC", Icon.commandAttack, () -> {
                                    IQC.show();
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

            Table noise = new Table(){{
                OENoise.PerlinNoiseElement noiseElement = new OENoise.PerlinNoiseElement(12345, 100, 100, 0.1f, 6, 0.5);
                Slider scaleSlider,octavesSlider,persistenceSlider;
                TextField seedField;

                OEUITools.setRelativeBounds(this,-0.7f,0f,0f,0f);// 调整位置
                add(new Table(){{add(noiseElement).grow();}}).size(200f, 200f).pad(10f).row();
                add(new Label("柏林噪声控制")).color(techBlue).padTop(20f).row();
                add(new Label("种子:")).left().padLeft(10f);
                add(seedField = new TextField("12345"){{setMaxLength(10);}}).width(100f).pad(5f).row();
                add(new Label("缩放:")).left().padLeft(10f);
                add(scaleSlider = new Slider(0.01f, 1.0f, 0.01f, false){{setValue(0.1f);}}).width(200f).pad(5f).row();
                add(new Label("层数:")).left().padLeft(10f);
                add(octavesSlider = new Slider(1, 10, 1, false){{setValue(6);}}).width(200f).pad(5f).row();
                add(new Label("持久度:")).left().padLeft(10f);
                add(persistenceSlider = new Slider(0.1f, 1.0f, 0.1f, false){{setValue(0.5f);}}).width(200f).pad(5f).row();
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
                }).size(150f, 40f).pad(10f).row();
            }};
            container.addChild(noise);
            Table weather = new Table(){{
                TextField time;

                OEUITools.setRelativeBounds(this,0f,0f,0f,0f);// 调整位置
                add(new Label("天气控制器")).color(techBlue).padTop(20f).row();
                add(new Label("持续时间(s):")).left().padLeft(10f);
                add(time = new TextField("30"){{setMaxLength(10);}}).width(100f).pad(5f).row();
                button("晴天", () -> {mods.getScripts().runConsole("Groups.weather.each(w => w.remove());");}).width(100f).pad(5f).row();
                button("雨天", () -> {mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'rain').create(1.0, "+String.valueOf(60 * Integer.parseInt(time.getText()))+")");}).width(100f).pad(5f).row();
            }};
            container.addChild(weather);

            cont.add(container);
        }
    }
}
