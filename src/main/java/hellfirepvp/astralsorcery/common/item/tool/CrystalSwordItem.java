/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyCalculator;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalSwordItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalSwordItem extends SwordItem implements CrystalToolItem {

    public CrystalSwordItem() {
        this(ItemsAS.CRYSTAL_TOOL_TIER, new Properties()
                .setNoRepair()
                .attributes(swordAttributes()));
    }

    protected CrystalSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    protected static ItemAttributeModifiers swordAttributes() {
        return createAttributes(ItemsAS.CRYSTAL_TOOL_TIER, 3, -2.4F);
    }

    @Override
    public int getCrystalCount() {
        return 2;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CrystalPropertyCalculator.getToolDurability(super.getMaxDamage(stack), stack, 1.5F);
    }
}
