/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.constellation.effect.base.CEffectAbstractList;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CropHelper
 * Created by HellFirePvP
 * Date: 11.06.2019 / 21:05
 */
public class CropHelper {

    public static final String GROWABLE = "growable";
    public static final String GROWABLE_REED = "growable_reed";
    public static final String GROWABLE_CACTUS = "growable_cactus";
    public static final String GROWABLE_NETHERWART = "growable_netherwart";
    public static final String HARVESTABLE = "harvestable";

    public static Map<String, Function<BlockPos, GrowablePlant>> growableFactoryWrapper = new HashMap<String, Function<BlockPos, GrowablePlant>>() {
        {
            put(GROWABLE, GrowableWrapper::new);
            put(GROWABLE_REED, GrowableReedWrapper::new);
            put(GROWABLE_CACTUS, GrowableCactusWrapper::new);
            put(GROWABLE_NETHERWART, GrowableNetherwartWrapper::new);
            put(HARVESTABLE, HarvestableWrapper::new);
        }
    };

    @Nullable
    public static GrowablePlant fromNBT(CompoundNBT nbt, BlockPos pos) {
        return growableFactoryWrapper.getOrDefault(nbt.getString("identifier"), (p) -> null).apply(pos);
    }

    @Nullable
    public static GrowablePlant wrapPlant(IWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block b = state.getBlock();
        if (state.getBlock() instanceof IGrowable) {
            if (b instanceof GrassBlock) return null;
            if (b instanceof TallGrassBlock) return null;
            if (b instanceof DoublePlantBlock) return null;
            return new GrowableWrapper(pos);
        }
        if (state.getBlock().equals(Blocks.SUGAR_CANE)) {
            if (isReedBase(world, pos)) {
                return new GrowableReedWrapper(pos);
            }
        }
        if (state.getBlock().equals(Blocks.CACTUS)) {
            if (isCactusBase(world, pos)) {
                return new GrowableCactusWrapper(pos);
            }
        }
        if (state.getBlock().equals(Blocks.NETHER_WART)) {
            return new GrowableNetherwartWrapper(pos);
        }
        return null;
    }

    @Nullable
    public static HarvestablePlant wrapHarvestablePlant(IWorld world, BlockPos pos) {
        GrowablePlant growable = wrapPlant(world, pos);
        if (growable == null) return null; //Every plant has to be growable.
        BlockState state = world.getBlockState(growable.getPos());
        if (state.getBlock().equals(Blocks.SUGAR_CANE) && growable instanceof GrowableReedWrapper) {
            return (GrowableReedWrapper) growable;
        }
        if (state.getBlock().equals(Blocks.CACTUS) && growable instanceof GrowableCactusWrapper) {
            return (GrowableCactusWrapper) growable;
        }
        if (state.getBlock().equals(Blocks.NETHER_WART) && growable instanceof GrowableNetherwartWrapper) {
            return (GrowableNetherwartWrapper) growable;
        }
        if (state.getBlock() instanceof IPlantable) {
            return new HarvestableWrapper(pos);
        }
        return null;
    }

    private static boolean isReedBase(IWorld world, BlockPos pos) {
        return !world.getBlockState(pos.down()).getBlock().equals(Blocks.SUGAR_CANE);
    }

    private static boolean isCactusBase(IWorld world, BlockPos pos) {
        return !world.getBlockState(pos.down()).getBlock().equals(Blocks.CACTUS);
    }

    public static interface GrowablePlant extends CEffectAbstractList.ListEntry {

        public String getIdentifier();

        public boolean isValid(IWorld world);

        public boolean canGrow(IWorld world);

        public boolean tryGrow(IWorld world, Random rand);

        @Override
        default void readFromNBT(CompoundNBT nbt) {}

        @Override
        default void writeToNBT(CompoundNBT nbt) {
            nbt.putString("identifier", this.getIdentifier());
        }
    }

    public static interface HarvestablePlant extends GrowablePlant {

        public boolean canHarvest(IWorld world);

        public NonNullList<ItemStack> harvestDropsAndReplant(ServerWorld world, Random rand, int harvestFortune);

    }

    public static class HarvestableWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public HarvestableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(IWorld world) {
            BlockState at = world.getBlockState(pos);
            if (!(at.getBlock() instanceof IGrowable)) return false;
            return !((IGrowable) at.getBlock()).canGrow(world, pos, at, false);
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerWorld world, Random rand, int harvestFortune) {
            NonNullList<ItemStack> drops = NonNullList.create();
            if (canHarvest(world)) {
                BlockPos pos = getPos();
                BlockState at = world.getBlockState(getPos());
                if (at.getBlock() instanceof IPlantable) {
                    drops.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
                    world.removeBlock(pos, false);
                    world.setBlockState(pos, ((IPlantable) at.getBlock()).getPlant(world, pos));
                }
            }
            return drops;
        }

