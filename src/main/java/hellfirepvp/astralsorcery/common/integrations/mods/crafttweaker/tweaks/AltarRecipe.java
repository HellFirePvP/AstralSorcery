/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.*;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipe
 * Created by HellFirePvP
 * Date: 27.02.2017 / 14:02
 */
@ZenClass("mods.astralsorcery.Altar")
public class AltarRecipe extends BaseTweaker {

    protected static final String name = "AstralSorcery Altar Crafting";

    public static int SLOT_COUNT_T1 = 9;
    public static int SLOT_COUNT_T2 = 13;
    public static int SLOT_COUNT_T3 = 21;
    public static int SLOT_COUNT_T4 = 25;

    @ZenMethod
    public static void removeAltarRecipe(String recipeRegistryName) {
        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeRemove(recipeRegistryName));
    }

    @ZenMethod
    @Deprecated
    public static void removeAltarRecipe(IItemStack output, int altarLevel) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'removeAltarRecipe'! Use the new method to remove recipes by their registry-name!");
        CraftTweakerAPI.logError("[" + name + "] Enable the F3-Debug screen and hover over the receipe output of an altar recipe to see existing ones!");

        ItemStack out = convertToItemStack(output);
        if(out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal due to invalid output.");
            return;
        }

        if(altarLevel < 0 || altarLevel >= TileAltar.AltarLevel.values().length) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal - No altar level with index " + altarLevel);
            return;
        }

        TileAltar.AltarLevel al = TileAltar.AltarLevel.values()[altarLevel];
        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeRemove(out, al));
    }

    @ZenMethod
    public static void addDiscoveryAltarRecipe(String recipeRegistryName, IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        if(!matchNeededSlots(inputs, TileAltar.AltarLevel.DISCOVERY)) return;

        ItemStack out = convertToItemStack(output);
        if(out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-addition due to invalid output itemstack.");
            return;
        }

        ItemHandle[] handles = new ItemHandle[SLOT_COUNT_T1];
        for (int i = 0; i < SLOT_COUNT_T1; i++) {
            handles[i] = convertToHandle(inputs[i]);
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeDiscovery(recipeRegistryName, handles, out, starlightRequired, craftingTickTime));
    }

    @ZenMethod
    @Deprecated
    public static void addDiscoveryAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addDiscoveryAltarRecipe'! Use the new method with an additional registry-name parameter!");
        CraftTweakerAPI.logError("[" + name + "] Enable the F3-Debug screen and hover over the receipe output of an altar recipe to see existing ones!");
        CraftTweakerAPI.logError("[" + name + "] Should you try to replace an existing crafting recipe, make sure you use the same recipe name!");
        addDiscoveryAltarRecipe("ct/null", output, starlightRequired, craftingTickTime, inputs);
    }

    @ZenMethod
    public static void addAttunementAltarRecipe(String recipeRegistryName, IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        if(!matchNeededSlots(inputs, TileAltar.AltarLevel.ATTUNEMENT)) return;

        ItemStack out = convertToItemStack(output);
        if(out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-addition due to invalid output itemstack.");
            return;
        }

        ItemHandle[] handles = new ItemHandle[SLOT_COUNT_T2];
        for (int i = 0; i < SLOT_COUNT_T2; i++) {
            handles[i] = convertToHandle(inputs[i]);
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeAttunement(recipeRegistryName, handles, out, starlightRequired, craftingTickTime));
    }

    @ZenMethod
    @Deprecated
    public static void addAttunmentAltarRecipe(String recipeRegistryName, IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addAttunmentAltarRecipe' with the TYPO! This method will be removed in an upcoming update!");
        addAttunementAltarRecipe(recipeRegistryName, output, starlightRequired, craftingTickTime, inputs);
    }

    @ZenMethod
    @Deprecated
    public static void addAttunementAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addAttunementAltarRecipe'! Use the new method with an additional registry-name parameter!");
        CraftTweakerAPI.logError("[" + name + "] Enable the F3-Debug screen and hover over the receipe output of an altar recipe to see existing ones!");
        CraftTweakerAPI.logError("[" + name + "] Should you try to replace an existing crafting recipe, make sure you use the same recipe name!");
        addAttunmentAltarRecipe("ct/null", output, starlightRequired, craftingTickTime, inputs);
    }

    @ZenMethod
    @Deprecated
    public static void addAttunmentAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addAttunmentAltarRecipe'! Use the new method with an additional registry-name parameter!");
        CraftTweakerAPI.logError("[" + name + "] Enable the F3-Debug screen and hover over the receipe output of an altar recipe to see existing ones!");
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addAttunmentAltarRecipe' with the TYPO! This method will be removed in an upcoming update!");
        addAttunementAltarRecipe(output, starlightRequired, craftingTickTime, inputs);
    }

    @ZenMethod
    public static void addConstellationAltarRecipe(String recipeRegistryName, IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        if(!matchNeededSlots(inputs, TileAltar.AltarLevel.CONSTELLATION_CRAFT)) return;

        ItemStack out = convertToItemStack(output);
        if(out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-addition due to invalid output itemstack.");
            return;
        }

        ItemHandle[] handles = new ItemHandle[SLOT_COUNT_T3];
        for (int i = 0; i < SLOT_COUNT_T3; i++) {
            handles[i] = convertToHandle(inputs[i]);
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeConstellation(recipeRegistryName, handles, out, starlightRequired, craftingTickTime));
    }

    @ZenMethod
    @Deprecated
    public static void addConstellationAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addConstellationAltarRecipe'! Use the new method with an additional registry-name parameter!");
        CraftTweakerAPI.logError("[" + name + "] Enable the F3-Debug screen and hover over the receipe output of an altar recipe to see existing ones!");
        CraftTweakerAPI.logError("[" + name + "] Should you try to replace an existing crafting recipe, make sure you use the same recipe name!");
        addConstellationAltarRecipe("ct/null", output, starlightRequired, craftingTickTime, inputs);
    }

    @ZenMethod
    public static void addTraitAltarRecipe(String recipeRegistryName, IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs, @Nullable String iRequiredConstellationFocusName) {
        if(!matchNeededSlots(inputs, TileAltar.AltarLevel.TRAIT_CRAFT)) return;

        ItemStack out = convertToItemStack(output);
        if(out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-addition due to invalid output itemstack.");
            return;
        }

        IConstellation cst = null;
        if(iRequiredConstellationFocusName != null) {
            cst = ConstellationRegistry.getConstellationByName(iRequiredConstellationFocusName);
            if(cst == null) {
                CraftTweakerAPI.logError("[" + name + "] Skipping recipe-addition due to unknown constellation: " + iRequiredConstellationFocusName);
                return;
            }
        }

        ItemHandle[] handles = new ItemHandle[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            handles[i] = convertToHandle(inputs[i]);
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeTrait(recipeRegistryName, handles, out, starlightRequired, craftingTickTime, cst));
    }

    @ZenMethod
    public static void addTraitAltarRecipe(String recipeRegistryName, IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        addTraitAltarRecipe(recipeRegistryName, output, starlightRequired, craftingTickTime, inputs, null);
    }

    @ZenMethod
    @Deprecated
    public static void addTraitAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addTraitAltarRecipe'! Use the new method with an additional registry-name parameter!");
        CraftTweakerAPI.logError("[" + name + "] Enable the F3-Debug screen and hover over the receipe output of an altar recipe to see existing ones!");
        CraftTweakerAPI.logError("[" + name + "] Should you try to replace an existing crafting recipe, make sure you use the same recipe name!");
        addTraitAltarRecipe("ct/null", output, starlightRequired, craftingTickTime, inputs, null);
    }

    @ZenMethod
    @Deprecated
    public static void addTraitAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs, @Nullable String iRequiredConstellationFocusName) {
        CraftTweakerAPI.logError("[" + name + "] Using deprecated 'addTraitAltarRecipe'! Use the new method with an additional registry-name parameter!");
        CraftTweakerAPI.logError("[" + name + "] Enable the F3-Debug screen and hover over the receipe output of an altar recipe to see existing ones!");
        CraftTweakerAPI.logError("[" + name + "] Should you try to replace an existing crafting recipe, make sure you use the same recipe name!");
        addTraitAltarRecipe("ct/null", output, starlightRequired, craftingTickTime, inputs, iRequiredConstellationFocusName);
    }

    private static boolean matchNeededSlots(IIngredient[] inputs, TileAltar.AltarLevel altarLevel) {
        int reqSlots;
        switch (altarLevel) {
            case DISCOVERY:
                reqSlots = SLOT_COUNT_T1;
                break;
            case ATTUNEMENT:
                reqSlots = SLOT_COUNT_T2;
                break;
            case CONSTELLATION_CRAFT:
                reqSlots = SLOT_COUNT_T3;
                break;
            case TRAIT_CRAFT:
            default:
                reqSlots = SLOT_COUNT_T4;
                break;
        }
        if(inputs == null || inputs.length < reqSlots) {
            CraftTweakerAPI.logError("[" + name + "] Not enough slots defined for altar level " + altarLevel.name());
            return false;
        }
        return true;
    }

}
