package oblivionengine.expand.Blocks;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
 import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import oblivionengine.content.core.OEBlocks;
import oblivionengine.content.core.OEUnits;

public class OEBlockReconstructor extends PayloadBlock {
    public float constructTime = 60 * 2;
    public OEBlockReconstructor(String name) {
        super(name);

        size = 3;
        update = true;
        outputsPayload = true;
        hasItems = true;
        solid = true;
        hasPower = true;
        acceptsUnitPayloads = false;
        rotate = true;
        regionRotated1 = 1;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(inRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, inRegion, outRegion, topRegion};
    }

    public class OEBlockReconstructorBuild extends PayloadBlockBuild {

        boolean constructing;

        public float fraction(){
            return constructTime;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            if (this.payload == null) {
                return true;
            }

            return false;
        }

        @Override
        public void handlePayload(Building source, Payload payload) {
            this.payload = payload;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(payload != null) {
                if(payload.content() != OEBlocks.stone_materiala) {
                    moveOutPayload();
                } else {
                    if(moveInPayload()) {
                        if(efficiency > 0){
                            payload = null;
                            payload = new UnitPayload(OEUnits.depleted_uranium.create(team));

                        }
                    }
                }
            }
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            //draw input
            boolean fallback = true;
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                    fallback = false;
                }
            }
            if(fallback) Draw.rect(inRegion, x, y, rotation * 90);

            Draw.rect(outRegion, x, y, rotdeg());

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
        }
    }
}
