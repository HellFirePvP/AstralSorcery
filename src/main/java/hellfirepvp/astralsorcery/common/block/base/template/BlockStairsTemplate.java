/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base.template;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStairsTemplate
 * Created by HellFirePvP
 * Date: 21.07.2019 / 10:34
 */
public class BlockStairsTemplate extends StairsBlock implements CustomItemBlock {

    public BlockStairsTemplate(BlockState state, Properties properties) {
        super(state, properties);
    }
}
