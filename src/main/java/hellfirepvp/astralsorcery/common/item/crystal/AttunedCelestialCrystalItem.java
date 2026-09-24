/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.component.AttunedConstellationComponent;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttunedCelestialCrystalItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttunedCelestialCrystalItem extends CelestialCrystalItem {

    public AttunedCelestialCrystalItem() {}

    protected AttunedCelestialCrystalItem(CrystalAttributesComponent emptyComponent) {
        super(emptyComponent);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS_ATTUNED_CRYSTALS.get();
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        RegistriesAS.REGISTRY_CONSTELLATIONS.forEach(cst -> {
            ItemStack stack = new ItemStack(this);
            stack.set(DataComponentsAS.ATTUNED_CONSTELLATION, new AttunedConstellationComponent(cst));
            tabItems.accept(stack);
        });
    }

    @Nonnull
    @Override
    public RockCrystalItem getCrystalSplitItem() {
        return ItemsAS.CELESTIAL_CRYSTAL.get();
    }
}
