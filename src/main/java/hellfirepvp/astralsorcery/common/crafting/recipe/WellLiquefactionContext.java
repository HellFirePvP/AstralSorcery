/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import hellfirepvp.astralsorcery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellLiquefactionContext
 * Created by HellFirePvP
 * Date: 01.07.2019 / 00:04
 */
public class WellLiquefactionContext extends RecipeCraftingContext<WellLiquefaction, IItemHandler> {

    private final ItemStack input;

    public WellLiquefactionContext(TileWell well) {
        this(well.getCatalyst());
    }

    public WellLiquefactionContext(ItemStack input) {
        this.input = input;
    }

    public ItemStack getInput() {
        return input;
    }
}
