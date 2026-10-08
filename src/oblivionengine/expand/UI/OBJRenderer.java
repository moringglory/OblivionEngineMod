package oblivionengine.expand.UI;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.scene.Element;
import arc.util.Align;
import arc.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// OBJ 模型渲染器
public class OBJRenderer extends Element {
    private List<Vec3> vertices = new ArrayList<>();
    private List<Face> faces = new ArrayList<>();
    private Color lineColor = Color.white;
    private Color fillColor = new Color(1, 1, 1, 0.3f);
    private float scale = 1f;
    private float rotationX = 0f, rotationY = 0f, rotationZ = 0f;
    private boolean wireframe = true;
    private boolean loaded = false;
    private String currentPath = "";

    public void loadOBJ(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            vertices.clear();
            faces.clear();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;

                switch (parts[0]) {
                    case "v":  // 顶点
                        if (parts.length >= 4) {
                            float x = Float.parseFloat(parts[1]);
                            float y = Float.parseFloat(parts[2]);
                            float z = parts.length > 3 ? Float.parseFloat(parts[3]) : 0f;
                            vertices.add(new Vec3(x, y, z));
                        }
                        break;

                    case "f":  // 面
                        parseFace(parts);
                        break;
                }
            }

            loaded = true;
            currentPath = filePath;
            Log.info("OBJ加载成功: " + vertices.size() + "个顶点, " + faces.size() + "个面");

        } catch (Exception e) {
            Log.err("加载OBJ失败: " + e);
            loaded = false;
        }
    }

    private void parseFace(String[] parts) {
        if (parts.length < 4) return;

        int[] vIndices = new int[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            String[] indices = parts[i].split("/");
            vIndices[i-1] = Integer.parseInt(indices[0]) - 1; // OBJ索引从1开始
        }

        // 三角化
        for (int i = 1; i < vIndices.length - 1; i++) {
            faces.add(new Face(vIndices[0], vIndices[i], vIndices[i + 1]));
        }
    }

    @Override
    public void draw() {
        if (!loaded || vertices.isEmpty()) return;

        // 获取渲染区域的中心
        float centerX = getX(Align.center);
        float centerY = getY(Align.center);

        // 计算包围盒
        Vec3 min = new Vec3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        Vec3 max = new Vec3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

        for (Vec3 v : vertices) {
            min.x = Math.min(min.x, v.x);
            min.y = Math.min(min.y, v.y);
            min.z = Math.min(min.z, v.z);
            max.x = Math.max(max.x, v.x);
            max.y = Math.max(max.y, v.y);
            max.z = Math.max(max.z, v.z);
        }

        float size = Math.max(max.x - min.x, Math.max(max.y - min.y, max.z - min.z));
        if (size == 0) size = 1f;

        // 计算缩放比例
        float modelScale = Math.min(getWidth(), getHeight()) * 0.4f / size * scale;

        // 渲染每个面
        for (Face face : faces) {
            Vec3 v1 = vertices.get(face.v1).cpy();
            Vec3 v2 = vertices.get(face.v2).cpy();
            Vec3 v3 = vertices.get(face.v3).cpy();

            // 居中
            Vec3 center = new Vec3(
                    (min.x + max.x) * 0.5f,
                    (min.y + max.y) * 0.5f,
                    (min.z + max.z) * 0.5f
            );

            v1.sub(center);
            v2.sub(center);
            v3.sub(center);

            // 应用旋转 - 旋转顺序很重要
            // 先Z轴（平面旋转），再Y轴，再X轴
            rotateVertex(v1, rotationX, rotationY, rotationZ);
            rotateVertex(v2, rotationX, rotationY, rotationZ);
            rotateVertex(v3, rotationX, rotationY, rotationZ);

            // 缩放
            v1.scl(modelScale);
            v2.scl(modelScale);
            v3.scl(modelScale);

            // 转换到屏幕坐标
            Vec2 p1 = toScreen(v1, centerX, centerY);
            Vec2 p2 = toScreen(v2, centerX, centerY);
            Vec2 p3 = toScreen(v3, centerX, centerY);

            if (!wireframe) {
                // 填充
                Draw.color(fillColor);
                Fill.tri(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
            }

            // 线框
            Draw.color(lineColor);
            Lines.stroke(1f);
            Lines.line(p1.x, p1.y, p2.x, p2.y);
            Lines.line(p2.x, p2.y, p3.x, p3.y);
            Lines.line(p3.x, p3.y, p1.x, p1.y);
        }

        Draw.reset();
    }

    // 旋转单个顶点，旋转顺序：Z -> Y -> X
    private void rotateVertex(Vec3 v, float rx, float ry, float rz) {
        float x = v.x, y = v.y, z = v.z;

        // 1. 绕Z轴旋转（平面旋转）
        if (rz != 0) {
            float cosZ = Mathf.cos(rz);
            float sinZ = Mathf.sin(rz);
            float newX = x * cosZ - y * sinZ;
            float newY = x * sinZ + y * cosZ;
            x = newX;
            y = newY;
        }

        // 2. 绕Y轴旋转
        if (ry != 0) {
            float cosY = Mathf.cos(ry);
            float sinY = Mathf.sin(ry);
            float newX = x * cosY + z * sinY;
            float newZ = -x * sinY + z * cosY;
            x = newX;
            z = newZ;
        }

        // 3. 绕X轴旋转
        if (rx != 0) {
            float cosX = Mathf.cos(rx);
            float sinX = Mathf.sin(rx);
            float newY = y * cosX - z * sinX;
            float newZ = y * sinX + z * cosX;
            y = newY;
            z = newZ;
        }

        v.set(x, y, z);
    }

    private Vec2 toScreen(Vec3 v, float centerX, float centerY) {
        // 简单正交投影，忽略Z轴
        return new Vec2(
                centerX + v.x,
                centerY + v.y
        );
    }

    public void setScale(float scale) { this.scale = scale; }
    public void setRotation(float x, float y, float z) {
        rotationX = x;
        rotationY = y;
        rotationZ = z;
    }
    public void setWireframe(boolean wireframe) { this.wireframe = wireframe; }
    public void setColors(Color line, Color fill) { lineColor = line; fillColor = fill; }
    public boolean isLoaded() { return loaded; }
    public String getCurrentPath() { return currentPath; }

    private static class Face {
        int v1, v2, v3;
        Face(int v1, int v2, int v3) { this.v1 = v1; this.v2 = v2; this.v3 = v3; }
    }
}