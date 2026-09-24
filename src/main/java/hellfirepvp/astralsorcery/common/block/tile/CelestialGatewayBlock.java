/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.block.tile.base.BaseTickTileBlock;
import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialGatewayBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialGatewayBlock extends BaseTickTileBlock<TileCelestialGateway> {

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 1, 15);

    public static final float defaultDestroySpeed = 2.5F;
    public static MapCodec<CelestialGatewayBlock> CODEC = simpleCodec(CelestialGatewayBlock::new);

    public CelestialGatewayBlock(Properties properties) {
        super(properties, TileEntitiesAS.CELESTIAL_GATEWAY);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        ColorComponent cmp = stack.get(DataComponentsAS.COLOR);
        if (cmp != null) {
            cmp.reference().asDyeColor().ifPresent(dyeColor -> {
                tooltipComponents.add(ColorUtil.getColorName(dyeColor).withColor(dyeColor.getTextColor()));
            });
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        MiscUtil.getTileAt(level, pos, TileCelestialGateway.class, true).ifPresent(gateway -> {
            if (gateway.hasCustomName()) {
                stack.set(DataComponents.CUSTOM_NAME, gateway.getCustomName());
            }
            if (gateway.getTileData().getColor() != null) {
                stack.set(DataComponentsAS.COLOR, new ColorComponent(ColorReference.Dye.of(gateway.getTileData().getColor())));
            }
        });
        return stack;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (placer instanceof ServerPlayer sPlayer) {
            MiscUtil.getTileAt(level, pos, TileCelestialGateway.class, true).ifPresent(tile -> {
                Component customName = stack.get(DataComponents.CUSTOM_NAME);
                if (customName != null) {
                    tile.getTileData().setCustomName(customName);
                }
                Optional.ofNullable(stack.get(DataComponentsAS.COLOR))
                        .flatMap(cmp -> cmp.reference().asDyeColor())
                        .ifPresent(dyeColor -> tile.getTileData().setColor(dyeColor));
                tile.getTileData().markForUpdate();
            });
        }
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return MiscUtil.getTileAt(level, pos, TileCelestialGateway.class, true).map(gateway -> {
            var data = gateway.getTileData();
            if (data.isLocked() && data.hasOwner() && !data.isOwner(player)) {
                return 0F;
            }
            int hardnessMultiplier = EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
            return player.getDigSpeed(state, pos) / defaultDestroySpeed / hardnessMultiplier;
        }).orElse(0F);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected BlockEntityTicker<TileCelestialGateway> createTicker() {
        return this.ticker();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
