/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.ore;

import hellfirepvp.astralsorcery.common.block.base.CustomItemBlock;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import net.minecraft.block.Block;
import net.minecraftforge.common.ToolType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStarmetalOre
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:40
 */
public class BlockStarmetalOre extends Block implements CustomItemBlock {

    public BlockStarmetalOre() {
        super(PropertiesMisc.defaultRock()
                .harvestLevel(2)
                .harvestTool(ToolType.PICKAXE));
    }

}
