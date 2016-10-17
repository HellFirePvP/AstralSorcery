package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.base.CropTypes;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectFertilitas
 * Created by HellFirePvP
 * Date: 16.10.2016 / 23:53
 */
public class CEffectFertilitas extends CEffectPositionBased {

    public static final int MAX_SEARCH_RANGE = 16;
    public static final int MAX_CROP_COUNT = 200;

    public CEffectFertilitas() {
        super(Constellations.fertilitas, MAX_SEARCH_RANGE, MAX_CROP_COUNT, (world, at) -> CropTypes.getCropType(world.getBlockState(at)) != null);
    }

    @Override
    public boolean mayExecuteMultipleMain() {
        return true;
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {}

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;
        if(doRandomOnPositions(world)) {
            BlockPos sel = positions.get(world.rand.nextInt(positions.size()));
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(sel))) {
                IBlockState state = world.getBlockState(sel);
                CropTypes reg = CropTypes.getCropType(state);
                if(reg == null) {
                    positions.remove(sel);
                    changed = true;
                } else {
                    reg.grow(world, sel, state);
                }
            }
        }

        if(super.playMainEffect(world, pos, percStrength, mayDoTraitEffect, possibleTraitEffect)) changed = true;

        return changed;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength) {
        return false;
    }

}
