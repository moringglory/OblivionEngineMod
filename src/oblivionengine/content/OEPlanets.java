package oblivionengine.content;

import mindustry.graphics.g3d.MeshBuilder;
import mindustry.graphics.g3d.PlanetGrid;
import oblivionengine.expand.Planet.AethelgardGalaxy;
import oblivionengine.expand.Map.OEPlanetTools;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.Planets;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;
import oblivionengine.expand.Planet.DysonRingMesh;

public class OEPlanets {
    public static Planet tome,aethelgard,profundity,frostcinder,CHAT_2c,distant;
    public static void load(){
        //星系
        aethelgard = new AethelgardGalaxy(){{}};
        profundity = new Planet("profundity",Planets.sun,4,3){
            final float OE_RAD = 3f;
            @Override
            public void drawBorders(arc.graphics.g3d.VertexBatch3D batch, mindustry.type.Sector sector, Color base, float alpha){
                Color color = arc.util.Tmp.c1.set(base).a((base.a + 0.3f + arc.math.Mathf.absin(arc.util.Time.globalTime, 5f, 0.3f)) * alpha);

                float r1 = radius;
                float r2 = OE_RAD * radius + 0.001f;

                for(int i = 0; i < sector.tile.corners.length; i++){
                    PlanetGrid.Corner c = sector.tile.corners[i];
                    PlanetGrid.Corner next = sector.tile.corners[(i + 1) % sector.tile.corners.length];

                    arc.util.Tmp.v31.set(c.v).setLength(r2);
                    arc.util.Tmp.v32.set(next.v).setLength(r2);
                    arc.util.Tmp.v33.set(c.v).setLength(r1);
                    batch.tri2(arc.util.Tmp.v31, arc.util.Tmp.v32, arc.util.Tmp.v33, color);

                    arc.util.Tmp.v31.set(next.v).setLength(r2);
                    arc.util.Tmp.v32.set(next.v).setLength(r1);
                    arc.util.Tmp.v33.set(c.v).setLength(r1);
                    batch.tri2(arc.util.Tmp.v31, arc.util.Tmp.v32, arc.util.Tmp.v33, color);
                }
            }

            @Override
            public void fill(arc.graphics.g3d.VertexBatch3D batch, mindustry.type.Sector sector, Color color, float offset){
                float rr = OE_RAD * radius + offset;
                for(int i = 0; i < sector.tile.corners.length; i++){
                    PlanetGrid.Corner c = sector.tile.corners[i];
                    PlanetGrid.Corner next = sector.tile.corners[(i + 1) % sector.tile.corners.length];
                    batch.tri(
                            arc.util.Tmp.v31.set(c.v).setLength(rr),
                            arc.util.Tmp.v32.set(next.v).setLength(rr),
                            arc.util.Tmp.v33.set(sector.tile.v).setLength(rr),
                            color
                    );
                }
            }

            @Override
            public void drawSelection(arc.graphics.g3d.VertexBatch3D batch, mindustry.type.Sector sector, Color color, float stroke, float length){
                float arad = (OE_RAD + length) * radius;

                for(int i = 0; i < sector.tile.corners.length; i++){
                    PlanetGrid.Corner next = sector.tile.corners[(i + 1) % sector.tile.corners.length];
                    PlanetGrid.Corner curr = sector.tile.corners[i];

                    next.v.scl(arad);
                    curr.v.scl(arad);
                    sector.tile.v.scl(arad);

                    arc.util.Tmp.v31.set(curr.v).sub(sector.tile.v).setLength(curr.v.dst(sector.tile.v) - stroke).add(sector.tile.v);
                    arc.util.Tmp.v32.set(next.v).sub(sector.tile.v).setLength(next.v.dst(sector.tile.v) - stroke).add(sector.tile.v);

                    batch.quad(curr.v, next.v, arc.util.Tmp.v32, arc.util.Tmp.v31, color);

                    sector.tile.v.scl(1f / arad);
                    next.v.scl(1f / arad);
                    curr.v.scl(1f / arad);
                }
            }

            @Override
            public arc.math.geom.Vec3 project(mindustry.type.Sector sector, arc.graphics.g3d.Camera3D cam, arc.math.geom.Vec3 out){
                return cam.project(
                        out.set(sector.tile.v)
                                .setLength(OE_RAD * radius)
                                .rotate(arc.math.geom.Vec3.Y, -getRotation())
                                .add(position)
                );
            }

            @Override
            public void setPlane(mindustry.type.Sector sector, arc.graphics.g3d.PlaneBatch3D projector){
                float rotation = -getRotation();
                float length = 0.01f;

                projector.setPlane(
                        arc.util.Tmp.v33.set(sector.tile.v).setLength((OE_RAD + length) * radius)
                                .rotate(arc.math.geom.Vec3.Y, rotation).add(position),
                        sector.plane.project(arc.util.Tmp.v32.set(sector.tile.v).add(arc.math.geom.Vec3.Y))
                                .sub(sector.tile.v, radius).rotate(arc.math.geom.Vec3.Y, rotation).nor(),
                        arc.util.Tmp.v31.set(arc.util.Tmp.v32).rotate(arc.math.geom.Vec3.Y, -rotation).add(sector.tile.v)
                                .rotate(sector.tile.v, 90).sub(sector.tile.v).rotate(arc.math.geom.Vec3.Y, rotation).nor()
                );
            }
            {
                clipRadius = 0.7f;
                camRadius = 0.7f;
                minZoom = 0.8f;
                maxZoom = 1f;
                drawOrbit = false;
                orbitRadius = 100f;
                orbitTime = Float.MAX_VALUE;
                rotateTime = Float.MAX_VALUE;
                alwaysUnlocked = true;
                iconColor = Color.valueOf("5299ff");
                generator = new OEPlanetTools();
                gridMeshLoader = () -> MeshBuilder.buildPlanetGrid(
                        grid,
                        Pal.accent.cpy().a(1f),
                        OE_RAD * radius
                );
                meshLoader = () -> new OEPlanetTools.OEModMesh(
                        this, 6,
                        6, 0.3, 1.9, 1.6, 1.4f,
                        0.65f,
                        Color.valueOf("6a4c28"),
                        Color.valueOf("5a4020"),
                        Color.valueOf("4a3418")
                );
                cloudMeshLoader = () -> new MultiMesh(
                        new HexSkyMesh(this,  11, 0.030f, 1.05f, 6, Color.valueOf("9a7048").a(0.50f), 5, 0.50f, 0.55f, 0.80f),
                        new HexSkyMesh(this,  29, 0.045f, 1.15f, 6, Color.valueOf("c8a070").a(0.45f), 5, 0.48f, 0.65f, 0.75f),
                        new HexSkyMesh(this,  47, 0.060f, 1.28f, 6,Color.valueOf("8a6038").a(0.42f), 4, 0.52f, 0.80f, 0.70f),
                        new HexSkyMesh(this,  61, 0.075f, 1.45f, 6, Color.valueOf("d4bd94").a(0.38f), 5, 0.50f, 0.95f, 0.68f),
                        new HexSkyMesh(this,  89, 0.090f, 1.65f, 6, Color.valueOf("7a5028").a(0.32f), 4, 0.50f, 1.15f, 0.60f),
                        new HexSkyMesh(this, 113, 0.105f, 1.85f, 6, Color.valueOf("e8d5b0").a(0.26f), 4, 0.50f, 1.40f, 0.55f),
                        new HexSkyMesh(this, 151, 0.120f, 2.05f, 6,Color.valueOf("f0e2c8").a(0.20f), 3, 0.50f, 1.70f, 0.50f),
                        new HexSkyMesh(this, 191, 0.135f, 2.25f, 5, Color.white.cpy().a(0.14f), 3, 0.45f, 2.10f, 0.45f)
                );
                ruleSetter = r -> {
                    r.waves = true;
                    r.waveTeam = Team.derelict;
                    r.placeRangeCheck = false;
//                    r.showSpawns = true;
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
            }
        };
        tome = new Planet("tome", profundity,1f,2){{
            solarSystem = Planets.sun;
            orbitRadius = profundity.radius + 1f + 20f;
            orbitTime = 60f * 10f;
            iconColor = Color.valueOf("3299cc");
            generator = new OEPlanetTools();
            position.set(
                    profundity.position.x + this.orbitRadius,
                    profundity.position.y,
                    profundity.position.z
            );
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
//                r.showSpawns = true;
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
