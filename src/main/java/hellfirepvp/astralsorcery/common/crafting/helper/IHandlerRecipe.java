/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IHandlerRecipe
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:39
 */
public interface IHandlerRecipe<I extends IItemHandler> extends IRecipe<IInventory> {

    boolean matches(I handler, World world);

    @Override
    default boolean matches(IInventory inv, World worldIn) {
        return false;
    }
}
