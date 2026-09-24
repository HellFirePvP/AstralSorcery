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
import hellfirepvp.astralsorcery.common.tile.TileLumenCrystallizer;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystallizerBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystallizerBlock extends BaseTickTileBlock<TileLumenCrystallizer> {

    public static MapCodec<LumenCrystallizerBlock> CODEC = simpleCodec(LumenCrystallizerBlock::new);
    private static final VoxelShape SHAPE = createShape();

    public LumenCrystallizerBlock(Properties properties) {
        super(properties, TileEntitiesAS.LUMEN_CRYSTALLIZER);
    }

    private static VoxelShape createShape() {
        VoxelShape pillar = Block.box(1, 0, 1, 15, 12, 15);
        VoxelShape top = Block.box(0, 12, 0, 16, 16, 16);
        return Shapes.or(pillar, top);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        return MiscUtil.getTileAt(level, pos, TileLumenCrystallizer.class, true).map(crystallizer -> {
            ItemStack held = player.getItemInHand(hand);

            return MiscUtil.firstNonNull(
                    () -> InteractUtil.tryTransferFluidIntoBlock(held, player, level, pos, newStack -> player.setItemInHand(hand, newStack)),
                    () -> {
                        if (level.isEmptyBlock(pos.above())) {
                            return InteractUtil.tryPlaceItemIntoBlock(held, player, level, pos);
                        }
                        return null;
                    }
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
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockEntityTicker<TileLumenCrystallizer> createTicker() {
        return this.ticker();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
