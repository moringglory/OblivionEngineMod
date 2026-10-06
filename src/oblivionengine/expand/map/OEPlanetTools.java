package oblivionengine.expand.map;

import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexMesher;
import mindustry.graphics.g3d.ShaderSphereMesh;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Planet;
import mindustry.world.Block;

public class OEPlanetTools extends PlanetGenerator {

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
//    public boolean isEmissive() {
//        return true;
//    }

    @Override
    public void getColor(Vec3 position, Color out) {
        int h = (int) Mathf.clamp(rawHeight(position) * 10,0,arr.length - 1);
        out.set(arr[h].mapColor);
    }

//    @Override
//    public void getEmissiveColor(Vec3 position, Color out) {
//        if(rawHeight(position) * 10==5) out.set(Blocks.water.mapColor).mul(0.1f);
//        else super.getEmissiveColor(position, out);
//    }

    @Override
    protected void generate() {
        pass((xx,yy) -> {
            if(xx % 10 == 0 || yy % 10 == 0)
                this.block = Blocks.copperWall;
        });

        median(2);
    }
    public static class OEModMesh extends HexMesh {
        public static float waterOffset = 0.05f;

        public OEModMesh(Planet planet, int divisions, double octaves, double persistence, double scl, double pow, double mag, float colorScale, Color... colors){
            super(planet, new HexMesher(){
                @Override
                public float getHeight(Vec3 position){
                    position = Tmp.v33.set(position).scl(4f);
                    float height = (Mathf.pow(Simplex.noise3d(123, 7, 0.5f, 1f/3f, position.x, position.y, position.z), 2.3f) + waterOffset) / (1f + waterOffset);
                    return Math.max(height, 0.05f);
                }

                @Override
                public void getColor(Vec3 position, Color out) {
                    HexMesher.super.getColor(position, out);
                    double height = Math.pow(Simplex.noise3d(1, octaves, persistence, scl, position.x, position.y, position.z), pow) * mag;
                    out.set(Tmp.c1.set(colors[Mathf.clamp((int)(height * colors.length), 0, colors.length - 1)]).mul(colorScale));
                }
            }, divisions, Shaders.unlit);
        }
    }
}
