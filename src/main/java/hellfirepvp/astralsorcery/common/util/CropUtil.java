/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CropUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CropUtil {
    
    private static final int PERFORMED_TICKS = 10;
    
    public static Optional<Plant> wrapPlant(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CropBlock) {
            return Optional.of(new CropPlant(pos));
        }
        if (state.getBlock() instanceof NetherWartBlock) {
            return Optional.of(new NetherWartPlant(pos));
        }
        if (StackableBlockPlant.isSuitableBase(level, pos)) {
            return Optional.of(new StackableBlockPlant(pos));
        }
        if (state.getBlock() instanceof StemBlock) {
            return Optional.of(new StemCropPlant(pos));
        }
        if (state.getBlock() instanceof AttachedStemBlock) {
            return Optional.of(new HarvestableStemCropPlant(pos));
        }
        if (state.getBlock() instanceof SweetBerryBushBlock) {
            return Optional.of(new SweetBerryBushPlant(pos));
        }

        if (state.getBlock() instanceof BonemealableBlock) {
            if (!state.is(BlockTags.DIRT) &&
                    !state.is(Blocks.SHORT_GRASS) &&
                    !state.is(Blocks.TALL_GRASS) &&
                    !(state.getBlock() instanceof DoublePlantBlock)) {
                return Optional.of(new BonemealablePlant(pos));
            }
        }
        return Optional.empty();
    }

    public interface Plant {

        BlockPos getPos();

        default boolean tryGrow(Level level) {
            return this.tryGrow(level, level.getRandom());
        }

        boolean tryGrow(Level level, RandomSource rand);

        boolean canHarvest(Level level);

        default List<ItemStack> getDrops(Level level, int fortuneLevel) {
            if (!(level instanceof ServerLevel sLevel)) return Collections.emptyList();
            return BlockUtil.getDrops(sLevel, this.getPos(), fortuneLevel);
        }

        boolean replant(Level level);

    }
    
    public static abstract class BasicPlant implements Plant {

        private final BlockPos pos;

        public BasicPlant(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public final BlockPos getPos() {
            return this.pos;
        }
    }

    public static class CropPlant extends BasicPlant {
        
        public CropPlant(BlockPos pos) {
            super(pos);
        }
        
        private Optional<CropBlock> getCrop(Level sLevel) {
            return Optional.of(sLevel.getBlockState(this.getPos()))
                    .map(BlockBehaviour.BlockStateBase::getBlock)
                    .filter(block -> block instanceof CropBlock)
                    .map(MiscUtil::cast);
        }

        @Override
        public boolean tryGrow(Level level, RandomSource rand) {
            if (!(level instanceof ServerLevel sLevel)) return false;
            return this.getCrop(level).map(cropBlock -> {
                BlockState state = level.getBlockState(this.getPos());
                if (cropBlock.isMaxAge(state)) return false;

                for (int i = 0; i < PERFORMED_TICKS; i++) {
                    state = level.getBlockState(this.getPos());
                    state.randomTick(sLevel, this.getPos(), rand);
                }
                return true;
            }).orElse(false);
        }

        @Override
        public boolean canHarvest(Level level) {
            return this.getCrop(level).map(cropBlock -> {
                BlockState state = level.getBlockState(this.getPos());
                return cropBlock.isMaxAge(state);
            }).orElse(false);
        }

        @Override
        public boolean replant(Level level) {
            return this.getCrop(level).map(cropBlock -> {
                BlockState state = cropBlock.getStateForAge(0);
                level.setBlock(this.getPos(), state, CropBlock.UPDATE_ALL);
                return true;
            }).orElse(false);
        }
    }

    public static class NetherWartPlant extends BasicPlant {

        public NetherWartPlant(BlockPos pos) {
            super(pos);
        }

        private Optional<NetherWartBlock> getNetherWart(Level sLevel) {
            return Optional.of(sLevel.getBlockState(this.getPos()))
                    .map(BlockBehaviour.BlockStateBase::getBlock)
                    .filter(block -> block instanceof NetherWartBlock)
                    .map(MiscUtil::cast);
        }

        @Override
        public boolean tryGrow(Level level, RandomSource rand) {
            if (!(level instanceof ServerLevel sLevel)) return false;
            return this.getNetherWart(level).map(netherWartBlock -> {
                BlockState state = level.getBlockState(this.getPos());
                if (state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE) return false;

                for (int i = 0; i < PERFORMED_TICKS; i++) {
                    state = level.getBlockState(this.getPos());
                    state.randomTick(sLevel, this.getPos(), rand);
                }
                return true;
            }).orElse(false);
        }

        @Override
        public boolean canHarvest(Level level) {
            return this.getNetherWart(level).map(netherWartBlock -> {
                BlockState state = level.getBlockState(this.getPos());
                return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            }).orElse(false);
        }

        @Override
        public boolean replant(Level level) {
            return this.getNetherWart(level).map(netherWartBlock -> {
                BlockState state = netherWartBlock.defaultBlockState().setValue(NetherWartBlock.AGE, 0);
                level.setBlock(this.getPos(), state, CropBlock.UPDATE_ALL);
                return true;
            }).orElse(false);
        }
    }

    public static class StackableBlockPlant extends BasicPlant {

        public StackableBlockPlant(BlockPos pos) {
            super(pos);
        }

        public static boolean isSuitableBase(Level level, BlockPos pos) {
            if (level.getBlockState(pos).is(Blocks.SUGAR_CANE) &&
                    !level.getBlockState(pos.below()).is(Blocks.SUGAR_CANE)) {
                return true;
            }
            return level.getBlockState(pos).is(Blocks.CACTUS) &&
                    !level.getBlockState(pos.below()).is(Blocks.CACTUS);
        }

        public int getHeight(Level level) {
            int height = 0;
            BlockPos at = this.getPos();
            while (level.getBlockState(at).is(Blocks.SUGAR_CANE) || level.getBlockState(at).is(Blocks.CACTUS)) {
                height++;
                at = at.above();
            }
            return height;
        }

        @Override
        public boolean tryGrow(Level level, RandomSource rand) {
            if (!(level instanceof ServerLevel sLevel)) return false;
            int height = this.getHeight(level);
            if (height >= 3) return false;
            BlockPos topPos = this.getPos().above(height - 1);
            for (int i = 0; i < PERFORMED_TICKS; i++) {
                BlockState state = level.getBlockState(topPos);
                state.randomTick(sLevel, topPos, rand);
            }
            return true;
        }

        @Override
        public boolean canHarvest(Level level) {
            return this.getHeight(level) > 2;
        }

        @Override
        public List<ItemStack> getDrops(Level level, int fortuneLevel) {
            if (!(level instanceof ServerLevel sLevel)) return Collections.emptyList();
            int height = this.getHeight(level) - 1;
            if (height <= 0) return Collections.emptyList();
            List<ItemStack> drops = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                drops.addAll(BlockUtil.getDrops(sLevel, this.getPos(), fortuneLevel));
            }
            return drops;
        }

        @Override
        public boolean replant(Level level) {
            int height = this.getHeight(level) - 1;
            for (int i = height; i > 0; i--) {
                level.setBlockAndUpdate(this.getPos().above(i), Blocks.AIR.defaultBlockState());
            }
            return true;
        }
    }

    public static class BonemealablePlant extends BasicPlant {

        public BonemealablePlant(BlockPos pos) {
            super(pos);
        }

        private Optional<BonemealableBlock> getBonemealable(Level sLevel) {
            return Optional.of(sLevel.getBlockState(this.getPos()))
                    .map(BlockBehaviour.BlockStateBase::getBlock)
                    .filter(block -> block instanceof BonemealableBlock)
                    .map(MiscUtil::cast);
        }

        @Override
        public boolean tryGrow(Level level, RandomSource rand) {
            if (!(level instanceof ServerLevel sLevel)) return false;
            return this.getBonemealable(sLevel).map(bonemealableBlock -> {
                BlockState state = level.getBlockState(this.getPos());
                BonemealEvent event = EventHooks.fireBonemealEvent(null, sLevel, this.getPos(), state, new ItemStack(Items.BONE_MEAL));
                if (event.isCanceled()) return event.isSuccessful();
                if (!event.isValidBonemealTarget() || !bonemealableBlock.isBonemealSuccess(sLevel, rand, this.getPos(), state)) return false;
                bonemealableBlock.performBonemeal(sLevel, rand, this.getPos(), state);
                return true;
            }).orElse(false);
        }

        @Override
        public boolean canHarvest(Level level) {
            return false;
        }

        @Override
        public boolean replant(Level level) {
            return false;
        }
    }

    public static class StemCropPlant extends BonemealablePlant {

        public StemCropPlant(BlockPos pos) {
            super(pos);
        }

        private Optional<StemBlock> getStem(Level level) {
            return Optional.of(level.getBlockState(this.getPos()))
                    .map(BlockBehaviour.BlockStateBase::getBlock)
                    .filter(block -> block instanceof StemBlock)
                    .map(MiscUtil::cast);
        }

        private Optional<BlockPos> tryFindFruit(Level level) {
            return this.getStem(level).map(stemBlock -> {
                ResourceKey<Block> fruit = stemBlock.fruit;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos checkPos = this.getPos().relative(dir);
                    if (level.getBlockState(checkPos).is(fruit)) {
                        return checkPos;
                    }
                }
                return null;
            });
        }

        @Override
        public boolean tryGrow(Level level, RandomSource rand) {
            if (!(level instanceof ServerLevel sLevel)) return false;
            boolean bonemealGrow = super.tryGrow(sLevel, rand);
            if (bonemealGrow) return true;
            Optional<BlockPos> fruitPos = this.tryFindFruit(sLevel);
            if (fruitPos.isPresent()) return false;

            BlockState stemState = sLevel.getBlockState(this.getPos());
            for (int i = 0; i < PERFORMED_TICKS; i++) {
                stemState.randomTick(sLevel, this.getPos(), rand);
            }
            return true;
        }
    }

    public static class HarvestableStemCropPlant extends BasicPlant {

        public HarvestableStemCropPlant(BlockPos pos) {
            super(pos);
        }

        private Optional<AttachedStemBlock> getAttachedStemBlock(Level level) {
            return Optional.of(level.getBlockState(this.getPos()))
                    .map(BlockBehaviour.BlockStateBase::getBlock)
                    .filter(block -> block instanceof AttachedStemBlock)
                    .map(MiscUtil::cast);
        }

        private Optional<BlockPos> tryFindFruit(Level level) {
            return this.getAttachedStemBlock(level).map(attachedStemBlock -> {
                ResourceKey<Block> fruit = attachedStemBlock.fruit;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos checkPos = this.getPos().relative(dir);
                    if (level.getBlockState(checkPos).is(fruit)) {
                        return checkPos;
                    }
                }
                return null;
            });
        }

        @Override
        public boolean tryGrow(Level level, RandomSource rand) {
            return false;
        }

        @Override
        public boolean canHarvest(Level level) {
            return this.tryFindFruit(level).isPresent();
        }

        @Override
        public List<ItemStack> getDrops(Level level, int fortuneLevel) {
            if (!(level instanceof ServerLevel sLevel)) return Collections.emptyList();
            return this.tryFindFruit(level)
                    .map(fruitPos -> BlockUtil.getDrops(sLevel, fruitPos, fortuneLevel))
                    .orElse(Collections.emptyList());
        }

        @Override
        public boolean replant(Level level) {
            return this.tryFindFruit(level).map(fruitPos -> {
                level.setBlockAndUpdate(fruitPos, Blocks.AIR.defaultBlockState());
                return true;
            }).orElse(false);
        }
    }

    public static class SweetBerryBushPlant extends BasicPlant {

        public SweetBerryBushPlant(BlockPos pos) {
            super(pos);
        }
        private Optional<SweetBerryBushBlock> getBush(Level level) {
            return Optional.of(level.getBlockState(this.getPos()))
                    .map(BlockBehaviour.BlockStateBase::getBlock)
                    .filter(block -> block instanceof SweetBerryBushBlock)
                    .map(MiscUtil::cast);
        }

        @Override
        public boolean tryGrow(Level level, RandomSource rand) {
            if (!(level instanceof ServerLevel sLevel)) return false;
            return this.getBush(level).map(bushBlock -> {
                BlockState state = level.getBlockState(this.getPos());
                if (state.getValue(SweetBerryBushBlock.AGE) >= SweetBerryBushBlock.MAX_AGE) return false;

                for (int i = 0; i < PERFORMED_TICKS; i++) {
                    state = level.getBlockState(this.getPos());
                    state.randomTick(sLevel, this.getPos(), rand);
                }
                return true;
            }).orElse(false);
        }

        @Override
        public boolean canHarvest(Level level) {
            return this.getBush(level).map(bushBlock -> {
                BlockState state = level.getBlockState(this.getPos());
                return state.getValue(SweetBerryBushBlock.AGE) >= SweetBerryBushBlock.MAX_AGE;
            }).orElse(false);
        }

        @Override
        public boolean replant(Level level) {
            return this.getBush(level).map(bushBlock -> {
                BlockState state = bushBlock.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 1);
                level.setBlock(this.getPos(), state, Block.UPDATE_ALL);
                return true;
            }).orElse(false);
        }
    }
}
