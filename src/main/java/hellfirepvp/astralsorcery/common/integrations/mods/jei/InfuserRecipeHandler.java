/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.ModIntegrationJEI;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfuserRecipeHandler
 * Created by HellFirePvP
 * Date: 11.01.2017 / 00:58
 */
public class InfuserRecipeHandler implements IRecipeHandler<AbstractInfusionRecipe> {

    @Override
    public Class<AbstractInfusionRecipe> getRecipeClass() {
        return AbstractInfusionRecipe.class;
    }

    @Override
    @Deprecated
    public String getRecipeCategoryUid() {
        return ModIntegrationJEI.idInfuser;
    }

    @Override
    public String getRecipeCategoryUid(AbstractInfusionRecipe recipe) {
        return ModIntegrationJEI.idInfuser;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(AbstractInfusionRecipe recipe) {
        return new InfuserRecipeWrapper(recipe);
    }

    //Would crash way earlier if that was ever false.
    @Override
    public boolean isRecipeValid(AbstractInfusionRecipe recipe) {
        return true;
    }

}
