/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe;

import hellfirepvp.astralsorcery.common.component.ColorComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RecipeChangeColor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RecipeChangeColor extends CustomRecipe {

    private final DeferredHolder<RecipeSerializer<?>, ? extends RecipeSerializer<?>> serializer;
    private final DeferredItem<?> targetItem;

    public RecipeChangeColor(CraftingBookCategory category,
                             DeferredHolder<RecipeSerializer<?>, ? extends RecipeSerializer<?>> serializer,
                             DeferredItem<?> targetItem) {
        super(category);
        this.serializer = serializer;
        this.targetItem = targetItem;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.findRecipeComponents(input).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return this.findRecipeComponents(input).map(tpl -> {
            ItemStack result = tpl.getA().copyWithCount(1);
            result.set(DataComponentsAS.COLOR, new ColorComponent(ColorReference.Dye.of(tpl.getB())));
            return result;
        }).orElse(ItemStack.EMPTY);
    }

    private Optional<Tuple<ItemStack, DyeColor>> findRecipeComponents(CraftingInput input) {
        ItemStack foundItem = ItemStack.EMPTY;
        DyeColor foundColor = null;
        int foundItems = 0;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) continue;
            foundItems++;

            if (stack.is(this.targetItem)) {
                foundItem = stack;
            } else {
                DyeColor color = DyeColor.getColor(stack);
                if (color != null) {
                    foundColor = color;
                }
            }
        }

        if (foundItem.isEmpty() || foundColor == null || foundItems != 2) {
            return Optional.empty();
        } else {
            return Optional.of(new Tuple<>(foundItem, foundColor));
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer.get();
    }

    public static class IlluminationWandChangeColor extends RecipeChangeColor {

        public IlluminationWandChangeColor(CraftingBookCategory category) {
            super(category, RecipeTypesAS.ILLUMINATION_WAND_CHANGE_COLOR_SERIALIZER, ItemsAS.ILLUMINATION_WAND);
        }
    }

    public static class CelestialGatewayChangeColor extends RecipeChangeColor {

        public CelestialGatewayChangeColor(CraftingBookCategory category) {
            super(category, RecipeTypesAS.CELESTIAL_GATEWAY_CHANGE_COLOR_SERIALIZER, ItemsAS.BLOCK_CELESTIAL_GATEWAY);
        }
    }
}
