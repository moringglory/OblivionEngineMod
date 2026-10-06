package oblivionengine.expand.Blocks;

import arc.graphics.g2d.Draw;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import oblivionengine.content.OEBlocks;
import oblivionengine.expand.Payload.OEPayloadBlock;
import oblivionengine.expand.ui.OEUITools;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

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
        boolean canPlace = true;

        int tx,ty;
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

            for (int i = 0; i < 4; i++) {
                Tile tile = world.tile(tx + dx[i], ty + dy[i]);
                if (tile == null || tile.build == null) continue;
                if (world.tile(tx + dx3[i], ty + dy3[i]).block() != Blocks.air) {
                    canPlace = false;
                    continue;
                }
                if (tile.build.tileX() == tx + dx[i] && tile.build.tileY() == ty + dy[i] && tile.build.block == OEBlocks.crashingWheel) {
                    if (dx[i] == 0) world.tile(tx + dx3[i], ty + dy3[i]).setBlock(OEBlocks.rockcrusher, team, 2);
                    if (dy[i] == 0) world.tile(tx + dx3[i], ty + dy3[i]).setBlock(OEBlocks.rockcrusher, team, 1);
                    canPlace = true;
                }
            }
        }

        @Override
        public void onRemoved() {
            super.onRemoved();

            for (int i = 0; i < 4; i++) {
                Tile t = Vars.world.tile(tx + dx3[i], ty + dy3[i]);
                if (canPlace && Vars.world.tile(tx + dx[i], ty + dy[i]).block() == OEBlocks.crashingWheel && t.block() == OEBlocks.rockcrusher && t.build.team == this.team) {
                    t.setBlock(Blocks.air);
                }
            }
        }


        @Override
        public void update() {
            super.update();

            if(!canPlace) {
                for (int i = 0; i < 4; i++) {
                    Tile tile = world.tile(tx + dx[i], ty + dy[i]);
                    if (tile == null || tile.build == null) continue;
                    if (world.tile(tx + dx3[i], ty + dy3[i]).block() != Blocks.air) {
                        canPlace = false;
                        continue;
                    }
                    if (tile.build.tileX() == tx + dx[i] && tile.build.tileY() == ty + dy[i] && tile.build.block == OEBlocks.crashingWheel) {
                        if (dx[i] == 0) world.tile(tx + dx3[i], ty + dy3[i]).setBlock(OEBlocks.rockcrusher, team, 2);
                        if (dy[i] == 0) world.tile(tx + dx3[i], ty + dy3[i]).setBlock(OEBlocks.rockcrusher, team, 1);
                        canPlace = true;
                    }
                }
            }
        }

        @Override
        public void draw() {
            Draw.z(Layer.block + 1f);
            Draw.rect(region, x, y, rotdeg());

            Draw.reset();
        }
    }
}
