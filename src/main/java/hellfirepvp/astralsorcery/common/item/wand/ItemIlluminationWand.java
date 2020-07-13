/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.block.tile.BlockFlareLight;
import hellfirepvp.astralsorcery.common.block.tile.BlockTranslucentBlock;
import hellfirepvp.astralsorcery.common.item.base.AlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.tile.TileTranslucentBlock;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import hellfirepvp.astralsorcery.common.util.sound.SoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationWand
 * Created by HellFirePvP
 * Date: 28.11.2019 / 20:57
 */
public class ItemIlluminationWand extends Item implements ItemDynamicColor, AlignmentChargeConsumer {

    private static final float COST_PER_ILLUMINATION = 650F;
    private static final float COST_PER_FLARE = 300F;

    public ItemIlluminationWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(CommonProxy.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        DyeColor color = getConfiguredColor(stack);
        tooltip.add(ColorUtils.getTranslation(color).setStyle(new Style().setColor(ColorUtils.textFormattingForDye(color))));
    }

    @Override
    public float getAlignmentChargeCost(PlayerEntity player, ItemStack stack) {
        if (player.isSneaking()) {
            return COST_PER_ILLUMINATION;
        } else {
            return COST_PER_FLARE;
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        Direction dir = context.getFace();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItem();

        if (world.isRemote() || player == null || stack.isEmpty() || !(stack.getItem() instanceof ItemIlluminationWand)) {
            return ActionResultType.SUCCESS;
        }

        BlockState state = world.getBlockState(pos);

        if (player.isSneaking()) {
            if (state.getBlock() instanceof BlockTranslucentBlock) {
                TileTranslucentBlock tb = MiscUtils.getTileAt(world, pos, TileTranslucentBlock.class, true);
                if (tb != null && (tb.getPlayerUUID() == null || tb.getPlayerUUID().equals(player.getUniqueID()))) {
                    SoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_UNHIGHLIGHT, SoundCategory.BLOCKS, world, pos, 1F, 0.9F + random.nextFloat() * 0.2F);
                    world.setBlockState(pos, tb.getFakedState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
            } else {
                TileEntity tile = MiscUtils.getTileAt(world, pos, TileEntity.class, true);
                if (tile == null &&
                        !state.hasTileEntity() &&
                        player.canPlayerEdit(pos, dir, stack) &&
                        VoxelShapes.fullCube().equals(world.getBlockState(pos).getShape(world, pos))) {
                    if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_ILLUMINATION, false)) {
                        if (world.setBlockState(pos, BlocksAS.TRANSLUCENT_BLOCK.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
                            SoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_HIGHLIGHT, SoundCategory.BLOCKS, world, pos, 1F, 0.9F + random.nextFloat() * 0.2F);
                            TileTranslucentBlock tb = MiscUtils.getTileAt(world, pos, TileTranslucentBlock.class, true);
                            if (tb != null) {
                                tb.setFakedState(state);
                                tb.setOverlayColor(getConfiguredColor(stack));
                                tb.setPlayerUUID(player.getUniqueID());
                            } else {
                                //Abort, we didn't get a tileentity... for some reason.
                                world.setBlockState(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
                            }
                        }
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }

        TileIlluminator illum = MiscUtils.getTileAt(world, pos, TileIlluminator.class, true);
        if (illum != null) {
            illum.onWandUsed(stack);
            SoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_LIGHT, SoundCategory.BLOCKS, world, pos, 1F, 1F);
            return ActionResultType.SUCCESS;
        }

        ISelectionContext selContext = ISelectionContext.forEntity(player);
        BlockPos placePos = pos;
        BlockState placeState = getPlacingState(stack);
        if (!BlockUtils.isReplaceable(world, pos)) {
            placePos = placePos.offset(dir);
        }

        if (player.canPlayerEdit(placePos, dir, stack)) {
            if (state.equals(placeState)) {
                if (world.setBlockState(placePos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
                    SoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_LIGHT, SoundCategory.BLOCKS, world, pos, 1F, 1F);
                }
            } else if (placeState.isValidPosition(world, placePos) &&
                    world.func_226663_a_(placeState, placePos, selContext)) {
                if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, COST_PER_FLARE, false)) {
                    if (world.setBlockState(placePos, placeState, Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
                        SoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_LIGHT, SoundCategory.BLOCKS, world, pos, 1F, 1F);
                    }
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) {
            return 0xFFFFFFFF;
        }
        DyeColor color = getConfiguredColor(stack);
        return ColorUtils.flareColorFromDye(color).getRGB() | 0xFF000000;
    }

    public static void setConfiguredColor(ItemStack stack, DyeColor color) {
        NBTHelper.getPersistentData(stack).putInt("color", color != null ? color.getId() : DyeColor.YELLOW.getId());
    }

    @Nonnull
    public static DyeColor getConfiguredColor(ItemStack stack) {
        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        if (tag.contains("color")) {
            return DyeColor.byId(tag.getInt("color"));
        }
        return DyeColor.YELLOW;
    }

    @Nonnull
    public static BlockState getPlacingState(ItemStack wand) {
        return BlocksAS.FLARE_LIGHT.getDefaultState().with(BlockFlareLight.COLOR, getConfiguredColor(wand));
    }
}
