/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.tile.crystal.CollectorCrystalType;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.block.Block;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockCelestialCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 21:04
 */
public class ItemBlockCelestialCollectorCrystal extends ItemBlockCollectorCrystal {

    public ItemBlockCelestialCollectorCrystal(Block block, Properties itemProperties) {
        super(block, itemProperties
                .rarity(CommonProxy.RARITY_CELESTIAL));
    }

    @Override
    public CollectorCrystalType getCollectorType() {
        return CollectorCrystalType.CELESTIAL_CRYSTAL;
    }

    @Override
    protected CrystalAttributes getCreativeTemplateAttributes() {
        return CrystalPropertiesAS.CREATIVE_CELESTIAL_COLLECTOR_ATTRIBUTES;
    }
}
