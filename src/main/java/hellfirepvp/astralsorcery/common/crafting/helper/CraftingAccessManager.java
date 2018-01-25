/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CraftingAccessManager
 * Created by HellFirePvP
 * Date: 15.02.2017 / 14:23
 */
public class CraftingAccessManager {

    private static List<Object> lastReloadRemovedRecipes = new LinkedList<>();

    private static boolean completed = false;
    public static boolean ignoreJEI = true;

    public static boolean hasCompletedSetup() {
        return completed;
    }

    /*
     * Called whenever the underlying cached recipes change
     * -> at post startup
     * -> when MT changes happen (/mt reload and the like)
     */
    public static void compile() {
        AltarRecipeRegistry.compileRecipes();
        InfusionRecipeRegistry.compileRecipes();
        completed = true;
    }

    public static void clearModifications() {
        //Unregister changes from JEI
        removeAll(InfusionRecipeRegistry.mtRecipes);
        removeAll(LightOreTransmutations.mtTransmutations);
        removeAll(WellLiquefaction.mtLiquefactions.values());
        removeAll(GrindstoneRecipeRegistry.mtRecipes);
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            removeAll(AltarRecipeRegistry.mtRecipes.get(al));
        }

        //Clear dirty maps
        InfusionRecipeRegistry.mtRecipes.clear();
        InfusionRecipeRegistry.recipes.clear();
        LightOreTransmutations.mtTransmutations.clear();
        WellLiquefaction.mtLiquefactions.clear();
        GrindstoneRecipeRegistry.mtRecipes.clear();
        AltarRecipeRegistry.mtRecipes.clear();
        AltarRecipeRegistry.recipes.clear();

        //Add removed recipes back to JEI
        for (Object removedPreviously : lastReloadRemovedRecipes) {
            addRecipe(removedPreviously);
        }
        lastReloadRemovedRecipes.clear();

        //Setup registry maps again
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            AltarRecipeRegistry.mtRecipes.put(al, new LinkedList<>());
            AltarRecipeRegistry.recipes.put(al, new LinkedList<>());
        }

        //Loading default configurations how it'd be without Minetweaker
        InfusionRecipeRegistry.loadFromFallback();
        AltarRecipeRegistry.loadFromFallback();
        LightOreTransmutations.loadFromFallback();
        WellLiquefaction.loadFromFallback();
        GrindstoneRecipeRegistry.loadFromFallback();
    }

    public static void registerMTInfusion(AbstractInfusionRecipe recipe) {
        InfusionRecipeRegistry.mtRecipes.add(recipe);
        addRecipe(recipe);
    }

    public static void registerMTAltarRecipe(AbstractAltarRecipe recipe) {
        TileAltar.AltarLevel al = recipe.getNeededLevel();
        AltarRecipeRegistry.mtRecipes.get(al).add(recipe);
        addRecipe(recipe);
    }

    public static void tryRemoveInfusionByOutput(ItemStack output) {
        markForRemoval(InfusionRecipeRegistry.removeFindRecipeByOutput(output));
    }

    public static void tryRemoveAltarRecipeByOutputAndLevel(ItemStack output, TileAltar.AltarLevel altarLevel) {
        markForRemoval(AltarRecipeRegistry.removeFindRecipeByOutputAndLevel(output, altarLevel));
    }

    public static void addMTTransmutation(ItemStack in, ItemStack out, double cost) {
        IBlockState stateIn = ItemUtils.createBlockState(in);
        IBlockState stateOut = ItemUtils.createBlockState(out);
        if(stateIn != null && stateOut != null) {
            LightOreTransmutations.Transmutation tr = new LightOreTransmutations.Transmutation(stateIn, stateOut, in, out, cost);
            tr = LightOreTransmutations.registerTransmutation(tr);
            if (tr != null) {
                //addRecipe(tr); Is picked up by default logic
                LightOreTransmutations.mtTransmutations.add(tr);
            }
        }
    }

    public static void removeMTTransmutation(ItemStack match, boolean matchMeta) {
        markForRemoval(LightOreTransmutations.tryRemoveTransmutation(match, matchMeta));
    }

    public static void addMTLiquefaction(ItemStack catalystIn, Fluid producedIn, float productionMultiplier, float shatterMultiplier, Color color) {
        if(WellLiquefaction.getLiquefactionEntry(catalystIn) != null) {
            return;
        }
        WellLiquefaction.LiquefactionEntry le = new WellLiquefaction.LiquefactionEntry(catalystIn, producedIn, productionMultiplier, shatterMultiplier, color);
        WellLiquefaction.mtLiquefactions.put(catalystIn, le);
        addRecipe(le);
    }

    public static void removeMTLiquefaction(ItemStack match, @Nullable Fluid fluid) {
        markForRemoval(WellLiquefaction.tryRemoveLiquefaction(match, fluid));
    }

    public static void addGrindstoneRecipe(ItemHandle in, ItemStack out, int chance) {
        GrindstoneRecipe gr = new GrindstoneRecipe(in, out, chance);

        GrindstoneRecipeRegistry.mtRecipes.add(gr);
        addRecipe(gr);
    }

    public static void removeGrindstoneRecipe(ItemStack out) {
        markForRemoval(GrindstoneRecipeRegistry.tryRemoveGrindstoneRecipe(out));
    }

    /*
     ******************************************
     *           JEI interact
     ******************************************
     */
    private static void addRecipe(Object o) {
        if(!ignoreJEI && Mods.JEI.isPresent()) {
            ModIntegrationJEI.addRecipe(o);
        }
    }

    private static void removeAll(Collection objects) {
        if(!ignoreJEI && Mods.JEI.isPresent()) {
            for (Object o : objects) {
                ModIntegrationJEI.removeRecipe(o);
            }
        }
    }

    private static void markForRemoval(Object o) {
        if(!ignoreJEI && o != null) {
            lastReloadRemovedRecipes.add(o);
            if(Mods.JEI.isPresent()) {
                ModIntegrationJEI.removeRecipe(o);
            }
        }
    }
}
