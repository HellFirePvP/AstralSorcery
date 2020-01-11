/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.ore;

import hellfirepvp.astralsorcery.common.block.base.template.BlockRockTemplate;
import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStarmetalOre
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:40
 */
public class BlockStarmetalOre extends BlockRockTemplate {

    @Override
    public boolean isSolid(BlockState p_200124_1_) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
