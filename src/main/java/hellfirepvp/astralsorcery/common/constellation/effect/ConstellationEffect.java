package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    //May be executed multiple times per tick
    public abstract void playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect);

    //May be executed multiple times per tick
    public abstract void playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength);

    @Nullable
    public TileRitualPedestal getPedestal(World world, BlockPos pos) {
        return MiscUtils.getTileAt(world, pos, TileRitualPedestal.class);
    }

    @Nullable
    public EntityPlayer getOwningPlayerInWorld(World world, BlockPos pos) {
        TileRitualPedestal pedestal = MiscUtils.getTileAt(world, pos, TileRitualPedestal.class);
        if(pedestal != null) {
            return pedestal.getOwningPlayerInWorld(world);
        }
        return null;
    }

}
