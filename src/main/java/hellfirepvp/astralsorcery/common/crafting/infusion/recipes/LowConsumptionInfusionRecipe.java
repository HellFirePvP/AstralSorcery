/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion.recipes;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LowConsumptionInfusionRecipe
 * Created by HellFirePvP
 * Date: 12.02.2017 / 17:23
 */
public class LowConsumptionInfusionRecipe extends BasicInfusionRecipe {

    public LowConsumptionInfusionRecipe(ItemStack output, String oreDictInput) {
        super(output, oreDictInput);
        setLiquidStarlightConsumptionChance(0.01F);
    }

    public LowConsumptionInfusionRecipe(ItemStack output, ItemStack input) {
        super(output, input);
        setLiquidStarlightConsumptionChance(0.01F);
    }

    public LowConsumptionInfusionRecipe(ItemStack output, ItemHandle input) {
        super(output, input);
        setLiquidStarlightConsumptionChance(0.01F);
    }

}
