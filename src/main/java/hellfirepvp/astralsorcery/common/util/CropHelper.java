package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CropHelper
 * Created by HellFirePvP
 * Date: 08.11.2016 / 13:05
 */
//Intended for mostly Server-Side use
public class CropHelper {

    @Nullable
    public static GrowablePlant wrapPlant(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if(state.getBlock() instanceof IGrowable) {
            if(b instanceof BlockGrass) return null;
            if(b instanceof BlockTallGrass) return null;
            if(b instanceof BlockDoublePlant) return null;
            return new GrowableWrapper(pos);
        }
        if(state.getBlock().equals(Blocks.REEDS)) {
            if(isReedBase(world, pos)) {
                return new GrowableReedWrapper(pos);
            }
        }
        return null;
    }

    @Nullable
    public static HarvestablePlant wrapHarvestablePlant(World world, BlockPos pos) {
        GrowablePlant growable = wrapPlant(world, pos);
        if(growable == null) return null; //Every plant has to be growable.
        IBlockState state = world.getBlockState(growable.getPos());
        if(state.getBlock() instanceof IPlantable) {
            return new HarvestableWrapper(growable);
        }
        if(state.getBlock().equals(Blocks.REEDS) && growable instanceof GrowableReedWrapper) {
            return (GrowableReedWrapper) growable;
        }
        return null;
    }

    private static boolean isReedBase(World world, BlockPos pos) {
        return !world.getBlockState(pos.down()).getBlock().equals(Blocks.REEDS);
    }

    public static interface GrowablePlant extends CEffectPositionListGen.CEffectGenListEntry {

        public boolean isValid(World world, boolean forceChunkLoad);

        public boolean canGrow(World world);

        public boolean tryGrow(World world, Random rand);

    }

    public static interface HarvestablePlant extends GrowablePlant {

        public boolean canHarvest(World world);

        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune);

    }

    public static class HarvestableWrapper implements HarvestablePlant {

        private final GrowablePlant plant;

        public HarvestableWrapper(GrowablePlant plant) {
            this.plant = plant;
        }

        @Override
        public boolean canHarvest(World world) {
            return !plant.canGrow(world);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            if(canHarvest(world)) {
                BlockPos pos = getPos();
                IBlockState at = world.getBlockState(getPos());
                if(at.getBlock() instanceof IPlantable) {
                    drops.addAll(at.getBlock().getDrops(world, pos, at, harvestFortune));
                    world.setBlockState(pos, ((IPlantable) at.getBlock()).getPlant(world, pos));
                }
            }
            return drops;
        }

        @Override
        public BlockPos getPos() {
            return plant.getPos();
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if(!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(getPos()))) return true; //We stall until it's loaded.
            return wrapHarvestablePlant(world, getPos()) != null;
        }

        @Override
        public boolean canGrow(World world) {
            return plant.canGrow(world);
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            return plant.tryGrow(world, rand);
        }
    }

    public static class GrowableReedWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableReedWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(World world) {
            return world.getBlockState(pos.up()).getBlock().equals(Blocks.REEDS);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            for (int i = 2; i > 0; i++) {
                BlockPos bp = pos.up(i);
                IBlockState at = world.getBlockState(bp);
                if(at.getBlock().equals(Blocks.REEDS)) {
                    drops.addAll(at.getBlock().getDrops(world, bp, at, harvestFortune));
                    world.setBlockToAir(bp);
                }
            }
            return drops;
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if(!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; //We stall until it's loaded.
            return world.getBlockState(pos).getBlock().equals(Blocks.REEDS);
        }

        @Override
        public boolean canGrow(World world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if(world.isAirBlock(cache)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if(world.isAirBlock(cache)) {
                    if(rand.nextInt(15) == 0) {
                        world.setBlockState(cache, Blocks.REEDS.getDefaultState());
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

    }

    public static class GrowableWrapper implements GrowablePlant {

        private final BlockPos pos;

        public GrowableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if(!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; //We stall until it's loaded.
            GrowablePlant res = wrapPlant(world, pos);
            return res != null && res instanceof GrowableWrapper;
        }

        @Override
        public boolean canGrow(World world) {
            IBlockState at = world.getBlockState(pos);
            return at.getBlock() instanceof IGrowable && ((IGrowable) at.getBlock()).canGrow(world, pos, at, false);
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            IBlockState at = world.getBlockState(pos);
            if(at.getBlock() instanceof IGrowable) {
                if(((IGrowable) at.getBlock()).canGrow(world, pos, at, false)) {
                    ((IGrowable) at.getBlock()).grow(world, rand, pos, at);
                    return true;
                }
            }
            return false;
        }
    }

}
