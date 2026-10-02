package oblivionengine.expand.Blocks;

import arc.Events;
import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
 import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import oblivionengine.content.core.OEBlocks;
import oblivionengine.content.core.OEUnits;

import java.util.Objects;

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
        public float progress, time;
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

        @Override
        public void handlePayload(Building source, Payload payload) {
            this.payload = payload;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            progress = 121;//test
            if(payload != null) {
                if(payload.content() != OEBlocks.stoneMateriala) {
                    moveOutPayload();
                } else {
                    if(moveInPayload()) {
                        if(efficiency > 0){
//                            Events.fire(new EventType.UnitCreateEvent(payload., this));
                        }
                        if(progress >= constructTime){
                            payload = null;
                            payload = new UnitPayload(OEUnits.depleted_uranium.create(team));
                            createSound.at(this, 1f + Mathf.range(0.06f), createSoundVolume);
                            progress %= 1f;
                            Effect.shake(2f, 3f, this);
                            Fx.producesmoke.at(this);
                            consume();
                        }
                    }
                }
            }
        }
    }
}
