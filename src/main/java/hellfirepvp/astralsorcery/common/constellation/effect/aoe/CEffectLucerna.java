package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectLucerna
 * Created by HellFirePvP
 * Date: 07.11.2016 / 08:21
 */
public class CEffectLucerna extends ConstellationEffect {

    public static boolean enabled = true;

    public CEffectLucerna() {
        super(Constellations.lucerna, "lucerna");
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {

    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(!enabled) return false;
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        super.readFromNBT(cmp);
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        super.writeToNBT(cmp);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
    }
}
