package oblivionengine.expand.Payload;

import mindustry.world.Block;

public class OEPayloadBlock extends Block {
    public OEPayloadBlock(String name) {
        super(name);

        destructible = true;
        canOverdrive=true;
        solid = true;
        update = true;
        canPickup=true;
        sync = true;
    }
}
