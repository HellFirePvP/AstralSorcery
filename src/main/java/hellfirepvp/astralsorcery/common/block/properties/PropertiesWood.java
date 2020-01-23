/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.properties;

import hellfirepvp.astralsorcery.common.lib.MaterialsAS;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertiesWood
 * Created by HellFirePvP
 * Date: 20.07.2019 / 20:00
 */
public class PropertiesWood {

    public static Block.Properties defaultInfusedWood() {
        return Block.Properties.create(MaterialsAS.INFUSED_WOOD)
                .hardnessAndResistance(2.5F, 7F)
                .harvestTool(ToolType.AXE)
                .sound(SoundType.WOOD);
    }

}
