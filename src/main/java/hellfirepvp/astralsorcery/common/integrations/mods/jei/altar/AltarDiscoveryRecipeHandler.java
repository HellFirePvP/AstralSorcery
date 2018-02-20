/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.altar;

import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.base.JEIBaseHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarDiscoveryRecipeHandler
 * Created by HellFirePvP
 * Date: 15.02.2017 / 16:54
 */
public class AltarDiscoveryRecipeHandler extends JEIBaseHandler<DiscoveryRecipe> {

    @Override
    public Class<DiscoveryRecipe> getRecipeClass() {
        return DiscoveryRecipe.class;
    }

    @Override
    public String getRecipeCategoryUid(DiscoveryRecipe recipe) {
        return ModIntegrationJEI.idAltarDiscovery;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(DiscoveryRecipe recipe) {
        return new AltarDiscoveryRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(DiscoveryRecipe recipe) {
        return recipe.getRequiredProgression() == ResearchProgression.BASIC_CRAFT; //Find a better way of filtering..
    }

}
