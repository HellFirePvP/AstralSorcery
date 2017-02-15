/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.integrations.mods.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmutationRecipeHandler
 * Created by HellFirePvP
 * Date: 15.02.2017 / 16:00
 */
public class TransmutationRecipeHandler implements IRecipeHandler<LightOreTransmutations.Transmutation> {

    @Override
    public Class<LightOreTransmutations.Transmutation> getRecipeClass() {
        return LightOreTransmutations.Transmutation.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return ModIntegrationJEI.idTransmutation;
    }

    @Override
    public String getRecipeCategoryUid(LightOreTransmutations.Transmutation recipe) {
        return ModIntegrationJEI.idTransmutation;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(LightOreTransmutations.Transmutation recipe) {
        return new TransmutationRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(LightOreTransmutations.Transmutation recipe) {
        ItemStack inStack = ItemUtils.createBlockStack(recipe.input);
        ItemStack outStack = ItemUtils.createBlockStack(recipe.output);
        return inStack != null && outStack != null;
    }
}
