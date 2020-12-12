/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.state.Property;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockUtils
 * Created by HellFirePvP
 * Date: 30.05.2019 / 15:50
 */
public class BlockUtils {

    @Nonnull
    public static List<ItemStack> getDrops(ServerWorld world, BlockPos pos, int harvestFortune, Random rand) {
        return getDrops(world, pos, harvestFortune, rand, ItemStack.EMPTY);
    }

    @Nonnull
    public static List<ItemStack> getDrops(ServerWorld world, BlockPos pos, int harvestFortune, Random rand, ItemStack tool) {
        return getDrops(world, pos, world.getBlockState(pos), harvestFortune, rand, tool);
    }

    @Nonnull
    public static List<ItemStack> getDrops(ServerWorld world, BlockPos pos, BlockState state, int harvestFortune, Random rand, ItemStack tool) {
        LootContext.Builder builder = new LootContext.Builder(world)
                .withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos))
                .withParameter(LootParameters.BLOCK_STATE, state)
                .withParameter(LootParameters.TOOL, tool)
                .withNullableParameter(LootParameters.BLOCK_ENTITY, MiscUtils.getTileAt(world, pos, TileEntity.class, true))
                .withRandom(rand)
                .withLuck(harvestFortune);
        return state.getDrops(builder);
    }

    @Nonnull
    public static BlockPos getWorldTopPos(BlockPos at) {
        BlockPos it = at;
        while (!World.isOutsideBuildHeight(it)) {
            it = it.up();
        }
        return it;
    }

    public static BlockPos firstSolidDown(IBlockReader world, BlockPos at) {
        BlockState state = world.getBlockState(at);
        while (at.getY() > 0 && !state.getMaterial().blocksMovement() && state.getFluidState().isEmpty()) {
            at = at.down();
            state = world.getBlockState(at);
        }
        return at;
    }

    public static boolean isReplaceable(World world, BlockPos pos) {
        return isReplaceable(world, pos, world.getBlockState(pos));
    }

    public static boolean isReplaceable(World world, BlockPos pos, BlockState state) {
        if (world.isAirBlock(pos)) {
            return true;
        }
        BlockItemUseContext ctx = TestBlockUseContext.getHandContext(world, null, Hand.MAIN_HAND, pos, Direction.UP);
        return state.isReplaceable(ctx);
    }

    //Same as PlayerEntity#getDigSpeed, but without firing an event and not position-based
    public static float getSimpleBreakSpeed(LivingEntity entity, ItemStack tool, BlockState state) {
        float breakSpeed = tool.getDestroySpeed(state);
        if (breakSpeed > 1.0F) {
            float efficiencyLevel = EnchantmentHelper.getEfficiencyModifier(entity);
            if (efficiencyLevel > 0 && !tool.isEmpty()) {
                breakSpeed += efficiencyLevel * efficiencyLevel + 1;
            }
        }

        if (EffectUtils.hasMiningSpeedup(entity)) {
            breakSpeed *= 1.0F + (EffectUtils.getMiningSpeedup(entity) + 1F) * 0.2F;
        }

        if (entity.isPotionActive(Effects.MINING_FATIGUE)) {
            float fatigueMultiplier;
            switch (entity.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    fatigueMultiplier = (float) Math.pow(0.3F, 1);
                    break;
                case 1:
                    fatigueMultiplier = (float) Math.pow(0.3F, 2);
                    break;
                case 2:
                    fatigueMultiplier = (float) Math.pow(0.3F, 3);
                    break;
                case 3:
                default:
                    fatigueMultiplier = (float) Math.pow(0.3F, 4);
            }

            breakSpeed *= fatigueMultiplier;
        }

        if (entity.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity)) {
            breakSpeed /= 5.0F;
        }

        if (!entity.isOnGround()) {
            breakSpeed /= 5.0F;
        }
        return breakSpeed;
    }

    public static boolean isFluidBlock(World world, BlockPos pos) {
        return isFluidBlock(world.getBlockState(pos));
    }

    public static boolean isFluidBlock(BlockState state) {
        return state == state.getFluidState().getBlockState();
    }

    @Nullable
    public static BlockState getMatchingState(Collection<BlockState> applicableStates, @Nullable BlockState test) {
        for (BlockState state : applicableStates) {
            if (matchStateExact(state, test)) {
                return state;
            }
        }
        return null;
    }

    public static boolean matchStateExact(@Nullable BlockState state, @Nullable BlockState stateToTest) {
        if (state == null) {
            return stateToTest == null;
        } else if (stateToTest == null) {
            return false;
        }

        if (!state.getBlock().getRegistryName().equals(stateToTest.getBlock().getRegistryName())) {
            return false;
        }

        for (Property<?> prop : state.getProperties()) {
            Comparable<?> original = state.get(prop);
            try {
                Comparable<?> test = stateToTest.get(prop);
                if (!original.equals(test)) {
                    return false;
                }
            } catch (Exception exc) {
                return false;
            }
        }
        return true;
    }

    public static boolean canToolBreakBlockWithoutPlayer(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ItemStack stack) {
        if (state.getBlockHardness(world, pos) == -1) {
            return false;
        }
        if (!state.getRequiresTool()) {
            return true;
        }

        ToolType tool = state.getHarvestTool();
        if (stack.isEmpty() || tool == null) {
            return !state.getRequiresTool() || stack.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, null, state);
        if (toolLevel < 0) {
            return!state.getRequiresTool() || stack.canHarvestBlock(state);
        }

        return toolLevel >= state.getHarvestLevel();
    }

    public static boolean breakBlockWithPlayer(BlockPos pos, ServerPlayerEntity playerMP) {
        return playerMP.interactionManager.tryHarvestBlock(pos);
    }

    //Copied from ForgeHooks.onBlockBreak & PlayerInteractionManager.tryHarvestBlock
    //Duplicate break functionality without a active player.
    //Emulates a FakePlayer - attempts without a player as harvester in case a fakeplayer leads to issues.
    public static boolean breakBlockWithoutPlayer(ServerWorld world, BlockPos pos) {
        return breakBlockWithoutPlayer(world, pos, world.getBlockState(pos), ItemStack.EMPTY, true, false);
    }

    @Deprecated
    public static boolean breakBlockWithoutPlayer(ServerWorld world, BlockPos pos, BlockState stateBroken, ItemStack heldItem, boolean breakBlock, boolean ignoreHarvestRestrictions, boolean playEffects) {
        return breakBlockWithoutPlayer(world, pos, stateBroken, heldItem, breakBlock, ignoreHarvestRestrictions);
    }

    public static boolean breakBlockWithoutPlayer(ServerWorld world, BlockPos pos, BlockState stateBroken, ItemStack heldItem, boolean breakBlock, boolean ignoreHarvestRestrictions) {
        FakePlayer fakePlayer = AstralSorcery.getProxy().getASFakePlayerServer(world);
        int xp;
        try {
            boolean preCancelEvent = false;
            if (!heldItem.isEmpty() && !heldItem.getItem().canPlayerBreakBlockWhileHolding(stateBroken, world, pos, fakePlayer)) {
                preCancelEvent = true;
            }
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, stateBroken, fakePlayer);
            event.setCanceled(preCancelEvent);
            MinecraftForge.EVENT_BUS.post(event);

            if (event.isCanceled()) {
                return false;
            }
            xp = event.getExpToDrop();
        } catch (Exception exc) {
            return false;
        }
        if (xp == -1) {
            return false;
        }

        if (heldItem.onBlockStartBreak(pos, fakePlayer)) {
            return false;
        }

        boolean harvestable = true;
        try {
            if (!ignoreHarvestRestrictions) {
                harvestable = stateBroken.canHarvestBlock(world, pos, fakePlayer);
            }
        } catch (Exception exc) {
            return false;
        }

        ItemStack heldCopy = heldItem.isEmpty() ? ItemStack.EMPTY : heldItem.copy();
        try {
            heldCopy.onBlockDestroyed(world, stateBroken, pos, fakePlayer);
        } catch (Exception exc) {
            return false;
        }

        boolean wasCapturingStates = world.captureBlockSnapshots;
        List<BlockSnapshot> previousCapturedStates = new ArrayList<>(world.capturedBlockSnapshots);

        world.captureBlockSnapshots = true;
        try {
            if (breakBlock) {
                if (!stateBroken.removedByPlayer(world, pos, fakePlayer, harvestable, Fluids.EMPTY.getDefaultState())) {
                    restoreWorldState(world, wasCapturingStates, previousCapturedStates);
                    return false;
                }
            } else {
                stateBroken.getBlock().onBlockHarvested(world, pos, stateBroken, fakePlayer);
            }
        } catch (Exception exc) {
            restoreWorldState(world, wasCapturingStates, previousCapturedStates);
            return false;
        }

        stateBroken.getBlock().onPlayerDestroy(world, pos, stateBroken);

        if (harvestable) {
            try {
                TileEntity tileentity = MiscUtils.getTileAt(world, pos, TileEntity.class, true);
                ItemStack harvestStack = heldCopy.isEmpty() ? ItemStack.EMPTY : heldCopy.copy();
                stateBroken.getBlock().harvestBlock(world, fakePlayer, pos, stateBroken, tileentity, harvestStack);
            } catch (Exception exc) {
                restoreWorldState(world, wasCapturingStates, previousCapturedStates);
                return false;
            }
        }

        if (xp > 0) {
            stateBroken.getBlock().dropXpOnBlockBreak(world, pos, xp);
        }
        BlockDropCaptureAssist.startCapturing();
        try {
            //Capturing block snapshots is aids. don't try that at home kids.
            world.captureBlockSnapshots = false;
            world.restoringBlockSnapshots = true;
            world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
            world.restoringBlockSnapshots = false;
            world.capturedBlockSnapshots.forEach((s) -> world.setBlockState(s.getPos(), Blocks.AIR.getDefaultState()));
        } finally {
            BlockDropCaptureAssist.getCapturedStacksAndStop(); //Discard

            //Restore previous state
            world.capturedBlockSnapshots.clear();
            world.captureBlockSnapshots = wasCapturingStates;
            world.capturedBlockSnapshots.addAll(previousCapturedStates);
        }
        return true;
    }

    private static void restoreWorldState(World world, boolean prevCaptureFlag, List<BlockSnapshot> prevSnapshots) {
        world.captureBlockSnapshots = false;

        world.restoringBlockSnapshots = true;
        world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
        world.restoringBlockSnapshots = false;

        world.capturedBlockSnapshots.clear();

        world.captureBlockSnapshots = prevCaptureFlag;
        world.capturedBlockSnapshots.addAll(prevSnapshots);
    }
}
