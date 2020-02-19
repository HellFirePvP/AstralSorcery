/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.armor;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ArmorMaterialImbuedLeather
 * Created by HellFirePvP
 * Date: 17.02.2020 / 19:16
 */
public class ArmorMaterialImbuedLeather implements IArmorMaterial {

    @Override
    public int getDurability(EquipmentSlotType slot) {
        return 486;
    }

    @Override
    public int getDamageReductionAmount(EquipmentSlotType slot) {
        switch (slot) {
            case CHEST:
                return 7;
        }
        return 0;
    }

    @Override
    public int getEnchantability() {
        return 24;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return ArmorMaterial.LEATHER.getSoundEvent();
    }

    @Override
    public Ingredient getRepairMaterial() {
        return Ingredient.fromItems(ItemsAS.STARDUST);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getName() {
        return "imbued_leather";
    }

    @Override
    public float getToughness() {
        return 1.5F;
    }
}
