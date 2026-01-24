package OblivionEngine.expand.map;

import OblivionEngine.content.core.OEItems;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Block;

public class OEGenerator extends PlanetGenerator {
    Block[] arr = new Block[]{
            Blocks.water,Blocks.sand,Blocks.grass,Blocks.stone,Blocks.snow
    };

    @Override
    protected void generate() {
        pass((xx,yy) -> {
            if(xx % 10 == 0 || yy % 10 == 0)
                this.block = Blocks.copperWall;
        });

        median(2);

    }

    float rawHeight(Vec3 position) {
        position = Tmp.v33.set(position);
        return (Mathf.pow(
                Simplex.noise3d(
                        this.seed,
                        (double) 7.0F,
                        (double) 0.5F,
                        (double) 0.33333334F,
                        (double) position.x,
                        (double) position.y,
                        (double) position.z),
                2.3F));
    }

    @Override
    public float getHeight(Vec3 position) {
        float v = rawHeight(position);
        OEItems.Uranium.description += ("\n" + v);
        return v;
    }

//    @Override
//    public Color getColor(Vec3 position) {
////        return Blocks.sand.mapColor;
//        float h = rawHeight(position) * 10;
//        return arr[(int) Mathf.clamp(h,0,arr.length - 1)].mapColor;
//    }
}
