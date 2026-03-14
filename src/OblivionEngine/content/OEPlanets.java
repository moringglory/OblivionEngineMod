package OblivionEngine.content;

import OblivionEngine.expand.map.AethelgardGalaxy;
import OblivionEngine.expand.map.OEPlanetTools;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.Planets;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;
import OblivionEngine.expand.map.DysonRingMesh;

public class OEPlanets {
    public static Planet tome,aethelgard,profundity,frostcinder,CHAT_2c,distant;
    public static void load(){
        //太阳系
        tome = new Planet("tome", Planets.serpulo,1f,2){{
            iconColor = Color.valueOf("3299cc");
            generator = new OEPlanetTools();
//            meshLoader = () -> new HexMesh(this, 5);
            meshLoader = () -> new OEPlanetTools.OEModMesh(
                    this, 6,
                    6, 0.3, 1.9, 1.6, 1.4f,
                    1.1f,
                    OEColor.SiAlONPhosphor,
                    OEColor.SiAlONPhosphor.cpy().lerp(Color.black, 0.2f).mul(1.05f),
                    Pal.darkestGray.cpy().mul(0.95f),
                    Pal.darkestGray.cpy().lerp(Color.white, 0.105f),
                    OEColor.lightDinoflagellates.cpy().lerp(Pal.gray, 0.2f),
                    OEColor.lightDinoflagellates,
                    OEColor.darkDinoflagellates
            );
            this.cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this,9,1.2F,0.24F,4,new Color().set(Pal.spore).mul(0.9f).a(0.75f),2,0.3F,0.7F, 0.43F),
                    new HexSkyMesh(this,3,3.7F,0.16F,6, Color.valueOf("a4b7fa").a(0.65F),6,0.45F,0.86F, 0.45F),
                    //new HexSkyMesh(this,3,0.01F,0.05F,5, Color.valueOf("ed7459"),2,0.5F,0.1F, 0.2F)
                    new DysonRingMesh(this, 2.305f, 0.19f, 729,OEColor.darkSpaceMetal,OEColor.CarbonParticles),
                    new DysonRingMesh(this, 2.505f, 0.19f, 2941,OEColor.CarbonParticles,OEColor.SiAlONPhosphor),
                    new DysonRingMesh(this, 2.705f, 0.19f, 3834,OEColor.darkDinoflagellates,OEColor.CarbonParticles)
                        );
            ruleSetter = r -> {
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
        //星系
        aethelgard = new AethelgardGalaxy();
        profundity = new Planet("profundity",aethelgard,4,3){{
            alwaysUnlocked = true;
            iconColor = Color.valueOf("5299ff");
            generator = new OEPlanetTools();
//            meshLoader = () -> new HexMesh(this, 5);
            meshLoader = () -> new OEPlanetTools.OEModMesh(
                    this, 6,
                    6, 0.3, 1.9, 1.6, 1.4f,
                    1.1f,
                    OEColor.SiAlONPhosphor,
                    OEColor.SiAlONPhosphor.cpy().lerp(Color.black, 0.2f).mul(1.05f),
                    Pal.darkestGray.cpy().mul(0.95f),
                    Pal.darkestGray.cpy().lerp(Color.white, 0.105f),
                    OEColor.lightDinoflagellates.cpy().lerp(Pal.gray, 0.2f),
                    OEColor.lightDinoflagellates,
                    OEColor.darkDinoflagellates
            );
            this.cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this,9,1.2F,0.24F,4,new Color().set(Pal.spore).mul(0.9f).a(0.75f),2,0.3F,0.7F, 0.43F),
                    new HexSkyMesh(this,3,3.7F,0.16F,6, Color.valueOf("a4b7fa").a(0.65F),6,0.45F,0.86F, 0.45F),
                    //new HexSkyMesh(this,3,0.01F,0.05F,5, Color.valueOf("ed7459"),2,0.5F,0.1F, 0.2F)
                    new DysonRingMesh(this, 2.305f, 0.19f, 729,OEColor.darkSpaceMetal,OEColor.CarbonParticles),
                    new DysonRingMesh(this, 2.505f, 0.19f, 2941,OEColor.CarbonParticles,OEColor.SiAlONPhosphor),
                    new DysonRingMesh(this, 2.705f, 0.19f, 3834,OEColor.darkDinoflagellates,OEColor.CarbonParticles)
            );
            ruleSetter = r -> {
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
    }
}
