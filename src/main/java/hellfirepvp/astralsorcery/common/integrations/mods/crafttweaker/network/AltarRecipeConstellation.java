/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeConstellation
 * Created by HellFirePvP
 * Date: 27.02.2017 / 15:58
 */
public class AltarRecipeConstellation extends BaseAltarRecipe {

    AltarRecipeConstellation() {
        super(null, null, 0, 0);
    }

    public AltarRecipeConstellation(ItemHandle[] inputs, ItemStack output, int starlightRequired, int craftingTickTime) {
        super(inputs, output, starlightRequired, craftingTickTime);
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T3_ADD;
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.registerMTAltarRecipe(
                buildRecipeUnsafe(
                        TileAltar.AltarLevel.CONSTELLATION_CRAFT,
                        this.starlightRequired,
                        this.craftingTickTime,
                        this.output,
                        this.inputs)
        );
    }

}
