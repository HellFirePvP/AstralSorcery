/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
        if(state.getBlock().equals(Blocks.CACTUS)) {
            if(isCactusBase(world, pos)) {
                return new GrowableCactusWrapper(pos);
            }
        }
        if(state.getBlock().equals(Blocks.NETHER_WART)) {
            return new GrowableNetherwartWrapper(pos);
        }
        return null;
    }

    @Nullable
    public static HarvestablePlant wrapHarvestablePlant(World world, BlockPos pos) {
        GrowablePlant growable = wrapPlant(world, pos);
        if(growable == null) return null; //Every plant has to be growable.
        IBlockState state = world.getBlockState(growable.getPos());
        if(state.getBlock().equals(Blocks.REEDS) && growable instanceof GrowableReedWrapper) {
            return (GrowableReedWrapper) growable;
        }
        if(state.getBlock().equals(Blocks.CACTUS) && growable instanceof GrowableCactusWrapper) {
            return (GrowableCactusWrapper) growable;
        }
        if(state.getBlock().equals(Blocks.NETHER_WART) && growable instanceof GrowableNetherwartWrapper) {
            return (GrowableNetherwartWrapper) growable;
        }
        if(state.getBlock() instanceof IPlantable) {
            return new HarvestableWrapper(pos);
        }
        return null;
    }

    private static boolean isReedBase(World world, BlockPos pos) {
        return !world.getBlockState(pos.down()).getBlock().equals(Blocks.REEDS);
    }

    private static boolean isCactusBase(World world, BlockPos pos) {
        return !world.getBlockState(pos.down()).getBlock().equals(Blocks.CACTUS);
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

        private final BlockPos pos;

        public HarvestableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(World world) {
            IBlockState at = world.getBlockState(pos);
            if(!(at.getBlock() instanceof IGrowable)) return false;
            return !((IGrowable) at.getBlock()).canGrow(world, pos, at, false);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            if(canHarvest(world)) {
                BlockPos pos = getPos();
                IBlockState at = world.getBlockState(getPos());
                if(at.getBlock() instanceof IPlantable) {
                    drops.addAll(at.getBlock().getDrops(world, pos, at, harvestFortune));
                    world.setBlockToAir(pos);
                    world.setBlockState(pos, ((IPlantable) at.getBlock()).getPlant(world, pos));
                }
            }
            return drops;
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
            if(!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(getPos()))) return true; //We stall until it's loaded.
            HarvestablePlant plant = wrapHarvestablePlant(world, getPos());
            return plant != null && plant instanceof HarvestableWrapper;
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

    public static class GrowableNetherwartWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableNetherwartWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if(!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; //We stall until it's loaded.
            return world.getBlockState(pos).getBlock().equals(Blocks.NETHER_WART);
        }

        @Override
        public boolean canGrow(World world) {
            IBlockState at = world.getBlockState(pos);
            return at.getBlock().equals(Blocks.NETHER_WART) && at.getValue(BlockNetherWart.AGE) < 3;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            if(rand.nextBoolean()) {
                IBlockState current = world.getBlockState(pos);
                world.setBlockState(pos, current.withProperty(BlockNetherWart.AGE, (Math.min(3, current.getValue(BlockNetherWart.AGE) + 1))), 3);
                return true;
            }
            return false;
        }

        @Override
        public boolean canHarvest(World world) {
            IBlockState current = world.getBlockState(pos);
            return current.getBlock().equals(Blocks.NETHER_WART) && current.getValue(BlockNetherWart.AGE) >= 3;
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            IBlockState current = world.getBlockState(pos);
            List<ItemStack> drops = current.getBlock().getDrops(world, pos, current, harvestFortune);
            world.setBlockState(pos, Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, 1), 3);
            return drops;
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

    public static class GrowableCactusWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableCactusWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(World world) {
            return world.getBlockState(pos.up()).getBlock().equals(Blocks.CACTUS);
        }

        @Override
        public boolean isValid(World world, boolean forceChunkLoad) {
            if(!forceChunkLoad && !MiscUtils.isChunkLoaded(world, new ChunkPos(pos))) return true; //We stall until it's loaded.
            return world.getBlockState(pos).getBlock().equals(Blocks.CACTUS);
        }

        @Override
        public List<ItemStack> harvestDropsAndReplant(World world, Random rand, int harvestFortune) {
            List<ItemStack> drops = Lists.newLinkedList();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.up(i);
                IBlockState at = world.getBlockState(bp);
                if(at.getBlock().equals(Blocks.CACTUS)) {
                    MiscUtils.breakBlockWithoutPlayer((WorldServer)  world, bp);
                }
            }
            return drops;
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
                    if(rand.nextBoolean()) {
                        world.setBlockState(cache, Blocks.CACTUS.getDefaultState());
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
            for (int i = 2; i > 0; i--) {
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
                    if(rand.nextBoolean()) {
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
            return at.getBlock() instanceof IGrowable && (((IGrowable) at.getBlock()).canGrow(world, pos, at, false) || (at.getBlock() instanceof BlockStem && !stemHasCrop(world)));
        }

        private boolean stemHasCrop(World world) {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                Block offset = world.getBlockState(pos.offset(enumfacing)).getBlock();
                if(offset.equals(Blocks.MELON_BLOCK) || offset.equals(Blocks.PUMPKIN)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(World world, Random rand) {
            IBlockState at = world.getBlockState(pos);
            if(at.getBlock() instanceof IGrowable) {
                if(((IGrowable) at.getBlock()).canGrow(world, pos, at, false)) {
                    if(!((IGrowable) at.getBlock()).canUseBonemeal(world, rand, pos, at)) {
                        if(world.rand.nextInt(40) != 0) return true; //Returning true to say it could've been potentially grown - So this doesn't invalidate caches.
                    }
                    ((IGrowable) at.getBlock()).grow(world, rand, pos, at);
                    return true;
                }
                if(at.getBlock() instanceof BlockStem) {
                    for (int i = 0; i < 10; i++) {
                        at.getBlock().updateTick(world, pos, at, rand);
                    }
                    return true;
                }
            }
            return false;
        }
    }

}
