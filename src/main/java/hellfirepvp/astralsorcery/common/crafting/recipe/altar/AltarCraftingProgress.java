/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.nbt.CompoundNBT;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarCraftingProgress
 * Created by HellFirePvP
 * Date: 13.08.2019 / 21:05
 */
public interface AltarCraftingProgress {

    boolean tryProcess(TileAltar altar, ActiveSimpleAltarRecipe currentRecipe, CompoundNBT data, int ticksCrafting, int totalCraftingTime);

}
