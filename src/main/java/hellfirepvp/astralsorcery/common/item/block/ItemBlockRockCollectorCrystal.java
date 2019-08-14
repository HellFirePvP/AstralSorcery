/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockRockCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 21:00
 */
public class ItemBlockRockCollectorCrystal extends ItemBlockCollectorCrystal {

    public ItemBlockRockCollectorCrystal(Block block, Properties itemProperties) {
        super(block, itemProperties);
    }

    @Override
    public CollectorCrystalType getCollectorType() {
        return CollectorCrystalType.ROCK_CRYSTAL;
    }

    @Override
    public int getMaxPropertySize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_ROCK;
    }

    @Override
    public CrystalProperties getMaxProperties(ItemStack stack) {
        return CrystalProperties.getMaxRockProperties();
    }
}
