/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectController;
import hellfirepvp.astralsorcery.client.effect.controller.OrbitalEffectLucerna;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerEntity;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.TickTokenizedMap;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectLucerna
 * Created by HellFirePvP
 * Date: 07.01.2017 / 19:24
 */
public class CEffectLucerna extends ConstellationEffect implements ConstellationEffectStatus {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;
    public static double range = 256;

    private int rememberedTimeout = 0;

    public CEffectLucerna(@Nullable ILocatable origin) {
        super(origin, Constellations.lucerna, "lucerna");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(pedestal.getTicksExisted() % 20 == 0) {
            OrbitalEffectLucerna luc = new OrbitalEffectLucerna();
            OrbitalEffectController ctrl = EffectHandler.getInstance().orbital(luc, luc, luc);
            ctrl.setOffset(new Vector3(pos).add(0.5, 0.5, 0.5));
            ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.7);
            ctrl.setOrbitAxis(Vector3.RotAxis.Y_AXIS);
            ctrl.setTicksPerRotation(20 + rand.nextInt(20));
        }
    }

    @Override
    public boolean runEffect(World world, BlockPos pos, int mirrorAmount, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        WorldBlockPos at = new WorldBlockPos(world, pos);
        TickTokenizedMap.SimpleTickToken<Double> token = EventHandlerEntity.spawnDenyRegions.get(at);
        if(token != null) {
            int next = token.getRemainingTimeout() + 80;
            if(next > 400) next = 400;
            token.setTimeout(next);
            rememberedTimeout = next;
        } else {
            rememberedTimeout = Math.min(400, rememberedTimeout + 80);
            EventHandlerEntity.spawnDenyRegions.put(at, new TickTokenizedMap.SimpleTickToken<>(range, rememberedTimeout));
        }
        return true;
    }

    @Override
    @Deprecated
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        return false;
    }

    @Override
    public boolean runTraitEffect(World world, BlockPos pos, int mirrorAmount, IMinorConstellation traitType) {
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        super.readFromNBT(cmp);

        this.rememberedTimeout = cmp.getInteger("rememberedTimeout");
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        super.writeToNBT(cmp);

        cmp.setInteger("rememberedTimeout", rememberedTimeout);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        range = cfg.getFloat(getKey() + "DenyRange", getConfigurationSection(), 256, 2, 2048, "Defines the range in which the ritual will prevent mobspawning.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }
}
