package oblivionengine.expand.Blocks;

import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

public class OEParts extends Block {
    public OEParts(String name) {
        super(name);

        destructible = true;
        canOverdrive=true;
        solid = true;
        canPickup=true;
        hideDatabase = false;
        databaseCategory = "parts";
        alwaysUnlocked=false;
        breakable = true;
        update = true;
        sync = true;
    }

    @Override
    public boolean canBreak(Tile tile) {
        return breakable;
    }

    public class OEPartsBuilding extends Building {

    }
}
