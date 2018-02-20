/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarConstellationRecipeHelper
 * Created by HellFirePvP
 * Date: 15.02.2017 / 19:18
 */
public class AltarConstellationRecipeHandler extends JEIBaseHandler<ConstellationRecipe> {

    @Override
    public Class<ConstellationRecipe> getRecipeClass() {
        return ConstellationRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(ConstellationRecipe recipe) {
        return ModIntegrationJEI.idAltarConstellation;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(ConstellationRecipe recipe) {
        return new AltarConstellationRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(ConstellationRecipe recipe) {
        return recipe.getRequiredProgression() == ResearchProgression.CONSTELLATION;
    }
}
