package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectAevitas
 * Created by HellFirePvP
 * Date: 29.11.2016 / 01:38
 */
public class CEffectAevitas extends CEffectPositionListGen<CropHelper.GrowablePlant> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 16;
    public static int maxCropCount = 200;

    public CEffectAevitas() {
        super(Constellations.aevitas, "aevitas", searchRange, maxCropCount, (world, pos) -> CropHelper.wrapPlant(world, pos) != null, CropHelper.GrowableWrapper::new);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(!Minecraft.isFancyGraphicsEnabled() && rand.nextInt(12) != 0) return;
        if(rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY() + rand.nextFloat() * 2 + 0.5,
                    pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F).setColor(new Color(63, 255, 63)).setMaxAge(35);
        }
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;
        CropHelper.GrowablePlant plant = getRandomElementByChance(rand);
        if(plant != null) {
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(plant.getPos()))) {
                if(!plant.isValid(world, true)) {
                    removeElement(plant);
                    changed = true;
                } else {
                    if(plant.tryGrow(world, rand)) {
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, plant.getPos());
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, plant.getPos(), 8));
                        changed = true;
                    }
                }
            }
        }

        if(findNewPosition(world, pos)) changed = true;

        return changed;
    }

    @Override
    public CropHelper.GrowablePlant newElement(World world, BlockPos at) {
        return CropHelper.wrapPlant(world, at);
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        for (int i = 0; i < 8; i++) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    at.getX() + rand.nextFloat(),
                    at.getY() + 0.2,
                    at.getZ() + rand.nextFloat());
            p.motion(0, 0.005 + rand.nextFloat() * 0.01, 0);
            p.scale(0.2F).setColor(Color.GREEN);
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 16, 1, 32, "Defines the radius (in blocks) in which the ritual will search for valid crops.");
        maxCropCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 200, 1, 4000, "Defines the amount of crops the ritual can cache at max. count");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
