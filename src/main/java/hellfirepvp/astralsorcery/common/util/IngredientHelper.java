/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IngredientHelper
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:18
 */
public class IngredientHelper {

    @OnlyIn(Dist.CLIENT)
    public static ItemStack getRandomVisibleStack(Ingredient ingredient) {
        return getRandomVisibleStack(ingredient, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public static ItemStack getRandomVisibleStack(Ingredient ingredient, long tick) {
        List<ItemStack> applicable = getVisibleItemStacks(ingredient);
        if (applicable.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int mod = (int) ((tick / 20L) % applicable.size());
        return applicable.get(MathHelper.clamp(mod, 0, applicable.size() - 1));
    }

    @OnlyIn(Dist.CLIENT)
    public static List<ItemStack> getVisibleItemStacks(Ingredient ingredient) {
        if (ingredient.hasNoMatchingItems()) {
            return Collections.emptyList();
        }
        return Arrays.asList(ingredient.getMatchingStacks());
    }

    @Nullable
    public static Tag<Item> guessTag(Ingredient ingredient) {
        ItemStack[] stacks = ingredient.getMatchingStacks();
        if (stacks.length == 0) {
            return null;
        }
        List<Tag<Item>> applicableTags = new ArrayList<>();
        ItemStack first = stacks[0];
        for (ResourceLocation key : first.getItem().getTags()) {
            Tag<Item> wrapper = new ItemTags.Wrapper(key);

            boolean containsAllItems = true;
            for (Item itemInTag : wrapper.getAllElements()) {
                if (!ingredient.test(new ItemStack(itemInTag))) {
                    containsAllItems = false;
                    break;
                }
            }
            if (containsAllItems) {
                applicableTags.add(wrapper);
            }
        }

        return applicableTags.stream()
                .max(Comparator.comparingInt(tag -> tag.getAllElements().size()))
                .orElse(null);
    }

}
