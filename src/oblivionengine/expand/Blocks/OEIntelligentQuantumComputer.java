package oblivionengine.expand.Blocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import arc.struct.*;

import static mindustry.Vars.*;

public class OEIntelligentQuantumComputer extends Block {
    // 护盾核心参数
    public float shieldHealth = 900f;
    public float breakCooldown = 60f * 10f;
    public float regenSpeed = 2f;

    // 护盾视觉效果参数
    public Color glowColor = Color.valueOf("ff7531").a(0.5f);
    public float glowMag = 0.6f, glowScl = 8f;

    // 护盾与方块的距离偏移
    public float shieldOffset = 60f;

    public OEIntelligentQuantumComputer(String name) {
        super(name);
        configurable = false;
        destructible = true;
        solid = true;
        update = true;
        flags = EnumSet.of(BlockFlag.factory);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.shieldHealth, shieldHealth);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("shield", (Booting entity) ->
                new Bar("stat.shieldhealth", Pal.accent,
                        () -> entity.broken() ? 0f : entity.shield / shieldHealth
                ).blink(Color.white)
        );
    }

    public class Booting extends Building {
        // 护盾核心变量
        public float shield = shieldHealth;
        public float shieldRadius = 1f;
        public float breakTimer = 0f;
        public float hit = 0f;

        // 护盾破裂判定
        public boolean broken() {
            return breakTimer > 0f || !canConsume();
        }

        @Override
        public void updateTile() {
            super.updateTile();

            // 护盾恢复逻辑
            if (breakTimer > 0f) {
                breakTimer -= Time.delta;
            } else if (!broken()) {
                shield = Mathf.clamp(shield + regenSpeed * edelta(), 0f, shieldHealth);
            }

            // 受击闪烁衰减
            if (hit > 0f) {
                hit -= Time.delta / 10f;
                hit = Math.max(hit, 0f);
            }

            // 护盾半径动画
            shieldRadius = Mathf.lerpDelta(shieldRadius, broken() ? 0f : 1f, 0.12f);

            // 主动检测并拦截子弹
            deflectBullets();
        }

        /** 检测并拦截子弹 */
        public void deflectBullets() {
            if (broken() || shield <= 0f) return;

            float detectionRadius = getShieldRadius() + 10f;

            Groups.bullet.intersect(x - detectionRadius, y - detectionRadius,
                    detectionRadius * 2f, detectionRadius * 2f,
                    bullet -> {
                        if (bullet.team != team && bullet.type.absorbable && !bullet.absorbed) {
                            if (isBulletInShield(bullet)) {
                                handleBulletHit(bullet);
                            }
                        }
                    });
        }

        /** 判断子弹是否在护盾内 */
        public boolean isBulletInShield(Bullet bullet) {
            float dist = Mathf.dst(bullet.x, bullet.y, x, y);
            return dist <= getShieldRadius();
        }

        /** 获取当前护盾半径 */
        public float getShieldRadius() {
            float baseRadius = (tilesize * size) / 2f;
            float shieldWidth = 8f;
            float innerRadius = baseRadius + shieldOffset;
            float outerRadius = innerRadius + shieldWidth;
            return innerRadius + (outerRadius - innerRadius) * shieldRadius;
        }

        /** 处理子弹命中护盾 */
        public void handleBulletHit(Bullet bullet) {
            float shieldDamage = bullet.type.shieldDamage(bullet);

            // 吸收子弹
            bullet.absorb();

            // 护盾扣血
            shield -= shieldDamage;
            hit = 1f;

            // 显示吸收效果
            Fx.absorb.at(bullet);

            // 检查护盾是否破裂
            if (shield <= 0f) {
                shield = 0f;
                breakTimer = breakCooldown;
                createCircleShieldBreakEffect();
            }
        }

        /** 创建圆形护盾破碎特效 */
        public void createCircleShieldBreakEffect() {
            float radius = getShieldRadius();

            // 圆形冲击波
            Fx.shockwave.at(x, y, radius / 8f, team.color);

            // 圆形粒子爆炸
            for(int i = 0; i < 16; i++){
                float angle = i * 22.5f;
                float len = radius * Mathf.random(0.8f, 1.2f);
                float px = x + Angles.trnsx(angle, len);
                float py = y + Angles.trnsy(angle, len);
                Fx.smoke.at(px, py, team.color);
            }

            // 声音效果
            Sounds.shoot.at(x, y, Mathf.random(0.9f, 1.1f));
        }

        /** 重写子弹碰撞方法 */
        @Override
        public boolean collision(Bullet bullet) {
            // 如果有护盾存在，子弹只能打到护盾
            if (!broken() && shield > 0f) {
                float dist = Mathf.dst(bullet.x, bullet.y, x, y);
                float shieldRadius = getShieldRadius();
                float buildingRadius = (tilesize * size) / 2f;

                // 修复：子弹从任何方向接近都要被护盾拦截
                if (dist <= shieldRadius) {
                    // 子弹在护盾范围内，由护盾处理
                    handleBulletHit(bullet);
                    return true;
                }

                // 额外检测：如果子弹在方块内部（从内部发射）
                // 也需要被护盾拦截
                if (dist < buildingRadius && bullet.team != team) {
                    handleBulletHit(bullet);
                    return true;
                }
            }

            // 护盾不存在或子弹在护盾范围外，正常碰撞
            return super.collision(bullet);
        }

        @Override
        public void draw() {
            super.draw();
            Draw.rect(block.region, x, y);

            // 绘制外围护盾
            if (shieldRadius > 0f && !broken()) {
                float radius = getShieldRadius();

                Draw.z(Layer.shields);
                Draw.color(team.color, Color.white, Mathf.clamp(hit));

                if (renderer.animateShields) {
                    // 动态护盾：绘制空心圆环
                    Lines.stroke(4f * shieldRadius);
                    Lines.circle(x, y, radius - 2f);
                } else {
                    // 静态护盾：绘制双层圆环
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.circle(x, y, radius);
                    Draw.alpha(1f);
                    Lines.circle(x, y, radius);
                    Draw.reset();
                }
                Draw.reset();

                // 绘制发光效果
                TextureRegion glow = Core.atlas.find("oblivion-engine-precursor");
                if (true) {
//                    Log.info("[OE] find textureregion glow");
                    float glowScale = (1f - glowMag + Mathf.absin(glowScl, glowMag)) * shieldRadius;
                    Drawf.additive(glow, glowColor, glowScale, x, y, 0f, Layer.blockAdditive);
                }
            }
        }

        @Override
        public void damage(float damage) {
            // 如果有护盾存在，优先用护盾吸收伤害
            if (!broken() && shield > 0f) {
                float shieldTaken = Math.min(shield, damage);
                shield -= shieldTaken;
                hit = 1f;

                // 护盾被击破时触发冷却
                if (shield <= 0.00001f && shieldTaken > 0f) {
                    shield = 0f;
                    breakTimer = breakCooldown;
                    createCircleShieldBreakEffect();
                }

                // 如果护盾完全吸收伤害，不传递到方块
                if (damage - shieldTaken <= 0f) {
                    return;
                }

                // 剩余伤害传递给方块本体
                super.damage(damage - shieldTaken);
            } else {
                // 没有护盾或护盾已破裂，直接伤害方块
                super.damage(damage);
            }
        }

        // 拾起时重置护盾
        @Override
        public void pickedUp() {
            super.pickedUp();
            shieldRadius = 0f;
            shield = shieldHealth;
            breakTimer = 0f;
        }

        // 序列化
        @Override
        public void write(arc.util.io.Writes write) {
            super.write(write);
            write.f(shield);
            write.f(breakTimer);
        }

        // 反序列化
        @Override
        public void read(arc.util.io.Reads read, byte revision) {
            super.read(read, revision);
            shield = read.f();
            breakTimer = read.f();
            if (shield > 0f) shieldRadius = 1f;
        }
    }
}