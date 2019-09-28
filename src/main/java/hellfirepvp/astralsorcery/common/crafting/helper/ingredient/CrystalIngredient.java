/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.StackList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalIngredient
 * Created by HellFirePvP
 * Date: 28.09.2019 / 10:03
 */
public class CrystalIngredient extends Ingredient {

    private final boolean hasToBeAttuned, hasToBeCelestial;

    public CrystalIngredient(boolean hasToBeAttuned, boolean hasToBeCelestial) {
        super(getItems(hasToBeAttuned, hasToBeCelestial));
        this.hasToBeAttuned = hasToBeAttuned;
        this.hasToBeCelestial = hasToBeCelestial;
    }

    private static Stream<IItemList> getItems(boolean hasToBeAttuned, boolean hasToBeCelestial) {
        List<ItemStack> stacks = new ArrayList<>();
        if (hasToBeAttuned) {
            if (hasToBeCelestial) {
                stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
            } else {
                stacks.add(new ItemStack(ItemsAS.ATTUNED_ROCK_CRYSTAL));
                stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
            }
        } else {
            if (hasToBeCelestial) {
                stacks.add(new ItemStack(ItemsAS.CELESTIAL_CRYSTAL));
                stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
            } else {
                stacks.add(new ItemStack(ItemsAS.ROCK_CRYSTAL));
                stacks.add(new ItemStack(ItemsAS.ATTUNED_ROCK_CRYSTAL));
                stacks.add(new ItemStack(ItemsAS.CELESTIAL_CRYSTAL));
                stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
            }
        }
        return Stream.of(new StackList(stacks));
    }

    public boolean hasToBeAttuned() {
        return hasToBeAttuned;
    }

    public boolean hasToBeCelestial() {
        return hasToBeCelestial;
    }
}
