package OblivionEngine.content;

import arc.scene.ui.layout.Table;

public class OENoise {
    public static class PerlinNoiseElement extends Table {
        private OENoise.OEPerlinNoise perlin;
        private int seed = 12345;
        private int width = 100, height = 100;
        private double[][] noiseMap;
        private float scale = 0.1f;
        private int octaves = 6;
        private double persistence = 0.5;

        public PerlinNoiseElement() {
            this.perlin = new OENoise.OEPerlinNoise(seed);
            generateNoiseMap();
        }

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

                    arc.graphics.Color color = new arc.graphics.Color(gray, gray, gray, 1f);

                    float drawX = this.getX(0) + x * pixelWidth;
                    float drawY = this.getY(0) + (height - y - 1) * pixelHeight;  // 翻转Y轴

                    arc.graphics.g2d.Draw.color(color);
                    arc.graphics.g2d.Fill.crect(drawX, drawY, pixelWidth, pixelHeight);
                }
            }
            arc.graphics.g2d.Draw.reset();
        }

        public void regenerate() {
            generateNoiseMap();
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