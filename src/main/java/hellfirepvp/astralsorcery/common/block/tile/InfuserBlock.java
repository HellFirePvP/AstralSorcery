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
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.util.InteractUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.TileRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InfuserBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InfuserBlock extends BaseTickTileBlock<TileInfuser> {

    public static MapCodec<InfuserBlock> CODEC = simpleCodec(InfuserBlock::new);

    public InfuserBlock(Properties properties) {
        this(properties, TileEntitiesAS.INFUSER);
    }

    protected InfuserBlock(Properties properties, TileRegistryObject<TileInfuser> tileType) {
        super(properties, tileType);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        return MiscUtil.getTileAt(level, pos, TileInfuser.class, true).map(infuser -> {
            ItemStack held = player.getItemInHand(hand);

            if (hand == InteractionHand.MAIN_HAND && stack.is(ItemsAS.WAND) &&
                    infuser.getTileData().getActiveRecipe().isEmpty()) {
                return infuser.findMatchingRecipe(level).map(recipe -> {
                    infuser.startCrafting(level, recipe);
                    return ItemInteractionResult.SUCCESS;
                }).orElse(ItemInteractionResult.CONSUME);
            }

            return MiscUtil.firstNonNull(
                    () -> InteractUtil.tryPlaceItemIntoBlock(held, player, level, pos)
            ).orElse(ItemInteractionResult.SUCCESS);
        }).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected BlockEntityTicker<TileInfuser> createTicker() {
        return this.ticker();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
