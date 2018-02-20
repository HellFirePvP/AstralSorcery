/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei;

import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipeHandler
 * Created by HellFirePvP
 * Date: 23.11.2017 / 20:00
 */
public class GrindstoneRecipeHandler extends JEIBaseHandler<GrindstoneRecipe> {

    @Override
    public Class<GrindstoneRecipe> getRecipeClass() {
        return GrindstoneRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(GrindstoneRecipe recipe) {
        return ModIntegrationJEI.idGrindstone;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(GrindstoneRecipe recipe) {
        return new GrindstoneRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(GrindstoneRecipe recipe) {
        return true;
    }

}
