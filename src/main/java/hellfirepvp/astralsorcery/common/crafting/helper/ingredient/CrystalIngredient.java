/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.lib.IngredientSerializersAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
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

    private final boolean hasToBeAttuned, hasToBeCelestial, canBeAttuned, canBeCelestialCrystal;

    public CrystalIngredient(boolean hasToBeAttuned, boolean hasToBeCelestial) {
        this(hasToBeAttuned, hasToBeCelestial, true, true);
    }

    public CrystalIngredient(boolean hasToBeAttuned, boolean hasToBeCelestial, boolean canBeAttuned, boolean canBeCelestialCrystal) {
        super(getItems(hasToBeAttuned, hasToBeCelestial, canBeAttuned, canBeCelestialCrystal));
        this.hasToBeAttuned = hasToBeAttuned;
        this.hasToBeCelestial = hasToBeCelestial;
        this.canBeAttuned = canBeAttuned;
        this.canBeCelestialCrystal = canBeCelestialCrystal;
    }

    private static Stream<IItemList> getItems(boolean hasToBeAttuned, boolean hasToBeCelestial, boolean canBeAttuned, boolean canBeCelestialCrystal) {
        if (hasToBeAttuned) {
            canBeAttuned = true;
        }
        if (hasToBeCelestial) {
             canBeCelestialCrystal = true;
        }

        List<ItemStack> stacks = new ArrayList<>();
        if (hasToBeAttuned) {
            if (hasToBeCelestial) {
                stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
            } else {
                stacks.add(new ItemStack(ItemsAS.ATTUNED_ROCK_CRYSTAL));
                if (canBeCelestialCrystal) {
                    stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
                }
            }
        } else {
            if (hasToBeCelestial) {
                stacks.add(new ItemStack(ItemsAS.CELESTIAL_CRYSTAL));
                if (canBeAttuned) {
                    stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
                }
            } else {
                stacks.add(new ItemStack(ItemsAS.ROCK_CRYSTAL));
                if (canBeCelestialCrystal) {
                    stacks.add(new ItemStack(ItemsAS.CELESTIAL_CRYSTAL));
                }
                if (canBeAttuned) {
                    stacks.add(new ItemStack(ItemsAS.ATTUNED_ROCK_CRYSTAL));
                    stacks.add(new ItemStack(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL));
                }
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

    public boolean canBeAttuned() {
        return canBeAttuned;
    }

    public boolean canBeCelestialCrystal() {
        return canBeCelestialCrystal;
    }

    @Override
    public JsonElement serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("type", CraftingHelper.getID(IngredientSerializersAS.CRYSTAL_SERIALIZER).toString());
        object.addProperty("hasToBeAttuned", this.hasToBeAttuned());
        object.addProperty("hasToBeCelestial", this.hasToBeCelestial());
        object.addProperty("canBeAttuned", this.canBeAttuned());
        object.addProperty("canBeCelestialCrystal", this.canBeCelestialCrystal());
        return object;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return IngredientSerializersAS.CRYSTAL_SERIALIZER;
    }
}
