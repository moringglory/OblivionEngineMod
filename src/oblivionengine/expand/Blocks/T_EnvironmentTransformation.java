package oblivionengine.expand.Blocks;

import oblivionengine.content.OENoise;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.Color;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import arc.struct.*;

public class T_EnvironmentTransformation extends Block {
    public T_EnvironmentTransformation(String name) {
        super(name);
        configurable = false;
        destructible = true;
        solid = true;
        update = true;
        flags = EnumSet.of(BlockFlag.factory);
        size = 2;
    }

    public class Build extends Building {
        private OENoise.PerlinNoiseWorldRenderer noiseRenderer = null;
        private boolean noiseActive = false;
        private float noiseScale = 0.01f;
        private int noiseSeed = 12345;

        @Override
        public void created() {
            super.created();

            // 初始化柏林噪声渲染器
            noiseRenderer = new OENoise.PerlinNoiseWorldRenderer(
                    noiseSeed, noiseScale, 6, 0.8
            );
        }

        @Override
        public void draw() {
            super.draw();

//            TextureRegion image = Core.atlas.find("oblivion-engine-heqing");
//            Draw.rect(image, this.x, this.y);

//            if (noiseActive && noiseRenderer != null) {
                drawNoisePixels();
//            }
        }

        // 绘制柏林噪声像素
        private void drawNoisePixels() {
            int gridSize = 200;  // 网格大小
            float cellSize = 4f;  // 每个单元格的大小

            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    // 计算世界坐标
                    float worldX = this.x + (x - gridSize/2) * cellSize;
                    float worldY = this.y + (y - gridSize/2) * cellSize;

                    // 计算噪声值
                    float noise = (float)noiseRenderer.getNoiseAt(worldX, worldY);

                    // 根据噪声值选择颜色
                    Color color = new Color(noise, noise, noise, 0.8f);

                    // 绘制像素
                    Draw.color(color);
                    Fill.rect(worldX, worldY, cellSize, cellSize);
                }
            }
            Draw.reset();
        }

        // 切换噪声显示
        public void toggleNoise() {
            noiseActive = !noiseActive;
        }
    }
}