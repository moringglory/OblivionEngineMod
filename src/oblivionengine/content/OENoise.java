package oblivionengine.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.scene.Element;
import mindustry.Vars;

import static arc.Core.camera;

public class OENoise {
    public static class PerlinNoiseWorldRenderer {
        private OEPerlinNoise perlin;
        private int seed = 12345;
        private float scale = 0.1f;
        private int octaves = 6;
        private double persistence = 0.5;
        private boolean active = false;
        private Color color1 = Color.white;
        private Color color2 = Color.black;
        private float cellSize = 8f;  // 每个噪声单元的大小
        private float opacity = 0.5f; // 透明度

        public PerlinNoiseWorldRenderer(int seed, float scale, int octaves, double persistence) {
            this.seed = seed;
            this.scale = scale;
            this.octaves = octaves;
            this.persistence = persistence;
            this.perlin = new OEPerlinNoise(seed);
        }

        // 绘制整个世界
        public void drawWorldNoise() {
            if (!active) return;

            float startX = 0;
            float endX = Vars.world.width() * Vars.tilesize;
            float startY = 0;
            float endY = Vars.world.height() * Vars.tilesize;

            Draw.draw(Draw.z(), () -> {
                for (float y = startY; y < endY; y += cellSize) {
                    for (float x = startX; x < endX; x += cellSize) {
                        drawNoiseCell(x, y);
                    }
                }
            });
        }

        // 绘制当前屏幕区域
        public void drawScreenNoise() {
            if (!active) return;

            // 获取屏幕边界的世界坐标
            float camX = camera.position.x;
            float camY = camera.position.y;
            float camWidth = camera.width;
            float camHeight = camera.height;

            float startX = camX - camWidth / 2 - cellSize;
            float endX = camX + camWidth / 2 + cellSize;
            float startY = camY - camHeight / 2 - cellSize;
            float endY = camY + camHeight / 2 + cellSize;

            // 确保不超出地图边界
            startX = Mathf.clamp(startX, 0, Vars.world.width() * Vars.tilesize);
            endX = Mathf.clamp(endX, 0, Vars.world.width() * Vars.tilesize);
            startY = Mathf.clamp(startY, 0, Vars.world.height() * Vars.tilesize);
            endY = Mathf.clamp(endY, 0, Vars.world.height() * Vars.tilesize);

            float finalStartY = startY;
            float finalEndY = endY;
            float finalStartX = startX;
            float finalEndX = endX;
            Draw.draw(Draw.z(), () -> {
                for (float y = finalStartY; y < finalEndY; y += cellSize) {
                    for (float x = finalStartX; x < finalEndX; x += cellSize) {
                        drawNoiseCell(x, y);
                    }
                }
            });
        }

        // 绘制单个噪声单元
        private void drawNoiseCell(float worldX, float worldY) {
            // 计算噪声值
            float noiseX = worldX * scale;
            float noiseY = worldY * scale;
            double noise = perlin.fbm(noiseX, noiseY, octaves, persistence);
            float gray = (float) noise;

            // 颜色插值
            Color color = new Color();
            color.set(color1).lerp(color2, gray);
            color.a = opacity;

            // 绘制矩形
            Draw.color(color);
            Fill.rect(worldX, worldY, cellSize, cellSize);
        }

