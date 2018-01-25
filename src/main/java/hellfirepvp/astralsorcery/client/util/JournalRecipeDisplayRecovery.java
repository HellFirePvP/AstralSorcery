/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.journal.page.*;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractRecipeAccessor;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.CraftingRecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalRecipeDisplayRecovery
 * Created by HellFirePvP
 * Date: 29.10.2017 / 13:28
 */
public class JournalRecipeDisplayRecovery {

    private JournalRecipeDisplayRecovery() {}

    public static void attemptRecipeRecovery() {
        if(!Mods.CRAFTTWEAKER.isPresent()) return; //Well, i guess noone changed recipes then?

        int rec = 0;
        for (ResearchProgression prog : ResearchProgression.values()) {
            for (ResearchNode rn : prog.getResearchNodes()) {
                Map<Integer, IJournalPage> update = new HashMap<>();
                List<IJournalPage> pages = rn.getPages();
                for (int i = 0; i < pages.size(); i++) {
                    IJournalPage page = pages.get(i);
                    RecoveryType rt = getRecoveryType(page);
                    if (rt != null) {
                        List<IRecipe> matchingRecipes;
                        boolean found;
                        IRecipe recipe;
                        switch (rt) {
                            case CRAFTING:
                                IRecipe actual = ((JournalPageRecipe) page).recipe.getParentRecipe();
                                matchingRecipes = CraftingRecipeHelper.findRecipesWithOutput(actual.getRecipeOutput());
                                found = false;
                                for (IRecipe ir : matchingRecipes) {
                                    if (ir.getRegistryName().equals(actual.getRegistryName())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    rec++;
                                    update.put(i, findReplacementPage(actual.getRecipeOutput()));
                                }
                                break;
                            case LIGHT_CRAFTING:
                                AccessibleRecipeAdapater recipeAd = ((JournalPageLightProximityRecipe) page).shapedLightProxRecipe;
                                matchingRecipes = CraftingRecipeHelper.findRecipesWithOutput(recipeAd.getRecipeOutput());
                                found = false;
                                for (IRecipe ir : matchingRecipes) {
                                    if (ir.getRegistryName().equals(recipeAd.getRegistryName())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    rec++;
                                    update.put(i, findReplacementPage(recipeAd.getRecipeOutput()));
                                }
                                break;
                            case ALTAR:
                                TileAltar.AltarLevel al = guessAltarLevel(page);
                                if(al != null) {
                                    AbstractAltarRecipe currentRecipe = null;
                                    ItemStack outputMatch = ItemStack.EMPTY;
                                    switch (al) {
                                        case DISCOVERY:
                                            currentRecipe = ((JournalPageDiscoveryRecipe) page).recipeToRender;
                                            outputMatch = currentRecipe.getOutputForMatching();
                                            break;
                                        case ATTUNEMENT:
                                            currentRecipe = ((JournalPageAttunementRecipe) page).recipe;
                                            outputMatch = currentRecipe.getOutputForMatching();
                                            break;
                                        case CONSTELLATION_CRAFT:
                                            currentRecipe = ((JournalPageConstellationRecipe) page).recipe;
                                            outputMatch = currentRecipe.getOutputForMatching();
                                            break;
                                        case TRAIT_CRAFT:
                                            currentRecipe = ((JournalPageTraitRecipe) page).recipe;
                                            outputMatch = currentRecipe.getOutputForMatching();
                                            break;
                                    }
                                    if(!outputMatch.isEmpty() && currentRecipe != null) {
                                        List<AbstractAltarRecipe> recipesMatched = AltarRecipeRegistry.getAltarRecipesByOutput(outputMatch, al);
                                        found = false;
                                        for(AbstractAltarRecipe aar : recipesMatched) {
                                            if(aar == currentRecipe) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if(!found) {
                                            rec++;
                                            update.put(i, findReplacementPage(outputMatch));
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
                for (Map.Entry<Integer, IJournalPage> entry : update.entrySet()) {
                    rn.getPages().set(entry.getKey(), entry.getValue());
                }
            }
        }

        if(rec > 0) {
            AstralSorcery.log.info("[AstralSorcery] Recovered " + rec + " changed recipes into journal!");
        }
    }

    @Nonnull
    private static IJournalPage findReplacementPage(ItemStack output) {
        List<IRecipe> matchingRecipes = CraftingRecipeHelper.findRecipesWithOutput(output);
        if (!matchingRecipes.isEmpty()) {
            IRecipe recipe = Iterables.getFirst(matchingRecipes, null);
            return new JournalPageRecipe(new AccessibleRecipeAdapater(
                    recipe,
                    AbstractRecipeAccessor.buildAccessorFor(recipe)));
        }
        List<AbstractAltarRecipe> recipesMatched = AltarRecipeRegistry.getAltarRecipesByOutput(output, TileAltar.AltarLevel.DISCOVERY);
        if(!recipesMatched.isEmpty()) {
            return new JournalPageDiscoveryRecipe((DiscoveryRecipe) Iterables.getFirst(recipesMatched, null));
        }
        recipesMatched = AltarRecipeRegistry.getAltarRecipesByOutput(output, TileAltar.AltarLevel.ATTUNEMENT);
        if(!recipesMatched.isEmpty()) {
            return new JournalPageAttunementRecipe((AttunementRecipe) Iterables.getFirst(recipesMatched, null));
        }
        recipesMatched = AltarRecipeRegistry.getAltarRecipesByOutput(output, TileAltar.AltarLevel.CONSTELLATION_CRAFT);
        if(!recipesMatched.isEmpty()) {
            return new JournalPageConstellationRecipe((ConstellationRecipe) Iterables.getFirst(recipesMatched, null));
        }
        recipesMatched = AltarRecipeRegistry.getAltarRecipesByOutput(output, TileAltar.AltarLevel.TRAIT_CRAFT);
        if(!recipesMatched.isEmpty()) {
            return new JournalPageTraitRecipe((TraitRecipe) Iterables.getFirst(recipesMatched, null));
        }
        return new JournalPageText("astralsorcery.journal.recipe.removalinfo");
    }

    @Nullable
    private static RecoveryType getRecoveryType(IJournalPage page) {
        if(page instanceof JournalPageRecipe) {
            return RecoveryType.CRAFTING;
        }
        if(page instanceof JournalPageLightProximityRecipe) {
            return RecoveryType.LIGHT_CRAFTING;
        }
        if(page instanceof JournalPageDiscoveryRecipe ||
                page instanceof JournalPageAttunementRecipe ||
                page instanceof JournalPageConstellationRecipe ||
                page instanceof JournalPageTraitRecipe) {
            return RecoveryType.ALTAR;
        }
        return null;
    }

    @Nullable
    private static TileAltar.AltarLevel guessAltarLevel(IJournalPage page) {
        if(page instanceof JournalPageTraitRecipe) {
            return TileAltar.AltarLevel.TRAIT_CRAFT;
        }
        if(page instanceof JournalPageConstellationRecipe) {
            return TileAltar.AltarLevel.CONSTELLATION_CRAFT;
        }
        if(page instanceof JournalPageAttunementRecipe) {
            return TileAltar.AltarLevel.ATTUNEMENT;
        }
        if(page instanceof JournalPageDiscoveryRecipe) {
            return TileAltar.AltarLevel.DISCOVERY;
        }
        return null;
    }

    public static enum RecoveryType {

        CRAFTING,
        LIGHT_CRAFTING,
        ALTAR

    }

}
