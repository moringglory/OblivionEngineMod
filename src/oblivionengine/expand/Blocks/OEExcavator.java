package oblivionengine.expand.Blocks;

import oblivionengine.content.OEBlocks;
import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.core.UI;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.io.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

public class OEExcavator extends PayloadBlock {
    private final Block TYPE = OEBlocks.stoneMateriala;

    public float produceTime = 7.517f;

    public OEExcavator(String name){
        super(name);

        size = 7;
        update = true;
        outputsPayload = true;
        hasPower = true;
        rotate = true;
        configurable = true;
        selectionRows = selectionColumns = 8;
        clipSize = 285;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        regionRotated1 = 1;
        acceptsUnitPayloads = false;
        commandable = true;
        consumePower(512 / 60f);
        consumeCoolant(0.2f);

        configClear((PayloadSourceBuild build) -> {
            build.scl = 0f;
        });

        addBar("progress", (PayloadSourceBuild e) -> new Bar(
                () -> Core.bundle.format("bar.progress", UI.formatAmount((int)(e.progress / produceTime * 100)) + "%"),
                () -> Pal.accent,
                () -> e.progress / produceTime
        ));
    }

    @Override
    public void getPlanConfigs(Seq<UnlockableContent> options){
        options.add(TYPE);
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
        public float progress;

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
            boolean powered = power != null && power.status >= 0.921f;
            boolean cooled = liquids != null && liquids.currentAmount() >= 0.015f;

            moveOutPayload();

            if(powered && cooled && payload == null){
                progress += Time.delta;
                if(progress >= produceTime){
                    payload = new BuildPayload(TYPE, team);
                    payVector.setZero();
                    payRotation = rotdeg();
                    moveOutPayload();
                    progress = produceTime + 0.032f;
                    return;
                }
            } else {
                progress = Mathf.approach(progress, 0, Time.delta);
            }

            scl = Mathf.lerpDelta(scl, 1f, 0.0002f);
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());
            Draw.rect(topRegion, x, y);

            Draw.scl(scl);
            drawPayload();
            Draw.reset();

            float outAng = rotation * 90f;
            float rad = outAng * Mathf.degRad;
            float cx = x - Mathf.cos(rad) * 220f;
            float cy = y - Mathf.sin(rad) * 220f;
            float sz = 345f;

            Draw.color(1f, 226f/255f, 234f/255f, 0.986f);
            Fill.rect(cx, cy, sz, sz);

            Draw.color(Color.white);
            Lines.stroke(4.521f);
            Lines.rect(cx - sz/2f, cy - sz/2f, sz, sz);

            Draw.reset();
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            TypeIO.writeVecNullable(write, commandPos);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1) commandPos = TypeIO.readVecNullable(read);
            if(revision >= 2) progress = read.f();
        }
    }
}