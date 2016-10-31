package hellfirepvp.astralsorcery.common.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CropRegistry
 * Created by HellFirePvP
 * Date: 17.10.2016 / 00:27
 */
public enum CropTypes {

    WHEAT(Blocks.WHEAT, new BlockStateCheck.Meta(7), Items.WHEAT_SEEDS),
    CARROT(Blocks.CARROTS, new BlockStateCheck.Meta(7), Items.CARROT),
    POTATO(Blocks.POTATOES, new BlockStateCheck.Meta(7), Items.POTATO),
    NETHERWART(Blocks.NETHER_WART, new BlockStateCheck.Meta(3), Items.NETHER_WART);

    public final Block block;
    public final BlockStateCheck doneCheck;
    public final Item seed;

    private CropTypes(Block block, BlockStateCheck doneCheck, Item seed) {
        this.block = block;
        this.doneCheck = doneCheck;
        this.seed = seed;
    }

    public boolean grow(World world, BlockPos pos, IBlockState oldState) {
        //int addStage = 1 + world.rand.nextInt(2);
        if(doneCheck.isStateValid(oldState)) return false;
        int growth;
        switch (this) {
            case WHEAT:
                growth = oldState.getValue(BlockCrops.AGE) + 1 + world.rand.nextInt(3);
                if(growth > 7) growth = 7;
                world.setBlockState(pos, oldState.withProperty(BlockCrops.AGE, growth));
                return true;
            case CARROT:
                growth = oldState.getValue(BlockCarrot.AGE) + 1 + world.rand.nextInt(3);
                if(growth > 7) growth = 7;
                world.setBlockState(pos, oldState.withProperty(BlockCarrot.AGE, growth));
                return true;
            case POTATO:
                growth = oldState.getValue(BlockPotato.AGE) + 1 + world.rand.nextInt(3);
                if(growth > 7) growth = 7;
                world.setBlockState(pos, oldState.withProperty(BlockPotato.AGE, growth));
                return true;
            case NETHERWART:
                growth = oldState.getValue(BlockNetherWart.AGE) + 1;
                if(growth > 3) growth = 3;
                world.setBlockState(pos, oldState.withProperty(BlockNetherWart.AGE, growth));
                return true;
            default:
                return false;
        }
    }

    public boolean isGrown(World world, BlockPos pos, IBlockState state) {
        return doneCheck.isStateValid(state);
    }

    public List<ItemStack> tryHarvestAndReplant(World world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new LinkedList<>();
        switch (this) {
            case WHEAT:
                drops.addAll(state.getBlock().getDrops(world, pos, state, fortune));
                world.setBlockState(pos, state.withProperty(BlockCrops.AGE, 2 + world.rand.nextInt(2)));
                break;
            case CARROT:
                drops.addAll(state.getBlock().getDrops(world, pos, state, fortune));
                world.setBlockState(pos, state.withProperty(BlockCarrot.AGE, 2 + world.rand.nextInt(2)));
                break;
            case POTATO:
                drops.addAll(state.getBlock().getDrops(world, pos, state, fortune));
                world.setBlockState(pos, state.withProperty(BlockPotato.AGE, 2 + world.rand.nextInt(2)));
                break;
            case NETHERWART:
                drops.addAll(state.getBlock().getDrops(world, pos, state, fortune));
                world.setBlockState(pos, state.withProperty(BlockNetherWart.AGE, 1));
                break;
        }
        return drops;
    }

    public static boolean isCrop(World world, BlockPos pos) {
        return isCrop(world.getBlockState(pos));
    }

    public static boolean isCrop(IBlockState state) {
        return getCropType(state) != null;
    }

    @Nullable
    public static CropTypes getCropType(IBlockState state) {
        for (CropTypes reg : values()) {
            if(reg.block.equals(state.getBlock())) return reg;
        }
        return null;
    }

}
