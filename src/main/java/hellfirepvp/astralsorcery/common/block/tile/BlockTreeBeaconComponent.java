/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockFakedState;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeaconComponent;
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
 * Class: BlockTreeBeaconComponent
 * Created by HellFirePvP
 * Date: 04.09.2020 / 19:18
 */
public class BlockTreeBeaconComponent extends BlockFakedState {

    public BlockTreeBeaconComponent() {
        super(Properties.create(Material.BARRIER, MaterialColor.AIR)
                .hardnessAndResistance(-1.0F, 6_000_000.0F)
                .lightValue(12));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        this.playParticles(world, pos, rand);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileTreeBeaconComponent();
    }
}
