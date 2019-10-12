/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageRecipe;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageRecipe
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:29
 */
public class JournalPageRecipe implements JournalPage {

    private final Supplier<IRecipe<?>> recipeProvider;

    public JournalPageRecipe(ResourceLocation recipeId) {
        this.recipeProvider = () -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server, but calling GUI code?");
            }

            IRecipe<?> recipe = mgr.getRecipes(IRecipeType.CRAFTING).get(recipeId);
            if (recipe != null) {
                return recipe;
            }
            throw new IllegalArgumentException("Crafting recipe " + recipeId + " does not exist!");
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderablePage buildRenderPage() {
        return RenderPageRecipe.fromRecipe(this.recipeProvider.get());
    }
}
