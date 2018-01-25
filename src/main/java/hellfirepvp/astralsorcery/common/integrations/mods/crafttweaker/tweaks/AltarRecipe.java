/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import crafttweaker.CraftTweakerAPI;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.*;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
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
    public static void removeAltarRecipe(IItemStack output, int altarLevel) {
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
    public static void addDiscoveryAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
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



        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeDiscovery(handles, out, starlightRequired, craftingTickTime));
    }

    @ZenMethod
    public static void addAttunementAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        addAttunmentAltarRecipe(output, starlightRequired, craftingTickTime, inputs);
    }

    @ZenMethod
    public static void addAttunmentAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
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

        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeAttunement(handles, out, starlightRequired, craftingTickTime));
    }

    @ZenMethod
    public static void addConstellationAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
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

        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeConstellation(handles, out, starlightRequired, craftingTickTime));
    }

    @ZenMethod
    public static void addTraitAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs) {
        addTraitAltarRecipe(output, starlightRequired, craftingTickTime, inputs, null);
    }

    @ZenMethod
    public static void addTraitAltarRecipe(IItemStack output, int starlightRequired, int craftingTickTime, IIngredient[] inputs, @Nullable String iRequiredConstellationFocusName) {
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

        ModIntegrationCrafttweaker.recipeModifications.add(new AltarRecipeTrait(handles, out, starlightRequired, craftingTickTime, cst));
    }

    private static boolean isEmpty(IIngredient[] inputs) {
        for (IIngredient i : inputs) {
            if(i != null) return false;
        }
        return true;
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
