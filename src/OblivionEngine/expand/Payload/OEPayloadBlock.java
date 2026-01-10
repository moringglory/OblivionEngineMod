package OblivionEngine.expand.Payload;

import arc.struct.EnumSet;
import mindustry.gen.Sounds;
import mindustry.world.Block;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BuildVisibility;

public class OEPayloadBlock extends Block {
    public OEPayloadBlock(String name) {
        super(name);

        destructible = true;
        canOverdrive = false;
        solid = true;
        update = true;
        canPickup=true;
        sync = true;
    }
}
