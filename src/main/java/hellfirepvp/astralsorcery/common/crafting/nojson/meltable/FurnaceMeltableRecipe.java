/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.meltable;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FurnaceMeltableRecipe
 * Created by HellFirePvP
 * Date: 30.11.2019 / 17:53
 */
public class FurnaceMeltableRecipe extends ItemMeltableRecipe {

    public FurnaceMeltableRecipe() {
        super(AstralSorcery.key("all_furnace_meltable"),
                (world, pos, state) -> !RecipeHelper.findSmeltingResult(world, state).isPresent(),
                (worldPos, state) -> RecipeHelper.findSmeltingResult(worldPos.getWorld(), state).orElse(ItemStack.EMPTY));
    }
}
