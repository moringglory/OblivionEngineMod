//package oblivionengine.expand.Payload;
//
//import mindustry.ctype.UnlockableContent;
//import mindustry.entities.units.UnitController;
//import mindustry.gen.Unit;
//import mindustry.gen.Unitc;
//import mindustry.logic.Senseable;
//import mindustry.type.UnitType;
//
//public class OEPayloadItems extends UnitType {
//
//    /**
//     * 构造一个基础的 Payload 单位模板。
//     * @param name 单位名称，如 "cargo-pod"、"supply-box"
//     */
//    public OEPayloadItems(String name) {
//        super(name);
//
//        // === 默认设置为“载荷” ===
//        this.allowedInPayloads = true; // 可被其他单位携带
//
//        // === 移除所有主动功能 ===
//        this.weapons.clear();          // 无武器
//        this.abilities.clear();        // 无技能/能力
//        this.commands.clear();         // 无命令
//        this.stances.clear();          // 无行为姿态
//
//        // === 基础运动与交互禁用 ===
//        //this.f;               // 不能自主移动
//        this.flying = false;           // 默认不飞行
//        this.targetable = false;       // 默认不可被目标锁定（可选，根据需求）
//        this.hittable = false;         // 默认不可被击中（可选）
//        this.killable = true;          // 可被销毁（可根据需求调整）
//        this.canAttack = false;        // 不能攻击
//        this.canDrown = false;         // 不受水/液体影响（可选）
//        this.mineTier = -1;            // 不能挖矿
//        this.buildSpeed = -1f;         // 不能建造
//        this.itemCapacity = 0;         // 无物品栏
//        this.ammoCapacity = 0;         // 无弹药
//
//        // === 基础外观和结构 ===
//        this.legCount = 0;             // 无腿
//        //this.clear();          // 无引擎/推进器
//        this.parts.clear();            // 无附加部件
//
//        // === AI 行为：默认无 AI 或空 AI（什么都不做）===
////        this.aiController = () -> new UnitController() {
////            @Override
////            public void update(Unit unit) {
////                // 空实现：什么都不做
////            }
////        };
//
//        // === 构造器：返回一个什么都不做的单位实例 ===
////        this.constructor = () -> new Unitc() {
////            @Override
////            public void update() {
////                // 空实现：载荷什么都不做
////            }
////        };
//
//        // === 可选：设置一个极小的血量 ===
//        this.health = 1f;
//
//        // === 默认不生成复杂内容，如音效、光效、特效等 ===
//        this.deathSound = null;
//        this.loopSound = null;
//        this.shieldColor = null;
//        this.lightRadius = -1f;
//    }
//
//    /**
//     * 可选：重写 init() 方法以允许子类扩展时做一些初始化
//     */
////    @Override
////    public void init() {
////        super.init();
////
////        // 你可以在这里添加默认的初始化逻辑，比如设置图标、描述等
////        // 或者留空，让具体子类去设置
////    }
//
//    /**
//     * 可选：提供一个快速创建“纯载荷”的静态方法或工厂方法
//     */
//    public static OEPayloadItems createBasicPayload(String name) {
//        return new OEPayloadItems(name);
//    }
//}