        @Override
        public String getIdentifier() {
            return HARVESTABLE;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public boolean isValid(IWorld world) {
            return wrapHarvestablePlant(world, getPos()) instanceof HarvestableWrapper;
        }

        @Override
        public boolean canGrow(IWorld world) {
            BlockState at = world.getBlockState(pos);
            return at.getBlock() instanceof IGrowable && ((IGrowable) at.getBlock()).canGrow(world, pos, at, false);
        }

        @Override
        public boolean tryGrow(IWorld world, Random rand) {
            BlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof IGrowable && world instanceof ServerWorld) {
                if (((IGrowable) at.getBlock()).canGrow(world, pos, at, false)) {
                    ((IGrowable) at.getBlock()).grow((ServerWorld) world, rand, pos, at);
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
        public boolean isValid(IWorld world) {
            return world.getBlockState(pos).getBlock().equals(Blocks.NETHER_WART);
        }

        @Override
        public boolean canGrow(IWorld world) {
            BlockState at = world.getBlockState(pos);
            return at.getBlock().equals(Blocks.NETHER_WART) && at.get(NetherWartBlock.AGE) < 3;
        }

        @Override
        public boolean tryGrow(IWorld world, Random rand) {
            if (rand.nextBoolean()) {
                BlockState current = world.getBlockState(pos);
                return world.setBlockState(pos, current.with(NetherWartBlock.AGE, (Math.min(3, current.get(NetherWartBlock.AGE) + 1))), 3);
            }
            return false;
        }

        @Override
        public boolean canHarvest(IWorld world) {
            BlockState current = world.getBlockState(pos);
            return current.getBlock().equals(Blocks.NETHER_WART) && current.get(NetherWartBlock.AGE) >= 3;
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerWorld world, Random rand, int harvestFortune) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            stacks.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
            world.setBlockState(pos, Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 1), 3);
            return stacks;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE_NETHERWART;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

    }

    public static class GrowableCactusWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableCactusWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(IWorld world) {
            return world.getBlockState(pos.up()).getBlock().equals(Blocks.CACTUS);
        }

        @Override
        public boolean isValid(IWorld world) {
            return world.getBlockState(pos).getBlock().equals(Blocks.CACTUS);
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerWorld world, Random rand, int harvestFortune) {
            NonNullList<ItemStack> drops = NonNullList.create();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.up(i);
                BlockState at = world.getBlockState(bp);
                if (at.getBlock().equals(Blocks.CACTUS)) {
                    BlockUtils.breakBlockWithoutPlayer(world, bp);
                }
            }
            return drops;
        }

        @Override
        public boolean canGrow(IWorld world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(IWorld world, Random rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache)) {
                    if (rand.nextBoolean()) {
                        return world.setBlockState(cache, Blocks.CACTUS.getDefaultState(), Constants.BlockFlags.DEFAULT);
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE_CACTUS;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }
    }

    public static class GrowableReedWrapper implements HarvestablePlant {

        private final BlockPos pos;

        public GrowableReedWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public boolean canHarvest(IWorld world) {
            return world.getBlockState(pos.up()).getBlock().equals(Blocks.SUGAR_CANE);
        }

        @Override
        public NonNullList<ItemStack> harvestDropsAndReplant(ServerWorld world, Random rand, int harvestFortune) {
            NonNullList<ItemStack> drops = NonNullList.create();
            for (int i = 2; i > 0; i--) {
                BlockPos bp = pos.up(i);
                BlockState at = world.getBlockState(bp);
                if (at.getBlock().equals(Blocks.SUGAR_CANE)) {
                    drops.addAll(BlockUtils.getDrops(world, pos, harvestFortune, rand));
                    world.removeBlock(bp, false);
                }
            }
            return drops;
        }

        @Override
        public boolean isValid(IWorld world) {
            return world.getBlockState(pos).getBlock().equals(Blocks.SUGAR_CANE);
        }

        @Override
        public boolean canGrow(IWorld world) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(IWorld world, Random rand) {
            BlockPos cache = pos;
            for (int i = 1; i < 3; i++) {
                cache = cache.up();
                if (world.isAirBlock(cache)) {
                    if (rand.nextBoolean()) {
                        return world.setBlockState(cache, Blocks.SUGAR_CANE.getDefaultState(), Constants.BlockFlags.DEFAULT);
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE_REED;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

    }

    public static class GrowableWrapper implements GrowablePlant {

        private final BlockPos pos;

        public GrowableWrapper(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public String getIdentifier() {
            return GROWABLE;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public boolean isValid(IWorld world) {
            return wrapPlant(world, pos) instanceof GrowableWrapper;
        }

        @Override
        public boolean canGrow(IWorld world) {
            BlockState at = world.getBlockState(pos);
            return at.getBlock() instanceof IGrowable && (
                    ((IGrowable) at.getBlock()).canGrow(world, pos, at, false) ||
                            (at.getBlock() instanceof StemBlock && !stemHasCrop(world))
            );
        }

        private boolean stemHasCrop(IWorld world) {
            for (Direction enumfacing : Direction.Plane.HORIZONTAL) {
                Block offset = world.getBlockState(pos.offset(enumfacing)).getBlock();
                if (offset.equals(Blocks.MELON) || offset.equals(Blocks.PUMPKIN)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean tryGrow(IWorld world, Random rand) {
            BlockState at = world.getBlockState(pos);
            if (at.getBlock() instanceof IGrowable && world instanceof ServerWorld) {
                if (((IGrowable) at.getBlock()).canGrow(world, pos, at, false)) {
                    if (!((IGrowable) at.getBlock()).canUseBonemeal((World) world, rand, pos, at)) {
                        if (rand.nextInt(20) != 0) {
                            return true; //Returning true to say it could've been potentially grown - So this doesn't invalidate caches.
                        }
                    }
                    ((IGrowable) at.getBlock()).grow((ServerWorld) world, rand, pos, at);
                    return true;
                }
                if (at.getBlock() instanceof StemBlock) {
                    for (int i = 0; i < 10; i++) {
                        at.tick((ServerWorld) world, pos, rand);
                    }
                    return true;
                }
            }
            return false;
        }
    }

}