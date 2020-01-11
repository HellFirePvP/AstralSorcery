/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.crystal;

import hellfirepvp.astralsorcery.common.block.tile.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockRockCollectorCrystal;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRockCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 23:03
 */
public class BlockRockCollectorCrystal extends BlockCollectorCrystal {

    public BlockRockCollectorCrystal() {
        super(CollectorCrystalType.ROCK_CRYSTAL);
    }

    @Override
    public Class<? extends ItemBlockCollectorCrystal> getItemBlockClass() {
        return ItemBlockRockCollectorCrystal.class;
    }
}
