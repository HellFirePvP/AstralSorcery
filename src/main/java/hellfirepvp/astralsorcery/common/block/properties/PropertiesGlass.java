/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.properties;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertiesGlass
 * Created by HellFirePvP
 * Date: 10.07.2019 / 21:03
 */
public class PropertiesGlass {

    public static Block.Properties coatedGlass() {
        return Block.Properties.create(Material.GLASS)
                .hardnessAndResistance(1F, 5F)
                .sound(SoundType.GLASS);
    }

}
