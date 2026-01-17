package OblivionEngine.expand.Blocks;

import OblivionEngine.content.core.OEBlock;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;

/** 固定输出 Poly 单位的 PayloadSource 变体 */
public class OEExcavator extends PayloadBlock {

    // 固定要输出的 Poly 单位类型（确保 Poly 存在于内容系统中）
    private final Block POLY_TYPE = OEBlock.stone_materiala;//Vars.content.getByName(ContentType.block, "stone_materiala");

    public OEExcavator(String name){
        super(name);

        size = 3;
        update = true;
        outputsPayload = true;
        hasPower = false;
        rotate = true;
        configurable = false; // 禁用配置（无需用户选择）
        selectionRows = selectionColumns = 8;
        clipSize = 120;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        regionRotated1 = 1;
        acceptsUnitPayloads = false;
        commandable = true;

        // 移除原有的 Block/UnitType 配置逻辑，改为固定输出 Poly
        // 清空所有配置监听（可选，确保无残留配置）


        // 可选：如果需要保留清除配置的功能（但无实际意义，因为固定输出）
        configClear((PayloadSourceBuild build) -> {
            // 清除操作无效，保持输出 Poly
            build.scl = 0f; // 重置缩放动画
        });
    }

    @Override
    public void getPlanConfigs(Seq<UnlockableContent> options){
        // 计划中仅显示 Poly 单位（可选，根据需求调整）
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
                // 直接生成 Poly 单位的 Payload
                payload = new BuildPayload(POLY_TYPE, team);
                payVector.setZero();
                payRotation = rotdeg();
            }
            scl = Mathf.lerpDelta(scl, 1f, 0.1f); // 缩放动画

            moveOutPayload(); // 输出 Payload 到相邻建筑/输送机
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());
            Draw.rect(topRegion, x, y);

            Draw.scl(scl);
            drawPayload(); // 绘制当前 Payload（Poly 单位）
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