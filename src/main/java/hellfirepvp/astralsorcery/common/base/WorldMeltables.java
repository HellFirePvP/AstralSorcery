package hellfirepvp.astralsorcery.common.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
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

    COBBLE(     (state) -> state.getBlock().equals(Blocks.COBBLESTONE),     Blocks.FLOWING_LAVA.getDefaultState(),  700),
    STONE(      (state) -> state.getBlock().equals(Blocks.STONE),           Blocks.FLOWING_LAVA.getDefaultState(),  400),
    OBSIDIAN(   (state) -> state.getBlock().equals(Blocks.OBSIDIAN),        Blocks.FLOWING_LAVA.getDefaultState(),  300),
    NETHERRACK( (state) -> state.getBlock().equals(Blocks.NETHERRACK),      Blocks.FLOWING_LAVA.getDefaultState(),  90),
    NETHERBRICK((state) -> state.getBlock().equals(Blocks.NETHER_BRICK),    Blocks.FLOWING_LAVA.getDefaultState(),  140),
    MAGMA(      (state) -> state.getBlock().equals(Blocks.field_189877_df), Blocks.FLOWING_LAVA.getDefaultState(),  2),
    SAND(       (state) -> state.getBlock().equals(Blocks.SAND),            Blocks.GLASS.getDefaultState(),         20),
    ICE(        (state) -> state.getBlock().equals(Blocks.ICE),             Blocks.FLOWING_WATER.getDefaultState(), 10),
    FROSTED_ICE((state) -> state.getBlock().equals(Blocks.FROSTED_ICE),     Blocks.FLOWING_WATER.getDefaultState(), 20),
    PACKED_ICE( (state) -> state.getBlock().equals(Blocks.PACKED_ICE),      Blocks.FLOWING_WATER.getDefaultState(), 30);

    private final BlockCheck meltableCheck;
    private final IBlockState meltResult;
    private final int meltDuration;

    private WorldMeltables(BlockCheck meltableCheck, IBlockState meltResult, int meltDuration) {
        this.meltableCheck = meltableCheck;
        this.meltResult = meltResult;
        this.meltDuration = meltDuration;
    }

    public boolean isMeltable(IBlockState worldState) {
        return meltableCheck.isMeltableBlock(worldState);
    }

    public IBlockState getMeltResult() {
        return meltResult;
    }

    public int getMeltDuration() {
        return meltDuration;
    }

    @Nullable
    public static WorldMeltables getMeltable(World world, BlockPos pos) {
        //System.out.println("test for " + pos);
        //System.out.println("found block: " + world.getBlockState(pos).getBlock().getUnlocalizedName());
        return getMeltable(world.getBlockState(pos));
    }

    @Nullable
    public static WorldMeltables getMeltable(IBlockState state) {
        for (WorldMeltables melt : values()) {
            if(melt.isMeltable(state))
                return melt;
        }
        return null;
    }

    public static interface BlockCheck {

        public boolean isMeltableBlock(IBlockState state);

    }

}
