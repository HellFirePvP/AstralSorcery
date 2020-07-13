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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
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
        BlockState state;
        LootContext.Builder builder = new LootContext.Builder(world)
                .withParameter(LootParameters.POSITION, pos)
                .withParameter(LootParameters.BLOCK_STATE, (state = world.getBlockState(pos)))
                .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
                .withNullableParameter(LootParameters.BLOCK_ENTITY, world.getTileEntity(pos))
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

    public static boolean isReplaceable(World world, BlockPos pos){
        return isReplaceable(world, pos, world.getBlockState(pos));
    }

    public static boolean isReplaceable(World world, BlockPos pos, BlockState state) {
        if (world.isAirBlock(pos)) {
            return true;
        }
        BlockItemUseContext ctx = TestBlockUseContext.getHandContext(world, null, Hand.MAIN_HAND, pos, Direction.UP);
        return state.isReplaceable(ctx);
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

        for (IProperty<?> prop : state.getProperties()) {
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
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }

        ToolType tool = state.getHarvestTool();
        if (stack.isEmpty() || tool == null) {
            return state.getMaterial().isToolNotRequired() || stack.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, null, state);
        if (toolLevel < 0) {
            return state.getMaterial().isToolNotRequired() || stack.canHarvestBlock(state);
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
        return breakBlockWithoutPlayer(world, pos, world.getBlockState(pos), ItemStack.EMPTY, true, false, true);
    }

    public static boolean breakBlockWithoutPlayer(ServerWorld world, BlockPos pos, BlockState stateBroken, ItemStack heldItem, boolean breakBlock, boolean ignoreHarvestRestrictions, boolean playEffects) {
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

        TileEntity tileentity = world.getTileEntity(pos);

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

        if (playEffects) {
            world.playEvent(null, 2001, pos, Block.getStateId(stateBroken));
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
            world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
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
        world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
        world.capturedBlockSnapshots.clear();

        world.captureBlockSnapshots = prevCaptureFlag;
        world.capturedBlockSnapshots.addAll(prevSnapshots);
    }
}
