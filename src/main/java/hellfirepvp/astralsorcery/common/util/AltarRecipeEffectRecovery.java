/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.crafting.ISpecialCraftingEffects;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeEffectRecovery
 * Created by HellFirePvP
 * Date: 29.10.2017 / 14:01
 */
public class AltarRecipeEffectRecovery {

    public static void attemptRecipeRecovery() {
        for (TileAltar.AltarLevel al : TileAltar.AltarLevel.values()) {
            for (AbstractAltarRecipe ar : AltarRecipeRegistry.getRecipesForLevel(al)) {
                if(!(ar instanceof ISpecialCraftingEffects)) {
                    ISpecialCraftingEffects eff = AltarRecipeRegistry.shouldHaveSpecialEffects(ar);
                    if(eff != null) {
                        ar.setSpecialEffectRecovery(eff.copyNewEffectInstance());
                    }
                }
            }
        }
    }

}
