package OblivionEngine.expand.Blocks;

import arc.func.Cons;
import arc.func.Intc2;
import arc.struct.EnumSet;
import arc.util.Nullable;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static mindustry.Vars.world;

public class OEExcavator extends Block {
    public Attribute attribute = Attribute.sand;
    public Item output = Items.sand;
    public OEExcavator(String name) {
        super(name);

        hasItems = true;
        rotate = true;
        update = true;
        solid = true;
        regionRotated1 = 1;

    }

    float getEfficiency(int tx, int ty, int rotation, @Nullable Cons<Tile> ctile, @Nullable Intc2 cpos){
        float eff = 0f;
        int cornerX = tx - (size-1)/2, cornerY = ty - (size-1)/2, s = size;

        for(int i = 0; i < size; i++){
            int rx = 0, ry = 0;

            switch(rotation){
                case 0 -> {
                    rx = cornerX + s;
                    ry = cornerY + i;
                }
                case 1 -> {
                    rx = cornerX + i;
                    ry = cornerY + s;
                }
                case 2 -> {
                    rx = cornerX - 1;
                    ry = cornerY + i;
                }
                case 3 -> {
                    rx = cornerX + i;
                    ry = cornerY - 1;
                }
            }

            if(cpos != null){
                cpos.get(rx, ry);
            }

            Tile other = world.tile(rx, ry);
            if(other != null && other.solid()){
                float at = other.block().attributes.get(attribute);
                eff += at;
                if(at > 0 && ctile != null){
                    ctile.get(other);
                }
            }
        }
        return eff;
    }
}
