/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalToolTier
 * Created by HellFirePvP
 * Date: 17.08.2019 / 16:13
 */
public class CrystalToolTier implements IItemTier {

    private static CrystalToolTier INSTANCE = new CrystalToolTier();

    private CrystalToolTier() {}

    public static CrystalToolTier getInstance() {
        return INSTANCE;
    }

    @Override
    public int getMaxUses() {
        return 16192;
    }

    @Override
    public float getEfficiency() {
        return 7.0F;
    }

    @Override
    public float getAttackDamage() {
        return 5.5F;
    }

    @Override
    public int getHarvestLevel() {
        return 3;
    }

    @Override
    public int getEnchantability() {
        return 40;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return Ingredient.EMPTY;
    }
}
