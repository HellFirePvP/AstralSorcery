/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageAltarRecipe;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageAltarRecipe
 * Created by HellFirePvP
 * Date: 12.10.2019 / 14:21
 */
public class JournalPageAltarRecipe implements JournalPage {

    private final Supplier<SimpleAltarRecipe> recipeProvider;

    public JournalPageAltarRecipe(ResourceLocation recipeId) {
        this.recipeProvider = () -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server, but calling GUI code?");
            }

            IRecipe<?> recipe = mgr.getRecipes(RecipeTypesAS.TYPE_ALTAR.getType()).get(recipeId);
            if (recipe instanceof SimpleAltarRecipe) {
                return (SimpleAltarRecipe) recipe;
            }
            throw new IllegalArgumentException("Recipe " + recipeId + " does not exist or is not a shaped recipe!");
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderablePage buildRenderPage() {
        return new RenderPageAltarRecipe(recipeProvider.get());
    }
}
