package hellfirepvp.astralsorcery.common.util.dispenser;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidContainerDispenseBehavior
 * Created by HellFirePvP
 * Date: 03.11.2020 / 23:06
 */
//Mostly taken from DispenseFluidContainer, but a variant that actually doesn't crash.
public class FluidContainerDispenseBehavior extends DefaultDispenseItemBehavior {

    private static final FluidContainerDispenseBehavior INSTANCE = new FluidContainerDispenseBehavior();
    private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

    private FluidContainerDispenseBehavior() {}

    public static FluidContainerDispenseBehavior getInstance() {
        return INSTANCE;
    }

    @Override
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        if (FluidUtil.getFluidContained(stack).isPresent()) {
            return dumpContainer(source, stack);
        } else {
            return fillContainer(source, stack);
        }
    }

    @Nonnull
    private ItemStack fillContainer(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        Direction dispenserFacing = source.getBlockState().get(DispenserBlock.FACING);
        BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);

        FluidActionResult actionResult = FluidUtil.tryPickUpFluid(stack, null, world, blockpos, dispenserFacing.getOpposite());
        ItemStack resultStack = actionResult.getResult();

        if (!actionResult.isSuccess() || resultStack.isEmpty()) {
            return super.dispenseStack(source, stack);
        }

        if (stack.getCount() == 1) {
            return resultStack;
        } else if (((DispenserTileEntity)source.getBlockTileEntity()).addItemStack(resultStack) < 0) {
            this.defaultBehavior.dispense(source, resultStack);
        }

        ItemStack stackCopy = stack.copy();
        stackCopy.shrink(1);
        return stackCopy;
    }

    @Nonnull
    private ItemStack dumpContainer(IBlockSource source, @Nonnull ItemStack stack) {
        World world = source.getWorld();
        if (!(world instanceof ServerWorld)) {
            return super.dispenseStack(source, stack);
        }
        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        LazyOptional<IFluidHandlerItem> itemFluidHandler = FluidUtil.getFluidHandler(singleStack);
        if (!itemFluidHandler.isPresent()) {
            return super.dispenseStack(source, stack);
        }
        FluidStack drained = itemFluidHandler
                .map(handler -> handler.drain(FluidAttributes.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE))
                .orElse(FluidStack.EMPTY);
        Direction dispenserFacing = source.getBlockState().get(DispenserBlock.FACING);
        BlockPos pos = source.getBlockPos().offset(dispenserFacing);
        PlayerEntity player = AstralSorcery.getProxy().getASFakePlayerServer((ServerWorld) world);
        FluidActionResult result = FluidUtil.tryPlaceFluid(player, source.getWorld(), Hand.MAIN_HAND, pos, stack, drained);

        if (result.isSuccess()) {
            ItemStack drainedStack = result.getResult();

            if (drainedStack.getCount() == 1) {
                return drainedStack;
            } else if (!drainedStack.isEmpty() && ((DispenserTileEntity) source.getBlockTileEntity()).addItemStack(drainedStack) < 0) {
                this.defaultBehavior.dispense(source, drainedStack);
            }

            ItemStack stackCopy = drainedStack.copy();
            stackCopy.shrink(1);
            return stackCopy;
        } else {
            return this.defaultBehavior.dispense(source, stack);
        }
    }
}
