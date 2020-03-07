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
import net.minecraft.block.material.MaterialColor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertiesMisc
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:25
 */
public class PropertiesMisc {

    public static Block.Properties defaultAir() {
        return Block.Properties.create(Material.AIR, MaterialColor.AIR)
                .doesNotBlockMovement();
    }

    public static Block.Properties defaultSand() {
        return Block.Properties.create(Material.SAND, MaterialColor.SAND)
                .hardnessAndResistance(0.5F)
                .sound(SoundType.SAND);
    }

    public static Block.Properties defaultRock() {
        return Block.Properties.create(Material.ROCK, MaterialColor.STONE)
                .hardnessAndResistance(1.5F, 6.0F)
                .sound(SoundType.STONE);
    }

    public static Block.Properties defaultPlant() {
        return Block.Properties.create(Material.PLANTS)
                .doesNotBlockMovement()
                .hardnessAndResistance(0)
                .sound(SoundType.PLANT);
    }

    public static Block.Properties defaultTickingPlant() {
        return Block.Properties.create(Material.PLANTS)
                .doesNotBlockMovement()
                .tickRandomly()
                .hardnessAndResistance(0)
                .sound(SoundType.PLANT);
    }

    public static Block.Properties defaultGoldMachinery() {
        return Block.Properties.create(Material.IRON, MaterialColor.GOLD)
                .hardnessAndResistance(1.0F, 4.0F)
                .sound(SoundType.STONE);
    }

}
