/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarAttunementRecipeHandler
 * Created by HellFirePvP
 * Date: 15.02.2017 / 18:11
 */
public class AltarAttunementRecipeHandler extends JEIBaseHandler<AttunementRecipe> {

    @Override
    public Class<AttunementRecipe> getRecipeClass() {
        return AttunementRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(AttunementRecipe recipe) {
        return ModIntegrationJEI.idAltarAttunement;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(AttunementRecipe recipe) {
        return new AltarAttunementRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(AttunementRecipe recipe) {
        return recipe.getRequiredProgression() == ResearchProgression.ATTUNEMENT;
    }

}