        // 绘制连续噪声纹理（更平滑）
        public void drawContinuousNoise() {
            if (!active) return;

            // 获取屏幕边界
            float camX = camera.position.x;
            float camY = camera.position.y;
            float camWidth = camera.width;
            float camHeight = camera.height;

            float startX = camX - camWidth / 2;
            float endX = camX + camWidth / 2;
            float startY = camY - camHeight / 2;
            float endY = camY + camHeight / 2;

            int samples = 100;  // 采样点数量

            Draw.draw(Draw.z(), () -> {
                for (int i = 0; i < samples; i++) {
                    float progressY = (float)i / samples;
                    float y = Mathf.lerp(startY, endY, progressY);

                    float lastX = startX;
                    float lastNoise = getNoiseAt(lastX, y);

                    for (int j = 1; j <= samples; j++) {
                        float progressX = (float)j / samples;
                        float x = Mathf.lerp(startX, endX, progressX);

                        float currentNoise = getNoiseAt(x, y);
                        float gray = Mathf.lerp((float)lastNoise, (float)currentNoise, 0.5f);

                        // 绘制垂直线条
                        Color color = new Color();
                        color.set(color1).lerp(color2, gray);
                        color.a = opacity;

                        Draw.color(color);
                        float height = cellSize * 0.5f;
                        Fill.rect(x, y, cellSize * 0.5f, height);

                        lastX = x;
                        lastNoise = currentNoise;
                    }
                }
            });
        }

        // 获取世界坐标的噪声值
        public float getNoiseAt(float worldX, float worldY) {
            float noiseX = worldX * scale;
            float noiseY = worldY * scale;
            return (float)perlin.fbm(noiseX, noiseY, octaves, persistence);
        }

        // 绘制到指定位置
        public void drawNoiseAt(float centerX, float centerY, float width, float height) {
            if (!active) return;

            float startX = centerX - width / 2;
            float endX = centerX + width / 2;
            float startY = centerY - height / 2;
            float endY = centerY + height / 2;

            int xSamples = (int)(width / cellSize);
            int ySamples = (int)(height / cellSize);

            Draw.draw(Draw.z(), () -> {
                for (int y = 0; y < ySamples; y++) {
                    float posY = Mathf.lerp(startY, endY, (float)y / ySamples);
                    for (int x = 0; x < xSamples; x++) {
                        float posX = Mathf.lerp(startX, endX, (float)x / xSamples);
                        drawNoiseCell(posX, posY);
                    }
                }
            });
        }

        // 绘制柏林噪声地形高度图
        public void drawHeightMap() {
            if (!active) return;

            float camX = camera.position.x;
            float camY = camera.position.y;
            float camWidth = camera.width;
            float camHeight = camera.height;

            float startX = camX - camWidth / 2;
            float endX = camX + camWidth / 2;
            float startY = camY - camHeight / 2;
            float endY = camY + camHeight / 2;

            int segments = 50;
            float segmentWidth = (endX - startX) / segments;
            float segmentHeight = (endY - startY) / segments;

            Draw.draw(Draw.z(), () -> {
                for (int i = 0; i < segments; i++) {
                    float x = startX + i * segmentWidth;
                    for (int j = 0; j < segments; j++) {
                        float y = startY + j * segmentHeight;

                        // 计算四个角的噪声值
                        float n1 = getNoiseAt(x, y);
                        float n2 = getNoiseAt(x + segmentWidth, y);
                        float n3 = getNoiseAt(x, y + segmentHeight);
                        float n4 = getNoiseAt(x + segmentWidth, y + segmentHeight);

                        // 插值
                        float noise = (n1 + n2 + n3 + n4) / 4f;
                        Color color = new Color();
                        color.set(color1).lerp(color2, noise);
                        color.a = opacity;

                        Draw.color(color);
                        Fill.rect(x + segmentWidth/2, y + segmentHeight/2, segmentWidth, segmentHeight);
                    }
                }
            });
        }

        // 控制方法
        public void setActive(boolean active) {
            this.active = active;
        }

        public void toggleActive() {
            this.active = !this.active;
        }

        public void setColors(Color color1, Color color2) {
            this.color1 = color1;
            this.color2 = color2;
        }

        public void setCellSize(float cellSize) {
            this.cellSize = Mathf.clamp(cellSize, 1f, 100f);
        }

        public void setOpacity(float opacity) {
            this.opacity = Mathf.clamp(opacity, 0f, 1f);
        }

