/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.TileEntitiesAS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenAlchemyArrayBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenAlchemyArrayBlock extends LumenArrayBlock {

    public static MapCodec<LumenAlchemyArrayBlock> CODEC = simpleCodec(LumenAlchemyArrayBlock::new);
    private static final VoxelShape SHAPE = Block.box(-1, 0, -1, 17, 17, 17);

    public LumenAlchemyArrayBlock(Properties properties) {
        super(properties, TileEntitiesAS.LUMEN_ALCHEMY_ARRAY);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
