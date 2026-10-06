package oblivionengine.expand.Planet;

import arc.graphics.Color;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Time;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;

public class AethelgardGalaxy extends Planet {
    public AethelgardGalaxy() {
        super("aethelgard", null, 5.0F);
        this.bloom = true;
        this.accessible = false;
        meshLoader = () -> new SunMesh(
                this, 4,
                5, 0.3, 1.7, 1.2, 1,
                1.1f,
                Color.valueOf("ff7a38"),
                Color.valueOf("ff9638"),
                Color.valueOf("ffc64c"),
                Color.valueOf("ffc64c"),
                Color.valueOf("ffe371"),
                Color.valueOf("f4ee8e")
        );
    }

    public void draw(PlanetParams params, Mat3D projection, Mat3D transform) {
        this.mesh.render(params, projection, transform.setToTranslation(this.position).rotate(Vec3.Y, Time.time / 20.0F));
    }
}
