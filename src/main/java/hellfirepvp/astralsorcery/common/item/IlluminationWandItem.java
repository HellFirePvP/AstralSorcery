/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.block.FlareLightBlock;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.item.base.CreativeTabItem;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.item.base.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.tile.TileTranslucentBlock;
import hellfirepvp.astralsorcery.common.util.BlockUtil;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IlluminationWandItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IlluminationWandItem extends ItemCustom implements ItemDynamicColor {

    private static final ColorComponent DEFAULT_COLOR = ColorComponent.DEFAULT_YELLOW;

    public IlluminationWandItem() {
        super(new Properties()
                .component(DataComponentsAS.COLOR, DEFAULT_COLOR)
                .stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Direction dir = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (!(level instanceof ServerLevel sLevel) ||
                !(context.getPlayer() instanceof ServerPlayer sPlayer) ||
                !(stack.is(ItemsAS.ILLUMINATION_WAND))) {
            return InteractionResult.SUCCESS;
        }

        if (sPlayer.isShiftKeyDown()) {
            return this.tryPlaceTranslucentBlock(sLevel, pos, sPlayer, dir, stack);
        }

        BlockState flareState = BlocksAS.FLARE_LIGHT.get()
                .defaultBlockState()
                .setValue(FlareLightBlock.COLOR, stack.getOrDefault(DataComponentsAS.COLOR, DEFAULT_COLOR).reference().asDyeColor().orElse(DyeColor.YELLOW));
        BlockPos placePos = pos;
        if (!BlockUtil.isReplaceable(sLevel, placePos)) {
            placePos = placePos.relative(dir);
        }
        if (!BlockUtil.isReplaceable(sLevel, placePos)) {
            return InteractionResult.SUCCESS;
        }

        if (sPlayer.mayUseItemAt(placePos, dir, stack) && !EventHooks.onBlockPlace(sPlayer, BlockSnapshot.create(sLevel.dimension(), sLevel, placePos), dir)) {
            if (sLevel.getBlockState(placePos).equals(flareState)) {
                if (sLevel.setBlockAndUpdate(placePos, Blocks.AIR.defaultBlockState())) {
                    ServerSoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_LIGHT, sLevel, placePos, 0.6F, 1F);
                }
            } else if (flareState.canSurvive(sLevel, placePos) && sLevel.isUnobstructed(flareState, placePos, CollisionContext.of(sPlayer))) {
                if (sLevel.setBlockAndUpdate(placePos, flareState)) {
                    //TODO lumen cost
                    ServerSoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_LIGHT, sLevel, placePos, 0.6F, 1F);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryPlaceTranslucentBlock(ServerLevel sLevel, BlockPos pos, ServerPlayer sPlayer, Direction facing, ItemStack stack) {
        BlockState state = sLevel.getBlockState(pos);

        if (state.is(BlocksAS.TRANSLUCENT_BLOCK)) {
            MiscUtil.getTileAt(sLevel, pos, TileTranslucentBlock.class, true).ifPresent(tile -> {
                if (!tile.getTileData().hasOwner() || tile.getTileData().getOwnerId().equals(sPlayer.getUUID())) {
                    if (sLevel.setBlockAndUpdate(pos, tile.getTileData().getStoredState())) {
                        ServerSoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_UNHIGHLIGHT, sLevel, pos, 0.6F, 0.9F + rand.nextFloat() * 0.2F);
                    }
                }
            });
        } else {
            if (Block.isShapeFullBlock(state.getShape(sLevel, pos)) &&
                    sPlayer.mayUseItemAt(pos, facing, stack) &&
                    sLevel.getBlockEntity(pos) == null &&
                    sLevel.setBlockAndUpdate(pos, BlocksAS.TRANSLUCENT_BLOCK.get().defaultBlockState())) {
                //TODO lumen cost
                MiscUtil.getTileAt(sLevel, pos, TileTranslucentBlock.class, true).ifPresent(tile -> {
                    tile.getTileData().setStoredState(state);
                    tile.getTileData().setDyeColor(stack.getOrDefault(DataComponentsAS.COLOR, DEFAULT_COLOR).reference().asDyeColor().orElse(DyeColor.YELLOW));
                    tile.getTileData().setOwnerId(sPlayer.getUUID());

                    tile.getTileData().markForUpdate();

                    ServerSoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_HIGHLIGHT, sLevel, pos, 0.6F, 0.9F + rand.nextFloat() * 0.2F);
                });
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public int getColor(ItemStack stack, long tick, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFFFF;

        ColorComponent cmp = stack.getOrDefault(DataComponentsAS.COLOR, DEFAULT_COLOR);
        return cmp.reference().color() | 0xFF000000;
    }
}
