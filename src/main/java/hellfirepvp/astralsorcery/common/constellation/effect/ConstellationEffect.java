package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffect
 * Created by HellFirePvP
 * Date: 01.10.2016 / 15:47
 */
public abstract class ConstellationEffect {

    private final Constellation constellation;

    public ConstellationEffect(Constellation constellation) {
        this.constellation = constellation;
    }

    public Constellation getConstellation() {
        return constellation;
    }

    public boolean mayExecuteMultipleMain() {
        return false;
    }

    public boolean mayExecuteMultipleTrait() {
        return false;
    }

    //Once per TE client tick
    @SideOnly(Side.CLIENT)
    public abstract void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal,  float percEffectVisibility, boolean extendedEffects);

    //May be executed multiple times per tick
    //Even if this effect can handle multiple effects per tick, it is still possible that this method is called.
    public abstract void playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect);

    //May be executed multiple times per tick
    public abstract void playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength);

    //Should handle multiple executions at once ('times' executions)
    public abstract void playMainEffectMultiple(World world, BlockPos pos, int times, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect);

    public abstract void playTraitEffectMultiple(World world, BlockPos pos, Constellation traitType, int times);

    @Nullable
    public TileRitualPedestal getPedestal(World world, BlockPos pos) {
        return MiscUtils.getTileAt(world, pos, TileRitualPedestal.class, false);
    }

    @Nullable
    public EntityPlayer getOwningPlayerInWorld(World world, BlockPos pos) {
        TileRitualPedestal pedestal = getPedestal(world, pos);
        if(pedestal != null) {
            return pedestal.getOwningPlayerInWorld(world);
        }
        return null;
    }

}
