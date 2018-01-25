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
 * Class: AltarRecipeAttunement
 * Created by HellFirePvP
 * Date: 27.02.2017 / 15:57
 */
public class AltarRecipeAttunement extends BaseAltarRecipe {

    AltarRecipeAttunement() {
        super(null, null, 0, 0);
    }

    public AltarRecipeAttunement(ItemHandle[] inputs, ItemStack output, int starlightRequired, int craftingTickTime) {
        super(inputs, output, starlightRequired, craftingTickTime);
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T2_ADD;
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.registerMTAltarRecipe(
                buildRecipeUnsafe(
                        TileAltar.AltarLevel.ATTUNEMENT,
                        this.starlightRequired,
                        this.craftingTickTime,
                        this.output,
                        this.inputs)
        );
    }

}
