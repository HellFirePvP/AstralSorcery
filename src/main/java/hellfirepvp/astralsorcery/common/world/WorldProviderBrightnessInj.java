/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldProviderBrightnessInj
 * Created by HellFirePvP
 * Date: 14.05.2016 / 23:32
 */
public class WorldProviderBrightnessInj extends WorldProvider {

    protected final WorldProvider parentOvrProvider;
    protected final World parentWorld;

    public WorldProviderBrightnessInj(World world, WorldProvider parent) {
        this.parentOvrProvider = parent;
        this.parentWorld = world;
    }

    private WorldSkyHandler getSkyHandler() {
        return ConstellationSkyHandler.getInstance().getWorldHandler(parentWorld);
    }

    @Override
    public DimensionType getDimensionType() {
        return parentOvrProvider.getDimensionType();
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return parentOvrProvider.createChunkGenerator();
    }

    @Override
    public WorldBorder createWorldBorder() {
        return parentOvrProvider.createWorldBorder();
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return parentOvrProvider.canCoordinateBeSpawn(x, z);
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return parentOvrProvider.calculateCelestialAngle(worldTime, partialTicks);
    }

    @Nullable
    @Override
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        return parentOvrProvider.calcSunriseSunsetColors(celestialAngle, partialTicks);
    }

    @Override
    public void calculateInitialWeather() {
        parentOvrProvider.calculateInitialWeather();
    }

    @Override
    public double getMovementFactor() {
        return parentOvrProvider.getMovementFactor();
    }

    @Override
    public boolean canDoLightning(Chunk chunk) {
        return parentOvrProvider.canDoLightning(chunk);
    }

    @Override
    public Biome getBiomeForCoords(BlockPos pos) {
        return parentOvrProvider.getBiomeForCoords(pos);
    }

    @Override
    public BiomeProvider getBiomeProvider() {
        return parentOvrProvider.getBiomeProvider();
    }

    @Override
    public BlockPos getRandomizedSpawnPoint() {
        return parentOvrProvider.getRandomizedSpawnPoint();
    }

    @Override
    public BlockPos getSpawnCoordinate() {
        return parentOvrProvider.getSpawnCoordinate();
    }

    @Override
    public BlockPos getSpawnPoint() {
        return parentOvrProvider.getSpawnPoint();
    }

    @Override
    public boolean isNether() {
        return parentOvrProvider.isNether();
    }

    @Override
    public double getHorizon() {
        return parentOvrProvider.getHorizon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float parTicks) {
        WorldSkyHandler handle = getSkyHandler();
        if(handle != null) {
            float sunBr = parentWorld.getSunBrightnessBody(parTicks);
            int eclTick = handle.solarEclipseTick;
            if (eclTick >= ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR) { //fading out
                eclTick -= ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR;
            } else {
                eclTick = ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR - eclTick;
            }
            float perc = ((float) eclTick) / ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR;
            return sunBr * (0.15F + (0.85F * perc));
        }
        return parentWorld.getSunBrightnessBody(parTicks);
    }

    @Override
    public float getSunBrightnessFactor(float parTicks) {
        WorldSkyHandler handle = getSkyHandler();
        if(handle != null) {
            if (handle.dayOfSolarEclipse && handle.solarEclipse) {
                float sunBr = parentWorld.getSunBrightnessFactor(parTicks);
                int eclTick = handle.solarEclipseTick;
                if (eclTick >= ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR) { //fading out
                    eclTick -= ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR;
                } else {
                    eclTick = ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR - eclTick;
                }
                float perc = ((float) eclTick) / ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR;
                return sunBr * (0.15F + (0.85F * perc));
            }
        }
        return parentWorld.getSunBrightnessFactor(parTicks);
    }

    @Override
    public double getVoidFogYFactor() {
        return parentOvrProvider.getVoidFogYFactor();
    }

    @Override
    public float getCloudHeight() {
        return parentOvrProvider.getCloudHeight();
    }

    @Override
    public float getCurrentMoonPhaseFactor() {
        return parentOvrProvider.getCurrentMoonPhaseFactor();
    }

    @Override
    public float getStarBrightness(float par1) {
        return parentOvrProvider.getStarBrightness(par1);
    }

    @Override
    public float[] getLightBrightnessTable() {
        return parentOvrProvider.getLightBrightnessTable();
    }

    @Override
    public int getActualHeight() {
        return parentOvrProvider.getActualHeight();
    }

    @Override
    public int getAverageGroundLevel() {
        return parentOvrProvider.getAverageGroundLevel();
    }

    @Override
    public int getDimension() {
        return parentOvrProvider.getDimension();
    }

    @Override
    public int getHeight() {
        return parentOvrProvider.getHeight();
    }

    @Override
    public int getMoonPhase(long worldTime) {
        return parentOvrProvider.getMoonPhase(worldTime);
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return parentOvrProvider.getRespawnDimension(player);
    }

    @Override
    public IRenderHandler getCloudRenderer() {
        return parentOvrProvider.getCloudRenderer();
    }

    @Override
    public IRenderHandler getSkyRenderer() {
        return parentOvrProvider.getSkyRenderer();
    }

    @Override
    public IRenderHandler getWeatherRenderer() {
        return parentOvrProvider.getWeatherRenderer();
    }

    @Override
    public long getSeed() {
        return parentOvrProvider.getSeed();
    }

    @Override
    public long getWorldTime() {
        return parentOvrProvider.getWorldTime();
    }

    @Override
    public String getSaveFolder() {
        return parentOvrProvider.getSaveFolder();
    }

    @Override
    public Vec3d getCloudColor(float partialTicks) {
        return parentOvrProvider.getCloudColor(partialTicks);
    }

    @Override
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_) {
        return parentOvrProvider.getFogColor(p_76562_1_, p_76562_2_);
    }

    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        return parentOvrProvider.getSkyColor(cameraEntity, partialTicks);
    }

    @Override
    public boolean canDropChunk(int x, int z) {
        return parentOvrProvider.canDropChunk(x, z);
    }

    @Override
    public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
        return parentOvrProvider.canBlockFreeze(pos, byWater);
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return parentOvrProvider.canDoRainSnowIce(chunk);
    }

    @Override
    public boolean canMineBlock(EntityPlayer player, BlockPos pos) {
        return parentOvrProvider.canMineBlock(player, pos);
    }

    @Override
    public boolean canRespawnHere() {
        return parentOvrProvider.canRespawnHere();
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        return parentOvrProvider.canSnowAt(pos, checkLight);
    }

    @Override
    public boolean isSkyColored() {
        return parentOvrProvider.isSkyColored();
    }

    @Override
    public ICapabilityProvider initCapabilities() {
        return parentOvrProvider.initCapabilities();
    }

    @Override
    public void setCloudRenderer(IRenderHandler renderer) {
        parentOvrProvider.setCloudRenderer(renderer);
    }

    @Override
    public void resetRainAndThunder() {
        parentOvrProvider.resetRainAndThunder();
    }

    @Override
    public void onPlayerRemoved(EntityPlayerMP player) {
        parentOvrProvider.onPlayerRemoved(player);
    }

    @Override
    public void setSkyRenderer(IRenderHandler skyRenderer) {
        parentOvrProvider.setSkyRenderer(skyRenderer);
    }

    @Override
    public void setWeatherRenderer(IRenderHandler renderer) {
        parentOvrProvider.setWeatherRenderer(renderer);
    }

    @Override
    public boolean doesWaterVaporize() {
        return parentOvrProvider.doesWaterVaporize();
    }

    @Override
    public boolean isDaytime() {
        WorldSkyHandler handle = getSkyHandler();
        if(handle != null) {
            if (handle.dayOfSolarEclipse && handle.solarEclipse) {
                return true;
            }
        }
        return parentOvrProvider.isDaytime();
    }

    @Override
    public boolean doesXZShowFog(int x, int z) {
        return parentOvrProvider.doesXZShowFog(x, z);
    }

    @Override
    public void setDimension(int dim) {
        parentOvrProvider.setDimension(dim);
    }

    @Override
    public boolean isSurfaceWorld() {
        return parentOvrProvider.isSurfaceWorld();
    }

    @Override
    public boolean isBlockHighHumidity(BlockPos pos) {
        return parentOvrProvider.isBlockHighHumidity(pos);
    }

    @Override
    public void onWorldUpdateEntities() {
        parentOvrProvider.onWorldUpdateEntities();
    }

    @Override
    public void onWorldSave() {
        parentOvrProvider.onWorldSave();
    }

    @Override
    public void onPlayerAdded(EntityPlayerMP player) {
        parentOvrProvider.onPlayerAdded(player);
    }

    @Override
    public void setSpawnPoint(BlockPos pos) {
        parentOvrProvider.setSpawnPoint(pos);
    }

    @Override
    public void setWorldTime(long time) {
        parentOvrProvider.setWorldTime(time);
    }

    @Override
    public void updateWeather() {
        parentOvrProvider.updateWeather();
    }

    @Override
    public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
        parentOvrProvider.setAllowedSpawnTypes(allowHostile, allowPeaceful);
    }

    @Override
    public boolean shouldMapSpin(String entity, double x, double y, double z) {
        return parentOvrProvider.shouldMapSpin(entity, x, y, z);
    }

    @Override
    public boolean hasSkyLight() {
        return parentOvrProvider.hasSkyLight();
    }

}
