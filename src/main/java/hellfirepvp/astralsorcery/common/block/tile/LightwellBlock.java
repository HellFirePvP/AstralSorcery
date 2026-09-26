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
import hellfirepvp.astralsorcery.common.tile.TileLightwell;
import hellfirepvp.astralsorcery.common.util.InteractUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightwellBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightwellBlock extends BaseTickTileBlock<TileLightwell> {

    public static MapCodec<LightwellBlock> CODEC = simpleCodec(LightwellBlock::new);
    private static final VoxelShape SHAPE = createShape();

    public LightwellBlock(Properties properties) {
        super(properties, TileEntitiesAS.LIGHTWELL);
    }

    private static VoxelShape createShape() {
        VoxelShape footing = Block.box(1, 0, 1, 15, 2, 15);
        VoxelShape floor = Block.box(3, 2, 3, 13, 4, 13);
        VoxelShape basinFloor = Block.box(1, 4, 1, 15, 5, 15);
        VoxelShape w1 = Block.box(1, 5, 1, 2, 16, 14);
        VoxelShape w2 = Block.box(2, 5, 1, 15, 16, 2);
        VoxelShape w3 = Block.box(14, 5, 2, 15, 16, 15);
        VoxelShape w4 = Block.box(1, 5, 14, 14, 16, 15);

        return Shapes.or(footing, floor, basinFloor, w1, w2, w3, w4);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        return MiscUtil.getTileAt(level, pos, TileLightwell.class, true).map(lightwell -> {
            ItemStack held = player.getItemInHand(hand);

            return MiscUtil.firstNonNull(
                    () -> InteractUtil.tryTransferFluidFromBlock(held, player, level, pos, newStack -> player.setItemInHand(hand, newStack)),
                    () -> InteractUtil.tryPlaceItemIntoBlock(held, player, level, pos)
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
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            MiscUtil.getTileAt(level, pos, TileLightwell.class, true).ifPresent(TileLightwell::breakCatalyst);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected BlockEntityTicker<TileLightwell> createTicker() {
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
