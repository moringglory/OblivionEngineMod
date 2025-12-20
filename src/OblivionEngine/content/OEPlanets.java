package OblivionEngine.content;

import arc.func.Prov;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.Planets;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;
import OblivionEngine.expand.map.DysonRingMesh;

public class OEPlanets {
    public static Planet vjsx,ymzq;
    public static void load(){
        vjsx = new Planet("vjsx", Planets.serpulo,1f,2){{
            this.iconColor = Color.valueOf("3299cc");
            this.generator = new SerpuloPlanetGenerator();
            this.meshLoader = new Prov<>() {
                @Override
                public GenericMesh get() {
                    return new HexMesh(vjsx,6);
                }
            };
            this.cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this,9,1.2F,0.24F,4,new Color().set(Pal.spore).mul(0.9f).a(0.75f),2,0.3F,0.7F, 0.43F),
                    new HexSkyMesh(this,3,3.7F,0.16F,6, Color.valueOf("a4b7fa").a(0.65F),6,0.45F,0.86F, 0.45F),
                    //new HexSkyMesh(this,3,0.01F,0.05F,5, Color.valueOf("ed7459"),2,0.5F,0.1F, 0.2F)
//                    new DysonRingMesh(this, 2.300f, 0.28f, 729, Pal.darkMetal, Pal.darkerMetal),
//                    new DysonRingMesh(this, 2.500f, 0.28f, 2941, Pal.darkMetal, Pal.darkerMetal),
//                    new DysonRingMesh(this, 2.700f, 0.28f, 3834, Pal.darkMetal, Pal.darkerMetal),
                    new DysonRingMesh(this, 2.305f, 0.19f, 729,OEColor.darkEnrColor,OEColor.darkEnrColor),
                    new DysonRingMesh(this, 2.505f, 0.19f, 2941,OEColor.darkEnrColor,OEColor.darkEnrColor),
                    new DysonRingMesh(this, 2.705f, 0.19f, 3834,OEColor.darkEnrColor,OEColor.darkEnrColor)
                        );
            this.ruleSetter = r -> {
                r.waves = true;
                r.waveTeam = Team.derelict;
                r.placeRangeCheck = false;
                r.showSpawns = true;
                r.waveSpacing = 60 * Time.toSeconds;
                r.initialWaveSpacing = 5f * Time.toMinutes;
                r.hideBannedBlocks = true;
//                r.spawns = NHPostProcess.generate(0.8f, false);
//                r.loadout = ItemStack.list(NHItems.titanium, 1000, NHItems.tungsten, 1000, NHItems.silicon, 1000, NHItems.zeta, 1000);

                Rules.TeamRule teamRule = r.teams.get(r.defaultTeam);
                teamRule.rtsAi = false;
                teamRule.unitBuildSpeedMultiplier = 5f;
                teamRule.buildSpeedMultiplier = 3f;
            };
            alwaysUnlocked = true;
            atmosphereColor = Color.valueOf("3299cc");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.4f;
            landCloudColor = atmosphereColor = Color.valueOf("3299cc");//00baff
        }};
        ymzq = new Planet("ymzq", Planets.sun,3.0f,2){{
            alwaysUnlocked = true;
        }};
    }
}
