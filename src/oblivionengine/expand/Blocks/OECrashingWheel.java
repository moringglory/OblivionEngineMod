package oblivionengine.expand.Blocks;

import arc.graphics.g2d.Draw;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import oblivionengine.content.OEBlocks;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class OECrashingWheel extends OEParts {
    public OECrashingWheel(String name) {
        super(name);

        allowRectanglePlacement = true;
        swapDiagonalPlacement = true;
    }

    public class OECrashingWhellBuild extends OEPartsBuilding {
        int[] errTile;
        boolean directionClear = true;

        int tx, ty;
        int[] dx = {6, 0, -6, 0};
        int[] dy = {0, 6, 0, -6};
        int[] dx3 = {3, 0, -3, 0};
        int[] dy3 = {0, 3, 0, -3};

        @Override
        public void created() {
            tx = tileX();
            ty = tileY();
        }

        @Override
        public void placed() {
            super.placed();
            refresh();
        }

        @Override
        public void update() {
            super.update();
            refresh();
        }

        private void refresh() {
            tx = tileX();
            ty = tileY();
            errTile = null;

            for (int i = 0; i < 4; i++) {
                Tile tile = world.tile(tx + dx[i], ty + dy[i]);
                if (tile == null || tile.build == null) continue;

                Tile middle = world.tile(tx + dx3[i], ty + dy3[i]);
                if (middle == null) continue;

                for (int ox = -1; ox <= 1; ox++) {
                    for (int oy = -1; oy <= 1; oy++) {
                        Tile t = world.tile(middle.x + ox, middle.y + oy);
                        if (t == null || (t.block() != Blocks.air && t.block() != OEBlocks.rockcrusher)) {
                            errTile = new int[]{tx + dx3[i] + ox, ty + dy3[i] + oy};
                            directionClear = false;
                        }
                    }
                }

                if (!directionClear) continue;

                if (tile.build.tileX() == tx + dx[i]
                        && tile.build.tileY() == ty + dy[i]
                        && tile.build.block == OEBlocks.crashingWheel) {
                    if (dx[i] == 0 && middle.block() != OEBlocks.rockcrusher) {
                        middle.setBlock(OEBlocks.rockcrusher, team, 2);
                    }
                    if (dy[i] == 0 && middle.block() != OEBlocks.rockcrusher) {
                        middle.setBlock(OEBlocks.rockcrusher, team, 1);
                    }
                    directionClear = true;
                }
            }
        }

        @Override
        public void onRemoved() {
            super.onRemoved();

            for (int i = 0; i < 4; i++) {
                Tile neighbor = Vars.world.tile(tx + dx[i], ty + dy[i]);
                Tile t = Vars.world.tile(tx + dx3[i], ty + dy3[i]);
                if (neighbor == null || t == null || t.build == null) continue;
                if (neighbor.block() == OEBlocks.crashingWheel && t.block() == OEBlocks.rockcrusher && t.build.team == this.team) {
                    t.setBlock(Blocks.air);
                }
            }
        }

        @Override
        public void draw() {
            if (errTile != null && directionClear) {
                Draw.z(Layer.block + 50f);
                Drawf.square(errTile[0] * tilesize, errTile[1] * tilesize, 14f, 0f);
            }
            Draw.z(Layer.block + 1f);
            Draw.rect(region, x, y, rotdeg());

            Draw.reset();
        }
    }
}