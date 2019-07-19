/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid;

import com.google.common.base.Preconditions;
import hellfirepvp.astralsorcery.common.lib.CapabilitiesAS;
import hellfirepvp.astralsorcery.common.util.fluid.handler.CompatBlockWrapper;
import hellfirepvp.astralsorcery.common.util.fluid.handler.CompatFluidBlockWrapper;
import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandler;
import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandlerItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatFluidUtil
 * Created by HellFirePvP
 * Date: 19.07.2019 / 11:46
 */
public class CompatFluidUtil {

    //TODO fill/empty sounds?...

    public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Direction side) {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        return getFluidHandler(world, pos, side).map(handler -> interactWithFluidHandler(player, hand, handler)).orElse(false);
    }

    public static boolean interactWithFluidHandler(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull ICompatFluidHandler handler) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty()) {
            return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .map(playerInventory -> {
                        CompatFluidActionResult fluidActionResult = tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                        if (!fluidActionResult.isSuccess()) {
                            fluidActionResult = tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                        }

                        if (fluidActionResult.isSuccess()) {
                            player.setHeldItem(hand, fluidActionResult.getResult());
                            return true;
                        }
                        return false;
                    })
                    .orElse(false);
        }
        return false;
    }

    @Nonnull
    public static CompatFluidActionResult tryFillContainerAndStow(@Nonnull ItemStack container, ICompatFluidHandler fluidSource, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doFill) {
        if (container.isEmpty()) {
            return CompatFluidActionResult.FAILURE;
        }

        if (player != null && player.abilities.isCreativeMode) {
            CompatFluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess()) {
                return new CompatFluidActionResult(container); // creative mode: item does not change
            }
        } else if (container.getCount() == 1) {// don't need to stow anything, just fill the container stack
            CompatFluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
            if (filledReal.isSuccess()) {
                return filledReal;
            }
        } else {
            CompatFluidActionResult filledSimulated = tryFillContainer(container, fluidSource, maxAmount, player, false);
            if (filledSimulated.isSuccess()) {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, filledSimulated.getResult(), true);
                if (remainder.isEmpty() || player != null) {
                    CompatFluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, doFill);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, filledReal.getResult(), !doFill);

                    // give it to the player or drop it at their feet
                    if (!remainder.isEmpty() && player != null && doFill) {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);
                    return new CompatFluidActionResult(containerCopy);
                }
            }
        }

        return CompatFluidActionResult.FAILURE;
    }

    @Nonnull
    public static CompatFluidActionResult tryEmptyContainerAndStow(@Nonnull ItemStack container, ICompatFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable PlayerEntity player, boolean doDrain) {
        if (container.isEmpty()) {
            return CompatFluidActionResult.FAILURE;
        }

        if (player != null && player.isCreative()) {
            CompatFluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess()) {
                return new CompatFluidActionResult(container); // creative mode: item does not change
            }
        } else if (container.getCount() == 1) {// don't need to stow anything, just fill and edit the container stack=
            CompatFluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
            if (emptiedReal.isSuccess()) {
                return emptiedReal;
            }
        } else {
            CompatFluidActionResult emptiedSimulated = tryEmptyContainer(container, fluidDestination, maxAmount, player, false);
            if (emptiedSimulated.isSuccess()) {
                // check if we can give the itemStack to the inventory
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedSimulated.getResult(), true);
                if (remainder.isEmpty() || player != null) {
                    CompatFluidActionResult emptiedReal = tryEmptyContainer(container, fluidDestination, maxAmount, player, doDrain);
                    remainder = ItemHandlerHelper.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);

                    // give it to the player or drop it at their feet
                    if (!remainder.isEmpty() && player != null && doDrain) {
                        ItemHandlerHelper.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    containerCopy.shrink(1);
                    return new CompatFluidActionResult(containerCopy);
                }
            }
        }

        return CompatFluidActionResult.FAILURE;
    }

    @Nonnull
    public static CompatFluidActionResult tryFillContainer(@Nonnull ItemStack container, ICompatFluidHandler fluidSource, int maxAmount, @Nullable PlayerEntity player, boolean doFill) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
                .map(containerFluidHandler -> {
                    CompatFluidStack simulatedTransfer = tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
                    if (simulatedTransfer != null) {
                        if (doFill) {
                            tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
                        } else {
                            containerFluidHandler.fill(simulatedTransfer, true);
                        }

                        ItemStack resultContainer = containerFluidHandler.getContainer();
                        return new CompatFluidActionResult(resultContainer);
                    }
                    return CompatFluidActionResult.FAILURE;
                })
                .orElse(CompatFluidActionResult.FAILURE);
    }

    @Nonnull
    public static CompatFluidActionResult tryEmptyContainer(@Nonnull ItemStack container, ICompatFluidHandler fluidDestination, int maxAmount, @Nullable PlayerEntity player, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
        return getFluidHandler(containerCopy)
                .map(containerFluidHandler -> {
                    if (doDrain) {
                        CompatFluidStack transfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, true);
                        if (transfer != null) {
                            ItemStack resultContainer = containerFluidHandler.getContainer();
                            return new CompatFluidActionResult(resultContainer);
                        }
                    } else {
                        CompatFluidStack simulatedTransfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, false);
                        if (simulatedTransfer != null) {
                            containerFluidHandler.drain(simulatedTransfer, true);
                            ItemStack resultContainer = containerFluidHandler.getContainer();
                            return new CompatFluidActionResult(resultContainer);
                        }
                    }
                    return CompatFluidActionResult.FAILURE;
                })
                .orElse(CompatFluidActionResult.FAILURE);
    }

    @Nullable
    public static CompatFluidStack tryFluidTransfer(ICompatFluidHandler fluidDestination, ICompatFluidHandler fluidSource, int maxAmount, boolean doTransfer) {
        CompatFluidStack drainable = fluidSource.drain(maxAmount, false);
        if (drainable != null && drainable.getAmount() > 0) {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return null;
    }

    @Nullable
    public static CompatFluidStack tryFluidTransfer(ICompatFluidHandler fluidDestination, ICompatFluidHandler fluidSource, CompatFluidStack resource, boolean doTransfer) {
        CompatFluidStack drainable = fluidSource.drain(resource, false);
        if (drainable != null && drainable.getAmount() > 0 && resource.isFluidEqual(drainable)) {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, doTransfer);
        }
        return null;
    }

    @Nullable
    private static CompatFluidStack tryFluidTransfer_Internal(ICompatFluidHandler fluidDestination, ICompatFluidHandler fluidSource, CompatFluidStack drainable, boolean doTransfer) {
        int fillableAmount = fluidDestination.fill(drainable, false);
        if (fillableAmount > 0) {
            if (doTransfer) {
                CompatFluidStack drained = fluidSource.drain(fillableAmount, true);
                if (drained != null) {
                    drained.setAmount(fluidDestination.fill(drained, true));
                    return drained;
                }
            } else {
                drainable.setAmount(fillableAmount);
                return drainable;
            }
        }
        return null;
    }

    public static LazyOptional<ICompatFluidHandlerItem> getFluidHandler(@Nonnull ItemStack itemStack) {
        return itemStack.getCapability(CapabilitiesAS.FLUID_HANDLER_ITEM_COMPAT);
    }

    public static LazyOptional<CompatFluidStack> getFluidContained(@Nonnull ItemStack container) {
        if (!container.isEmpty()) {
            container = ItemHandlerHelper.copyStackWithSize(container, 1);
            return getFluidHandler(container).map(handler -> handler.drain(Integer.MAX_VALUE, false));
        }
        return LazyOptional.empty();
    }

    public static LazyOptional<ICompatFluidHandler> getFluidHandler(World world, BlockPos blockPos, @Nullable Direction side) {
        BlockState state = world.getBlockState(blockPos);
        Block block = state.getBlock();

        if (block.hasTileEntity(state)) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity != null) {
                return tileEntity.getCapability(CapabilitiesAS.FLUID_HANDLER_COMPAT, side);
            }
        }

        if (block instanceof FlowingFluidBlock) {
            ICompatFluidHandler handle = new CompatFluidBlockWrapper((FlowingFluidBlock) block, world, blockPos);
            return LazyOptional.of(() -> handle);
        }
        return LazyOptional.empty();
    }

    private static ICompatFluidHandler getFluidBlockHandler(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);

        if (blockState.getBlock() instanceof FlowingFluidBlock) {
            return new CompatFluidBlockWrapper((FlowingFluidBlock) blockState.getBlock(), world, pos);
        }
        return new CompatBlockWrapper(blockState.getBlock(), world, pos);
    }

    public static void destroyBlockOnFluidPlacement(World world, BlockPos pos) {
        if (!world.isRemote()) {
            BlockState blockstate = world.getBlockState(pos);
            Material material = blockstate.getMaterial();
            boolean isNotSolid = !material.isSolid();
            boolean isReplaceable = material.isReplaceable();
            if ((isNotSolid || isReplaceable) && !material.isLiquid()) {
                world.destroyBlock(pos, true);
            }
        }
    }

    @Nonnull
    public static ItemStack getFilledBucket(@Nonnull CompatFluidStack fluidStack) {
        Fluid f = fluidStack.getFluid();

        if (f == Fluids.WATER || f == Fluids.FLOWING_WATER) {
            return new ItemStack(Items.WATER_BUCKET);
        }
        if (f == Fluids.LAVA || f == Fluids.FLOWING_LAVA) {
            return new ItemStack(Items.LAVA_BUCKET);
        }
        return ItemStack.EMPTY;
    }

}
