/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.journal;

import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageAltarRecipe;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageRecipe;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderPageText;
import hellfirepvp.astralsorcery.client.screen.journal.page.RenderablePage;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.function.Predicate;
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

    private JournalPageRecipe(Supplier<IRecipe<?>> recipeProvider) {
        this.recipeProvider = recipeProvider;
    }

    public static JournalPageRecipe fromName(ResourceLocation recipeId) {
        return new JournalPageRecipe(() -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server, but calling GUI code?");
            }

            IRecipe<?> recipe = mgr.getRecipes(RecipeTypesAS.TYPE_ALTAR.getType()).get(recipeId);
            if (recipe != null) {
                return recipe;
            }

            recipe = mgr.getRecipes(IRecipeType.CRAFTING).get(recipeId);
            if (recipe != null) {
                return recipe;
            }
            return null;
        });
    }

    public static JournalPageRecipe fromOutputPreferAltarRecipes(Predicate<ItemStack> outputTest) {
        return new JournalPageRecipe(() -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server, but calling GUI code?");
            }

            IRecipe<?> recipe = mgr.getRecipes(RecipeTypesAS.TYPE_ALTAR.getType()).values()
                    .stream()
                    .map(r -> (SimpleAltarRecipe) r)
                    .filter(r -> outputTest.test(r.getOutputForRender(Collections.emptyList())))
                    .findFirst()
                    .orElse(null);
            if (recipe != null) {
                return recipe;
            }

            recipe = mgr.getRecipes(IRecipeType.CRAFTING).values()
                    .stream()
                    .filter(r -> outputTest.test(r.getRecipeOutput()))
                    .findFirst()
                    .orElse(null);
            if (recipe != null) {
                return recipe;
            }
            return null;
        });
    }

    public static JournalPageRecipe fromOutputPreferVanillaRecipes(Predicate<ItemStack> outputTest) {
        return new JournalPageRecipe(() -> {
            RecipeManager mgr = RecipeHelper.getRecipeManager();
            if (mgr == null) {
                throw new IllegalStateException("Not connected to a server, but calling GUI code?");
            }

            IRecipe<?> recipe = mgr.getRecipes(IRecipeType.CRAFTING).values()
                    .stream()
                    .filter(r -> outputTest.test(r.getRecipeOutput()))
                    .findFirst()
                    .orElse(null);
            if (recipe != null) {
                return recipe;
            }

            recipe = mgr.getRecipes(RecipeTypesAS.TYPE_ALTAR.getType()).values()
                    .stream()
                    .map(r -> (SimpleAltarRecipe) r)
                    .filter(r -> outputTest.test(r.getOutputForRender(Collections.emptyList())))
                    .findFirst()
                    .orElse(null);
            if (recipe != null) {
                return recipe;
            }
            return null;
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderablePage buildRenderPage(ResearchNode node, int nodePage) {
        IRecipe<?> recipe = this.recipeProvider.get();
        if (recipe instanceof SimpleAltarRecipe) {
            return new RenderPageAltarRecipe(node, nodePage, (SimpleAltarRecipe) recipe);
        } else if (recipe != null) {
            return RenderPageRecipe.fromRecipe(node, nodePage, recipe);
        } else {
            return new RenderPageText("astralsorcery.journal.recipe.removalinfo");
        }
    }
}
