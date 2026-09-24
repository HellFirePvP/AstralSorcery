/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialCrystalItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialCrystalItem extends RockCrystalItem {

    public CelestialCrystalItem() {
        super(CrystalAttributesComponent.empty(6, 14));
    }

    protected CelestialCrystalItem(CrystalAttributesComponent emptyComponent) {
        super(emptyComponent);
    }

    @Nullable
    @Override
    public ColorWrapper getItemEntityColor(ItemStack stack) {
        return ColorsAS.CELESTIAL_CRYSTAL;
    }

    @Override
    public Item getAttunedItem() {
        return ItemsAS.ATTUNED_CELESTIAL_CRYSTAL.get();
    }
}
