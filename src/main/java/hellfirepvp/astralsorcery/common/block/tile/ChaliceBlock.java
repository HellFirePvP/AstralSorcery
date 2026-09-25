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
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.InteractUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ChaliceBlock
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class ChaliceBlock extends BaseTickTileBlock<TileChalice> {

    public static final MapCodec<ChaliceBlock> CODEC = simpleCodec(ChaliceBlock::new);
    private static final VoxelShape SHAPE = Shapes.box(2D / 16D, 0D / 16D, 2D / 16D, 14D / 16D, 14D / 16D, 14D / 16D);

    public ChaliceBlock(Properties properties) {
        super(properties, TileEntitiesAS.CHALICE);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        return MiscUtil.getTileAt(level, pos, TileChalice.class, true).map(chalice -> {
            ItemStack held = player.getItemInHand(hand);
            return MiscUtil.firstNonNull(
                    () -> InteractUtil.tryTransferFluidFromBlock(held, player, level, pos, newStack -> InteractUtil.handleContainerReplacement(player, newStack, hand)),
                    () -> InteractUtil.tryTransferFluidIntoBlock(held, player, level, pos, newStack -> InteractUtil.handleContainerReplacement(player, newStack, hand))
            ).orElse(ItemInteractionResult.SUCCESS);
        }).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        AuxiliaryLightManager lightMgr = level.getAuxLightManager(pos);
        if (lightMgr == null) return 0;
        return lightMgr.getLightAt(pos);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return MiscUtil.getTileAt(level, pos, TileChalice.class, false)
                .map(chalice -> Mth.ceil(chalice.getTankFillPercentage() * 15F))
                .orElse(0);
    }

    @Override
    protected BlockEntityTicker<TileChalice> createTicker() {
        return this.ticker();
    }
}
