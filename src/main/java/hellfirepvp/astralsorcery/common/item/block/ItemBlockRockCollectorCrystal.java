/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.block.Block;

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
    protected CrystalAttributes getCreativeTemplateAttributes() {
        return CrystalPropertiesAS.CREATIVE_ROCK_COLLECTOR_ATTRIBUTES;
    }
}
