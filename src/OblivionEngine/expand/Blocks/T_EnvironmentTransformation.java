package OblivionEngine.expand.Blocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import arc.struct.*;

import static mindustry.Vars.mods;

public class T_EnvironmentTransformation extends Block {
    public T_EnvironmentTransformation(String name) {
        super(name);
        configurable = false;
        destructible = true;
        solid = true;
        update = true;
        flags = EnumSet.of(BlockFlag.factory);
        size = 2;
    }

    public class Build extends Building {
        boolean raind = false;
        @Override
        public void update() {
            super.update();

            if(!raind) mods.getScripts().runConsole("Vars.content.getByName(ContentType.weather, 'rain').create(1.0, 60 * 60)");
            raind = true;
        }

        @Override
        public void draw() {
            super.draw();

            TextureRegion image = Core.atlas.find("oblivion-engine-heqing");

            Draw.rect(image, this.x, this.y);
        }
    }
}