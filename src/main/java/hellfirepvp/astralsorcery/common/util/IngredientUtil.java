/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IngredientUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IngredientUtil {

    @Nonnull
    public static ItemStack getRandomDisplayStack(IngredientBridge ingredient, long tick) {
        return getRandomDisplayStack(ingredient.getItems().toList(), tick);
    }

    @Nonnull
    public static ItemStack getRandomDisplayStack(Ingredient ingredient, long tick) {
        return getRandomDisplayStack(Arrays.asList(ingredient.getItems()), tick);
    }

    @Nonnull
    public static ItemStack getRandomDisplayStack(SizedFluidIngredient ingredient, long tick) {
        return getRandomDisplayStack(Arrays.stream(ingredient.getFluids()).map(FluidUtil::getFilledBucket).toList(), tick);
    }

    @Nonnull
    public static ItemStack getRandomDisplayStack(List<ItemStack> stacks, long tick) {
        if (stacks.isEmpty()) return ItemStack.EMPTY;
        int mod = (int) ((tick / 20L) % stacks.size());
        return stacks.get(Mth.clamp(mod, 0, stacks.size() - 1));
    }

    @Nullable
    public static TagKey<Item> guessIngredientTag(Ingredient ingredient) {
        ItemStack[] stacks = ingredient.getItems();
        if (stacks.length == 0) return null;

        TagKey<Item> match = null;
        int matchSize = 0;

        ItemStack first = stacks[0];
        for (TagKey<Item> key : first.getTags().toList()) {
            boolean containsAllItems = true;
            HolderSet.Named<Item> tagSet = BuiltInRegistries.ITEM.getTag(key).orElse(null);
            if (tagSet == null) continue;
            for (Holder<Item> itemInTag : tagSet.stream().toList()) {
                if (!ingredient.test(new ItemStack(itemInTag))) {
                    containsAllItems = false;
                    break;
                }
            }
            if (containsAllItems) {
                int tagSize = tagSet.stream().toList().size();
                if (match == null || tagSize > matchSize) {
                    match = key;
                    matchSize = tagSize;
                }
            }
        }

        return match;
    }

    public static List<Fluid> guessFluids(FluidIngredient ingredient) {
        List<Fluid> fluids = new ArrayList<>();
        for (FluidStack stack : ingredient.getStacks()) {
            if (!fluids.contains(stack.getFluid())) {
                fluids.add(stack.getFluid());
            }
        }
        fluids.sort(Comparator.comparing(Fluid::toString));
        return fluids;
    }
}
