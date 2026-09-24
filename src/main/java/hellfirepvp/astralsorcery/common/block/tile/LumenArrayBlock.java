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
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.lumen.capability.LumenHandlerView;
import hellfirepvp.astralsorcery.common.tile.TileLightwell;
import hellfirepvp.astralsorcery.common.tile.TileLumenArray;
import hellfirepvp.astralsorcery.common.util.InteractUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenArrayBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenArrayBlock extends BaseTickTileBlock<TileLumenArray> {

    public static MapCodec<LumenArrayBlock> CODEC = simpleCodec(LumenArrayBlock::new);
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    public LumenArrayBlock(Properties properties) {
        this(properties, TileEntitiesAS.LUMEN_ARRAY);
    }

    protected LumenArrayBlock(Properties properties, TileRegistryObject<?> tileType) {
        super(properties, MiscUtil.cast(tileType));
        this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ENABLED);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        return MiscUtil.getTileAt(level, pos, TileLumenArray.class, true).map(lumenArray -> {
            ItemStack held = player.getItemInHand(hand);

            return MiscUtil.firstNonNull(
                    () -> InteractUtil.tryTransferFluidIntoBlock(held, player, level, pos, newStack -> player.setItemInHand(hand, newStack)),
                    () -> InteractUtil.tryPlaceItemIntoBlock(held, player, level, pos)
            ).orElse(ItemInteractionResult.SUCCESS);
        }).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return MiscUtil.getTileAt(level, pos, TileLumenArray.class, true).map(tile -> {
            LumenHandlerView handler = tile.getTileData().getLumenHandler();
            int amt = handler.getContainedLumen().stream()
                    .findFirst()
                    .map(LumenStack::getAmount)
                    .orElse(0);
            return Math.round(Mth.clamp(((float) amt / TileLumenArray.Data.LUMEN_TANK_CAPACITY), 0F, 1F) * 15F);
        }).orElse(0);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        this.updateEnabledState(level, pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (!oldState.is(state.getBlock())) {
            this.updateEnabledState(level, pos, state);
        }
    }

    private void updateEnabledState(Level level, BlockPos pos, BlockState currentState) {
        boolean enable = !level.hasNeighborSignal(pos);
        if (enable != currentState.getValue(ENABLED)) {
            level.setBlock(pos, currentState.setValue(ENABLED, enable), Block.UPDATE_ALL);
        }
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
        if (!(newState.getBlock() instanceof LumenArrayBlock)) {
            if (!level.isClientSide()) {
                MiscUtil.getTileAt(level, pos, TileLumenArray.class, true).ifPresent(TileLumenArray::breakCatalyst);
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityTicker<TileLumenArray> createTicker() {
        return this.ticker();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
