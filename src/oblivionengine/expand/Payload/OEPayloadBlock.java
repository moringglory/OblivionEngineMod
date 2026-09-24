package oblivionengine.expand.Payload;

import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

public class OEPayloadBlock extends Block {
    public OEPayloadBlock(String name) {
        super(name);

        canOverdrive=true;
        breakable = false;
        solid = true;
        sync = true;
        update = true;
        destructible = true;
        rebuildable = false;

    }

    @Override
    public boolean canBreak(Tile tile) {
        return breakable;
    }

    public class OEPayloadBlockBuild extends Building {
        @Override
        public void heal(float amount) {}
        @Override
        public boolean damaged() {return false;}
        @Override
        public void update() {
            super.update();
            this.team = Team.derelict;
        }
    }
}
