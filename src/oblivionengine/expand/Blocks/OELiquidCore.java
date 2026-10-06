package oblivionengine.expand.Blocks;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.LaunchAnimator;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import mindustry.content.Fx;
import mindustry.world.modules.LiquidModule;

import static mindustry.Vars.*;

public class OELiquidCore extends CoreBlock{
    // ✅ 添加公共字段，用于存储配置的液体容量
    public float liquidCapacity = 150f; // 默认容量

    public OELiquidCore(String name){
        super(name);

        size = 4;
        health = 480 * size;
        solid = true;
        noUpdateDisabled = true;
        canOverdrive = false;
        floating = true;
        update = true;
        hasLiquids = true;      // ✅ 有液体模块
        outputsLiquid = true;   // 允许输出
        envEnabled |= Env.space | Env.underwater;
    }

    public class OELiquidCoreBuild extends CoreBuild implements LaunchAnimator {
        public int storageCapacityliquid;
        public ObjectMap<Team, Float> teamLiquidTotals = new ObjectMap<>(); // 静态变量存储团队总液体量

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            // ✅ 1. 物品同步（原有逻辑）
            for(Building other : state.teams.cores(team)){
                if(other.tile != tile){
                    this.items = other.items;
                }
            }
            state.teams.registerCore(this);

            // ✅ 2. 液体容量计算
            storageCapacityliquid = (int) liquidCapacity;

            // 计算相连方块的液体容量
            for(Building e : proximity){
                if(owns(e) && e.block.hasLiquids){
                    storageCapacityliquid += (int)e.block.liquidCapacity;
                }
            }

            // 计算其他核心的液体容量
            for(CoreBuild core : state.teams.cores(team)){
                if(core == this) continue;
                if(core.block.hasLiquids){
                    storageCapacityliquid += (int)core.block.liquidCapacity;
                }
            }

            // ✅ 3. 液体容器同步（关键！）
            syncLiquidsWithTeam();

            // ✅ 4. 同步液体总量到静态变量
            updateTeamLiquidTotal();
        }

        /** 同步液体容器（模仿物品同步逻辑） */
        private void syncLiquidsWithTeam(){
            // 查找团队中已有的 OELiquidCoreBuild
            for(CoreBuild core : state.teams.cores(team)){
                if(core != this && core instanceof OELiquidCoreBuild otherLiquidCore){
                    // 如果对方有液体容器，就同步引用
                    if(otherLiquidCore.liquids != null){
                        this.liquids = otherLiquidCore.liquids;
                        return; // 找到第一个就返回
                    }
                }
            }

            // 如果没找到现有的液体容器，自己创建一个
            if(liquids == null){
                liquids = new LiquidModule();
            }
        }

        /** 更新团队液体总量统计 */
        private void updateTeamLiquidTotal(){
            float total = 0f;

            // 统计所有 OELiquidCore 的液体总量
            for(CoreBuild core : state.teams.cores(team)){
                if(core instanceof OELiquidCoreBuild liquidCore && liquidCore.liquids != null){
                    total += liquidCore.liquids.currentAmount();
                }
            }

            // ✅ 关键：将计算结果存入静态映射
            teamLiquidTotals.put(team, total);

            // 可选：调试输出
            if(Mathf.chance(0.1f)){ // 极低概率输出，避免刷屏
                Log.info("[OELiquidCore] 团队 " + team + " 液体总量: " + total);
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();

            // 确保 liquids 不为 null
            if(liquids == null){
                liquids = new LiquidModule();
            }

            float currentTotalCapacity = storageCapacityliquid;

            // ✅ 修复：添加非空检查
            if(team.data().core() != null && team.data().core().liquids != null){
                // ✅ 从资源栏补充液体到缓冲区（供输出）
                if(liquids.currentAmount() < 125f){
                    Liquid liq = liquids.current();
                    if(liq != null){
                        float need = currentTotalCapacity - liquids.currentAmount();
                        float taken = team.data().core().liquids.get(liq);
                        if(taken > 1333f){
                            float transfer = Math.min(need, taken);
                            team.data().core().liquids.remove(liq, transfer);
                            liquids.add(liq, transfer);
                        }
                    }
                }
            }

            // ✅ 自动输出到相邻方块
            dumpLiquid(liquids.current());
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            // 1. 检查容量
            if(liquids.currentAmount() >= storageCapacityliquid){
                return false;
            }

            // 2. 防止液体混合
            if(liquids.current() != null && liquids.current() != liquid){
                return false;
            }

            return true;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            if(!acceptLiquid(source, liquid)) return;

            // 计算剩余容量
            float maxAccept = storageCapacityliquid - liquids.currentAmount();
            float accepted = Math.min(amount, maxAccept);

            if(accepted > 0){
                // 添加液体
                liquids.add(liquid, accepted);

                // 添加效果反馈
                if(accepted >= 1f){
                    Fx.fireHit.at(source.x, source.y, accepted, liquid.color, this);
                }

                // 更新团队液体统计
                updateTeamLiquidTotal();
            }
        }
    }
}