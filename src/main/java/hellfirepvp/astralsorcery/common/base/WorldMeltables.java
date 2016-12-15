package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldMeltables
 * Created by HellFirePvP
 * Date: 31.10.2016 / 22:49
 */
public enum WorldMeltables {

    COBBLE(     new BlockStateCheck.Block(Blocks.COBBLESTONE),     Blocks.FLOWING_LAVA.getDefaultState(),  700),
    STONE(      new BlockStateCheck.Block(Blocks.STONE),           Blocks.FLOWING_LAVA.getDefaultState(),  400),
    OBSIDIAN(   new BlockStateCheck.Block(Blocks.OBSIDIAN),        Blocks.FLOWING_LAVA.getDefaultState(),  300),
    NETHERRACK( new BlockStateCheck.Block(Blocks.NETHERRACK),      Blocks.FLOWING_LAVA.getDefaultState(),  90),
    NETHERBRICK(new BlockStateCheck.Block(Blocks.NETHER_BRICK),    Blocks.FLOWING_LAVA.getDefaultState(),  140),
    MAGMA(      new BlockStateCheck.Block(Blocks.MAGMA),           Blocks.FLOWING_LAVA.getDefaultState(),  2),
    SAND(       new BlockStateCheck.Block(Blocks.SAND),            Blocks.GLASS.getDefaultState(),         20),
    ICE(        new BlockStateCheck.Block(Blocks.ICE),             Blocks.FLOWING_WATER.getDefaultState(), 10),
    FROSTED_ICE(new BlockStateCheck.Block(Blocks.FROSTED_ICE),     Blocks.FLOWING_WATER.getDefaultState(), 20),
    PACKED_ICE( new BlockStateCheck.Block(Blocks.PACKED_ICE),      Blocks.FLOWING_WATER.getDefaultState(), 30);

    private final BlockStateCheck meltableCheck;
    private final IBlockState meltResult;
    private final int meltDuration;

    private WorldMeltables(BlockStateCheck meltableCheck, IBlockState meltResult, int meltDuration) {
        this.meltableCheck = meltableCheck;
        this.meltResult = meltResult;
        this.meltDuration = meltDuration;
    }

    public boolean isMeltable(World world, BlockPos pos, IBlockState worldState) {
        return meltableCheck.isStateValid(world, pos, worldState);
    }

    public IBlockState getMeltResult() {
        return meltResult;
    }

    public int getMeltDuration() {
        return meltDuration;
    }

    @Nullable
    public static WorldMeltables getMeltable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        for (WorldMeltables melt : values()) {
            if(melt.isMeltable(world, pos, state))
                return melt;
        }
        return null;
    }

    public static class ActiveMeltableEntry extends GenListEntries.CounterListEntry {

        public ActiveMeltableEntry(BlockPos pos) {
            super(pos);
        }

        public boolean isValid(World world, boolean forceLoad) {
            if(!forceLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(getPos()))) return true;
            return getMeltable(world) != null;
        }

        public WorldMeltables getMeltable(World world) {
            return WorldMeltables.getMeltable(world, getPos());
        }

    }

}
