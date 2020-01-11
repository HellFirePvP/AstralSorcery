/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.block.tile.BlockFlareLight;
import hellfirepvp.astralsorcery.common.block.tile.BlockTranslucentBlock;
import hellfirepvp.astralsorcery.common.item.base.render.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.tile.TileTranslucentBlock;
import hellfirepvp.astralsorcery.common.util.ColorUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationWand
 * Created by HellFirePvP
 * Date: 28.11.2019 / 20:57
 */
public class ItemIlluminationWand extends Item implements ItemDynamicColor {

    public ItemIlluminationWand() {
        super(new Properties()
                .maxStackSize(1)
                .group(RegistryItems.ITEM_GROUP_AS));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        DyeColor color = getConfiguredColor(stack);
        tooltip.add(ColorUtils.getTranslation(color).setStyle(new Style().setColor(ColorUtils.textFormattingForDye(color))));
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
                    world.setBlockState(pos, tb.getFakedState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                }
            } else {
                TileEntity tile = MiscUtils.getTileAt(world, pos, TileEntity.class, true);
                if (tile == null && !state.hasTileEntity()) {
                    if (world.setBlockState(pos, BlocksAS.TRANSLUCENT_BLOCK.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER)) {
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
            return ActionResultType.SUCCESS;
        }

        TileIlluminator illum = MiscUtils.getTileAt(world, pos, TileIlluminator.class, true);
        if (illum != null) {

            //TODO implement illuminator fully..
            return ActionResultType.SUCCESS;
        }

        ISelectionContext selContext = ISelectionContext.forEntity(player);
        //TODO eventually actually use the to-be-placed block as item here.
        BlockItemUseContext bContext = new BlockItemUseContext(context);
        BlockPos placePos = pos;
        BlockState placeState = getPlacingState(stack);
        if (!state.isReplaceable(bContext)) {
            placePos = placePos.offset(dir);
        }

        if (player.canPlayerEdit(placePos, dir, stack)) {
            if (state.equals(placeState)) {
                if (world.setBlockState(placePos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER)) {

                    SoundType type = placeState.getSoundType(world, placePos, player);
                    world.playSound(player, placePos, type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
                }
            } else if (placeState.isValidPosition(world, placePos) &&
                    world.func_217350_a(placeState, placePos, selContext) &&
                    world.setBlockState(placePos, placeState, Constants.BlockFlags.DEFAULT_AND_RERENDER)) {

                SoundType type = placeState.getSoundType(world, placePos, player);
                world.playSound(player, placePos, type.getPlaceSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
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
