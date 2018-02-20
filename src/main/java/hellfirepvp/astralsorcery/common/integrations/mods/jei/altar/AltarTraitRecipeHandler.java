/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarTraitRecipeHandler
 * Created by HellFirePvP
 * Date: 19.10.2017 / 22:59
 */
public class AltarTraitRecipeHandler extends JEIBaseHandler<TraitRecipe> {

    @Override
    public Class<TraitRecipe> getRecipeClass() {
        return TraitRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(TraitRecipe recipe) {
        return ModIntegrationJEI.idAltarTrait;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(TraitRecipe recipe) {
        return new AltarTraitRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(TraitRecipe recipe) {
        return recipe.getRequiredProgression() == ResearchProgression.RADIANCE;
    }

}
