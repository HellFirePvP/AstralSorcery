/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesGlass;
import hellfirepvp.astralsorcery.common.item.ItemAquamarine;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.observerlib.api.block.BlockStructureObserver;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialGateway
 * Created by HellFirePvP
 * Date: 10.09.2020 / 16:46
 */
public class BlockCelestialGateway extends ContainerBlock implements CustomItemBlock, BlockStructureObserver {

    private static final VoxelShape SHAPE = VoxelShapes.create(1D / 16D, 0D / 16D, 1D / 16D, 15D / 16D, 1D / 16D, 15D / 16D);

    public BlockCelestialGateway() {
        super(PropertiesGlass.coatedGlass()
                .lightValue(12)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        DyeColor color = getColor(stack);
        if (color != null) {
            tooltip.add(ColorUtils.getTranslation(color).applyTextStyle(ColorUtils.textFormattingForDye(color)));
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack stack = new ItemStack(BlocksAS.GATEWAY);
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null) {
            if (gateway.hasCustomName()) {
                stack.setDisplayName(gateway.getDisplayName());
            }
            gateway.getColor().ifPresent(color -> setColor(stack, color));
        }
        return stack;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, false);
        if (gateway != null &&
                gateway.getOwner() != null &&
                gateway.getOwner().isPlayer(player)) {

            if (gateway.isLocked()) {
                if (!world.isRemote()) {
                    ItemStack remaining = ItemUtils.dropItemToPlayer(player, new ItemStack(ItemsAS.AQUAMARINE));
                    if (!remaining.isEmpty()) {
                        ItemUtils.dropItemNaturally(world, player.getPosX(), player.getPosY(), player.getPosZ(), remaining);
                    }
                    gateway.unlock();
                }
                return ActionResultType.SUCCESS;
            } else {
                ItemStack held = player.getHeldItem(hand);
                if (held.getItem() instanceof ItemAquamarine) {
                    if (!world.isRemote()) {
                        held.shrink(1);
                        gateway.lock();
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null) {
            if (stack.hasDisplayName()) {
                gateway.setDisplayText(stack.getDisplayName());
            }
            DyeColor color = getColor(stack);
            if (color != null) {
                gateway.setColor(color);
            }
        }
    }

    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null && gateway.isLocked() && gateway.getOwner() != null) {
            return gateway.getOwner().isPlayer(player) ? this.blockHardness : -1F;
        }
        return this.blockHardness;
    }

    @Override
    public float getBlockHardness(BlockState blockState, IBlockReader world, BlockPos pos) {
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
        if (gateway != null && gateway.isLocked() && gateway.getOwner() != null) {
            //Assume this is non-player related hardness. In which case, it cannot be mined to begin with.
            return -1;
        }
        return this.blockHardness;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moving) {
        if (state != newState && !world.isRemote()) {
            DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE).removePosition(world, pos);
            TileCelestialGateway gateway = MiscUtils.getTileAt(world, pos, TileCelestialGateway.class, true);
            if (gateway != null && gateway.isLocked()) {
                ItemUtils.dropItemNaturally(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemsAS.AQUAMARINE));
            }
        }

        super.onReplaced(state, world, pos, newState, moving);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction placedAgainst, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        if (!this.isValidPosition(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return hasSolidSideOnTop(world, pos.down());
    }

    @Nullable
    public static DyeColor getColor(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem) ||
                !(((BlockItem) stack.getItem()).getBlock() instanceof BlockCelestialGateway)) {
            return null;
        }

        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (!tag.contains("color")) {
            return null;
        }
        return NBTHelper.readEnum(tag, "color", DyeColor.class);
    }

    public static void setColor(ItemStack stack, @Nullable DyeColor color) {
        if (!(stack.getItem() instanceof BlockItem) ||
                !(((BlockItem) stack.getItem()).getBlock() instanceof BlockCelestialGateway)) {
            return;
        }

        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (color == null) {
            tag.remove("color");
        } else {
            NBTHelper.writeEnum(tag, "color", color);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileCelestialGateway();
    }
}
