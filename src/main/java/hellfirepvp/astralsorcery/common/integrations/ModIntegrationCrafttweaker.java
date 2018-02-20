/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import crafttweaker.CraftTweakerAPI;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.SerializeableRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.*;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationCrafttweaker
 * Created by HellFirePvP
 * Date: 16.07.2017 / 17:38
 */
public class ModIntegrationCrafttweaker {

    public static ModIntegrationCrafttweaker instance = new ModIntegrationCrafttweaker();
    public static List<SerializeableRecipe> recipeModifications = new LinkedList<>();

    private ModIntegrationCrafttweaker() {}

    public void load() {
        CraftTweakerAPI.registerClass(InfusionRecipe.class);
        CraftTweakerAPI.registerClass(GrindstoneRecipe.class);
        CraftTweakerAPI.registerClass(LightTransmutations.class);
        CraftTweakerAPI.registerClass(AltarRecipe.class);
        CraftTweakerAPI.registerClass(WellRecipe.class);
    }

    public void pushChanges() {
        AstralSorcery.log.info("[AstralSorcery] Got " + recipeModifications.size() + " recipe modifications from CraftTweaker. - Applying...");
        for (SerializeableRecipe recipe : recipeModifications) {
            try {
                recipe.applyRecipe();
            } catch (Exception exc) {
                AstralSorcery.log.error("[AstralSorcery] Couldn't apply RecipeModification for type " + recipe.getType().name().toLowerCase());
                exc.printStackTrace();
            }
        }
        AstralSorcery.log.info("[AstralSorcery] Recipe changes applied.");
    }

}
