/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAttunementAltar
 * Created by HellFirePvP
 * Date: 17.11.2019 / 07:43
 */
public class BlockAttunementAltar extends ContainerBlock implements CustomItemBlock {

    private static final VoxelShape ATTUNEMENT_ALTAR = Block.makeCuboidShape(-2, 0, -2, 18, 6, 18);
    private static final VoxelShape ATTUNEMENT_ALTAR_COLLISION = Block.makeCuboidShape(0, 0, 0, 16, 6, 16);

    public BlockAttunementAltar() {
        super(PropertiesMarble.defaultMarble()
                .lightValue(4)
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        RenderingUtils.playBlockBreakParticles(pos, state, BlocksAS.MARBLE_RUNED.getDefaultState());
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return ATTUNEMENT_ALTAR;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return ATTUNEMENT_ALTAR_COLLISION;
    }

    @Override
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileAttunementAltar();
    }
}
