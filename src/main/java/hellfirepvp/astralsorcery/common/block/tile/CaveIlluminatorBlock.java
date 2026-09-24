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
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import hellfirepvp.astralsorcery.common.tile.TileCaveIlluminator;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CaveIlluminatorBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CaveIlluminatorBlock extends BaseTickTileBlock<TileCaveIlluminator> {

    public static MapCodec<CaveIlluminatorBlock> CODEC = simpleCodec(CaveIlluminatorBlock::new);

    private static final VoxelShape SHAPE = createShape();

    public CaveIlluminatorBlock(Properties properties) {
        super(properties, TileEntitiesAS.CAVE_ILLUMINATOR);
    }

    private static VoxelShape createShape() {
        VoxelShape[] cubes = new VoxelShape[27];
        int i = 0;
        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                for (int zz = 0; zz < 3; zz++) {
                    cubes[i++] = Block.box(
                            1 + xx * 5, 1 + yy * 5, 1 + zz * 5,
                            5 + xx * 5, 5 + yy * 5, 5 + zz * 5);
                }
            }
        }
        return Shapes.or(cubes[0], java.util.Arrays.copyOfRange(cubes, 1, cubes.length));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && placer instanceof Player) {
            MiscUtil.getTileAt(level, pos, TileCaveIlluminator.class, true).ifPresent(tile -> {
                tile.getTileData().setPlayerPlaced(true);
                tile.getTileData().markForUpdate();
            });
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(ItemsAS.ILLUMINATION_WAND)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        if (!(level instanceof ServerLevel sLevel) || !(player instanceof ServerPlayer)) {
            return ItemInteractionResult.SUCCESS;
        }

        return MiscUtil.getTileAt(level, pos, TileCaveIlluminator.class, true).map(tile -> {
            ColorComponent colorCmp = stack.getOrDefault(DataComponentsAS.COLOR, ColorComponent.DEFAULT_YELLOW);
            DyeColor dyeColor = colorCmp.reference().asDyeColor().orElse(DyeColor.YELLOW);
            tile.onWandUsed(dyeColor);
            ServerSoundHelper.playSoundAround(SoundsAS.ILLUMINATION_WAND_LIGHT, sLevel, pos, 0.6F, 1F);
            return ItemInteractionResult.SUCCESS;
        }).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockEntityTicker<TileCaveIlluminator> createTicker() {
        return this.ticker();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