        public void setParameters(int newSeed, float newScale, int newOctaves, double newPersistence) {
            this.seed = newSeed;
            this.scale = newScale;
            this.octaves = newOctaves;
            this.persistence = newPersistence;
            this.perlin = new OEPerlinNoise(newSeed);
        }

        public boolean isActive() {
            return active;
        }
    }
    public static class PerlinNoiseElement extends Element {
        private OENoise.OEPerlinNoise perlin;
        private int seed = 12345;
        private int width = 100, height = 100;
        private double[][] noiseMap;
        private float scale = 0.1f;
        private int octaves = 6;
        private double persistence = 0.5;

        public PerlinNoiseElement(int seed, int width, int height, float scale, int octaves, double persistence) {
            this.seed = seed;
            this.width = width;
            this.height = height;
            this.scale = scale;
            this.octaves = octaves;
            this.persistence = persistence;
            this.perlin = new OENoise.OEPerlinNoise(seed);
            generateNoiseMap();
        }

        private void generateNoiseMap() {
            noiseMap = new double[width][height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    noiseMap[x][y] = perlin.fbm(x * scale, y * scale, octaves, persistence);
                }
            }
        }

        @Override
        public void draw() {
            float pixelWidth = getWidth() / width;
            float pixelHeight = getHeight() / height;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double noiseValue = noiseMap[x][y];

                    float gray = (float) noiseValue;

                    Color color = new arc.graphics.Color(gray, gray, gray, 1f);

                    float drawX = this.getX(0) + x * pixelWidth;
                    float drawY = this.getY(0) + (height - y - 1) * pixelHeight;  // 翻转Y轴

                    Draw.color(color);
                    Fill.crect(drawX, drawY, pixelWidth, pixelHeight);
                }
            }
            Draw.reset();
        }

        public void regenerate(int newSeed, float newScale, int newOctaves, double newPersistence) {
            this.seed = newSeed;
            this.scale = newScale;
            this.octaves = newOctaves;
            this.persistence = newPersistence;
            this.perlin = new OENoise.OEPerlinNoise(newSeed);
            generateNoiseMap();
        }
    }

    public static class OEPerlinNoise {
        private static final int P_SIZE = 512;
        private final int[] p = new int[P_SIZE];

        public OEPerlinNoise(long seed) {
            int[] permutation = new int[256];
            java.util.Random rand = new java.util.Random(seed);
            for (int i = 0; i < 256; i++) permutation[i] = i;
            for (int i = 255; i > 0; i--) {
                int j = rand.nextInt(i + 1);
                int swap = permutation[i];
                permutation[i] = permutation[j];
                permutation[j] = swap;
            }
            for (int i = 0; i < 256; i++) p[256 + i] = p[i] = permutation[i];
        }

        private static double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private static double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private static double grad(int hash, double x, double y) {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : (h == 12 || h == 14 ? x : 0);
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }

        public double noise(double x, double y) {
            int X = (int) Math.floor(x) & 255;
            int Y = (int) Math.floor(y) & 255;

            x -= Math.floor(x);
            y -= Math.floor(y);

            double u = fade(x);
            double v = fade(y);

            int aa = p[p[X] + Y];
            int ab = p[p[X] + Y + 1];
            int ba = p[p[X + 1] + Y];
            int bb = p[p[X + 1] + Y + 1];

            double x1 = lerp(u, grad(aa, x, y), grad(ba, x - 1, y));
            double x2 = lerp(u, grad(ab, x, y - 1), grad(bb, x - 1, y - 1));
            return lerp(v, x1, x2);
        }

        public double fbm(double x, double y, int octaves, double persistence) {
            double total = 0;
            double freq = 1, amp = 1, max = 0;
            for (int i = 0; i < octaves; i++) {
                total += noise(x * freq, y * freq) * amp;
                max += amp;
                amp *= persistence;
                freq *= 2;
            }
            return (total / max + 1) / 2;
        }
    }
}