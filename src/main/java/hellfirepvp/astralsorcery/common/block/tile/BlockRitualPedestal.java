/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile;

import hellfirepvp.astralsorcery.common.block.base.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRitualPedestal
 * Created by HellFirePvP
 * Date: 09.07.2019 / 20:03
 */
public class BlockRitualPedestal extends BlockStarlightNetwork {

    public BlockRitualPedestal() {
        super(Block.Properties.create(MaterialsAS.MARBLE)
                .hardnessAndResistance(1F, 3F)
                .sound(SoundType.STONE));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileRitualPedestal();
    }
}
