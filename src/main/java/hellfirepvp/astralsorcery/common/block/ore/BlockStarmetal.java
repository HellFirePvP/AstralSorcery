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
import net.minecraft.block.material.MaterialColor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStarmetal
 * Created by HellFirePvP
 * Date: 30.11.2020 / 11:10
 */
public class BlockStarmetal extends Block implements CustomItemBlock {

    public BlockStarmetal() {
        super(PropertiesMisc.defaultMetal(MaterialColor.BLUE));
    }
}
