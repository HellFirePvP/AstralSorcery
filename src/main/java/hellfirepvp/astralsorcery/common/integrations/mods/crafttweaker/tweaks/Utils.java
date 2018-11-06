/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.item.MCItemStack;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Utils
 * Created by HellFirePvP
 * Date: 19.05.2018 / 15:58
 */
@ZenClass("mods.astralsorcery.Utils")
public class Utils extends BaseTweaker {

    @ZenMethod
    public static IIngredient getCrystalORIngredient(boolean hasToBeCelestial, boolean hasToBeAttuned) {
        IIngredient combined;
        if(hasToBeCelestial) {
            if(hasToBeAttuned) {
                combined = getMCLazyMatchItem(new ItemStack(ItemsAS.tunedCelestialCrystal));
            } else {
                combined = getMCLazyMatchItem(new ItemStack(ItemsAS.tunedCelestialCrystal));
                combined = combined.or(getMCLazyMatchItem(new ItemStack(ItemsAS.celestialCrystal)));
            }
        } else {
            if(hasToBeAttuned) {
                combined = getMCLazyMatchItem(new ItemStack(ItemsAS.tunedCelestialCrystal));
                combined = combined.or(getMCLazyMatchItem(new ItemStack(ItemsAS.tunedRockCrystal)));
            } else {
                combined = getMCLazyMatchItem(new ItemStack(ItemsAS.tunedCelestialCrystal));
                combined = combined.or(getMCLazyMatchItem(new ItemStack(ItemsAS.celestialCrystal)));
                combined = combined.or(getMCLazyMatchItem(new ItemStack(ItemsAS.tunedRockCrystal)));
                combined = combined.or(getMCLazyMatchItem(new ItemStack(ItemsAS.rockCrystal)));
            }
        }
        return combined;
    }

    private static IIngredient getMCLazyMatchItem(ItemStack stack) {
        return new MCItemStack(stack) {
            @Override
            public boolean matchesExact(IItemStack item) {
                ItemStack internal = getItemStack(item);
                ItemStack thisInternal = (ItemStack) getInternal();
                if(thisInternal.hasTagCompound()) {
                    return matchesExact(item);
                }
                return !internal.isEmpty() && !thisInternal.isEmpty() && internal.getItem() == thisInternal.getItem() && (internal.getCount() >= thisInternal.getCount()) && (thisInternal.getItemDamage() == OreDictionary.WILDCARD_VALUE || thisInternal.getItemDamage() == internal.getItemDamage() || (!thisInternal.getHasSubtypes() && !thisInternal.getItem().isDamageable()));
            }
        };
    }

}
