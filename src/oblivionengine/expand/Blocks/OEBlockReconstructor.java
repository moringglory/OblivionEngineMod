package oblivionengine.expand.Blocks;

import arc.audio.Sound;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
 import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import oblivionengine.content.OEBlocks;
import oblivionengine.content.OEUnits;

public class OEBlockReconstructor extends PayloadBlock {
    public float constructTime = 60 * 2;
    public Sound createSound = Sounds.unitCreate;
    public float createSoundVolume = 1f;

    public OEBlockReconstructor(String name) {
        super(name);

        size = 3;
        update = true;
        outputsPayload = true;
        hasItems = true;
        solid = true;
        hasPower = true;
        acceptsUnitPayloads = false;
        canPickup = false;
        rotate = true;
    }

    @Override
    public boolean canBreak(Tile tile) {
        return breakable;
    }

    public class OEBlockReconstructorBuild extends PayloadBlockBuild {
        boolean vertical = false;
        private int lastRot = -1;

        @Override
        public void created() {
            super.created();

            vertical = (rotation == 1 || rotation == 3);
        }

        @Override
        public void update() {
            super.update();

            if (lastRot == -1) lastRot = rotation;
            if (rotation != lastRot) {
                rotation = vertical ? (lastRot == 1 ? 3 : 1) : (lastRot == 0 ? 2 : 0);
            }
            lastRot = rotation;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            if (this.payload != null) return false;
            return payload.content() == OEBlocks.stoneMateriala;
        }
    }
}
