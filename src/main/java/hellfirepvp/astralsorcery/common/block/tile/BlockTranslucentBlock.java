/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockFakedState;
import hellfirepvp.astralsorcery.common.tile.TileTranslucentBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTranslucentBlock
 * Created by HellFirePvP
 * Date: 28.11.2019 / 19:16
 */
public class BlockTranslucentBlock extends BlockFakedState {

    public BlockTranslucentBlock() {
        super(Properties.create(Material.BARRIER, MaterialColor.AIR)
                .hardnessAndResistance(-1.0F, 6_000_000.0F)
                .setLightLevel(state -> 12));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        this.playParticles(world, pos, rand);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new TileTranslucentBlock();
    }
}
