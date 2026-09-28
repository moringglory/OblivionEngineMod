package oblivionengine.expand.Blocks;

import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import oblivionengine.content.core.OEBlocks;
import oblivionengine.expand.ui.OEUITools;

import static mindustry.Vars.tilesize;

public class OECrashingWheel extends OEParts {
    public float range = 6f;

    public OECrashingWheel(String name) {
        super(name);

        allowRectanglePlacement = true;
        swapDiagonalPlacement = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.circles(x * tilesize + offset, y * tilesize + offset, range * tilesize);
    }

    public class OECrashingWhellBuild extends OEPartsBuilding {


        @Override
        public void placed() {
            super.placed();

            int cx = tileX();
            int cy = tileY();
            int[] dx = {6, 0, -6, 0};
            int[] dy = {0, 6, 0, -6};
            int[] dx3 = {3, 0, -3, 0};
            int[] dy3 = {0, 3, 0, -3};

            for (int i = 0; i < 4; i++) {
                Tile tile = Vars.world.tile(cx + dx[i], cy + dy[i]);
                if (tile == null || tile.build == null) continue;

                if (tile.build.block == OEBlocks.crashing_wheel) {
//                    OEUITools.PrintOEInformation("found crashing-wheel");
                    if (dx[i] == 0) Vars.world.tile(cx + dx3[i], cy + dy3[i]).setBlock(OEBlocks.rockcrusher, team, 2);
                    if (dy[i] == 0) Vars.world.tile(cx + dx3[i], cy + dy3[i]).setBlock(OEBlocks.rockcrusher, team, 1);
                }
            }
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();

            int tx = tileX();
            int ty = tileY();
            int[] dx = {6, 0, -6, 0};
            int[] dy = {0, 6, 0, -6};
            int[] dx3 = {3, 0, -3, 0};
            int[] dy3 = {0, 3, 0, -3};

            for (int i = 0; i < 4; i++) {
                Tile t = Vars.world.tile(tx + dx[i], ty + dy[i]);
                if (t == null || t.build == null) continue;

                if (t.build.block == OEBlocks.crashing_wheel) {
                    OEUITools.PrintOEInformation("found crashing wheel");
                    Vars.world.tile(tx + dx3[i], ty + dy3[i]).setBlock(Blocks.air);
                }
            }
        }


        @Override
        public void update() {
            super.update();

        }

        @Override
        public void draw() {
            Draw.z(Layer.block + 0.1f);
            Draw.rect(region, x, y, rotdeg());

            Draw.reset();
        }
    }
}
