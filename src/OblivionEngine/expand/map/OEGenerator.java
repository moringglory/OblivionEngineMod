package OblivionEngine.expand.map;

import OblivionEngine.content.core.OEBlock;
import OblivionEngine.content.core.OEItems;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.graphics.Layer;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.world.Block;

public class OEGenerator extends PlanetGenerator {

    Block[] arr = new Block[]{
            Blocks.water,Blocks.sand,Blocks.grass,Blocks.stone,Blocks.snow
    };

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

//    @Override
//    public float getHeight(Vec3 position) {
//        float v = rawHeight(position);
//        OEItems.Uranium.description += ("\n" + v);
//        return v;
//    }

//    @Override
//    public void getColor(Vec3 position, Color out) {
//        int h = (int) Mathf.clamp(rawHeight(position) * 10,0,arr.length - 1);
//        out.set(arr[h].mapColor);
//    }

    @Override
    public void getEmissiveColor(Vec3 position, Color out) {
        float brightness = 1.0f;
        out.set(1f, 1f, 1f, 1f);
        out.mul(brightness);
        super.
    }


    @Override
    protected void generate() {
        pass((xx,yy) -> {
            if(xx % 10 == 0 || yy % 10 == 0)
                this.block = Blocks.copperWall;
        });

        median(2);
    }

}
