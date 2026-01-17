package OblivionEngine.expand.Blocks;

import OblivionEngine.content.core.OEBlock;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

public class OEExcavator extends PayloadBlock {

    private final Block POLY_TYPE = OEBlock.stone_materiala;//Vars.content.getByName(ContentType.block, "stone_materiala");

    public OEExcavator(String name){
        super(name);

        size = 3;
        update = true;
        outputsPayload = true;
        hasPower = false;
        rotate = true;
        configurable = true;
        selectionRows = selectionColumns = 8;
        clipSize = 120;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        regionRotated1 = 1;
        acceptsUnitPayloads = false;
        commandable = true;

        configClear((PayloadSourceBuild build) -> {
            build.scl = 0f;
        });
    }

    @Override
    public void getPlanConfigs(Seq<UnlockableContent> options){
        options.add(POLY_TYPE);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, outRegion, topRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    public class PayloadSourceBuild extends PayloadBlockBuild<Payload>{
        public @Nullable Vec2 commandPos;
        public float scl;

        @Override
        public Vec2 getCommandPosition(){
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target){
            commandPos = target;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return false;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(payload == null){
                payload = new BuildPayload(POLY_TYPE, team);
                payVector.setZero();
                payRotation = rotdeg();
            }
            scl = Mathf.lerpDelta(scl, 1f, 0.1f);

            moveOutPayload();
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());
            Draw.rect(topRegion, x, y);

            Draw.scl(scl);
            drawPayload();
            Draw.reset();
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            TypeIO.writeVecNullable(write, commandPos);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                commandPos = TypeIO.readVecNullable(read);
            }
        }
    }
}