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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertiesMarble
 * Created by HellFirePvP
 * Date: 10.07.2019 / 21:02
 */
public class PropertiesMarble {

    public static Block.Properties defaultMarble() {
        return Block.Properties.create(MaterialsAS.MARBLE)
                .hardnessAndResistance(3F, 25F)
                .sound(SoundType.STONE);
    }

    public static Block.Properties defaultBlackMarble() {
        return Block.Properties.create(MaterialsAS.MARBLE)
                .hardnessAndResistance(3F, 25F)
                .sound(SoundType.STONE);
    }

}
