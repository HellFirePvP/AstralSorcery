/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe;

import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CustomRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class CustomRecipe<R extends CustomRecipe<R, T>, T extends CustomRecipeInput> implements Recipe<T> {

    private String group = "";

    public abstract boolean consumeInputs(T input, HolderLookup.Provider registries);

    public abstract void createOutput(T input, HolderLookup.Provider registries);

    @Override
    public ItemStack assemble(T input, HolderLookup.Provider registries) {
        this.createOutput(input, registries);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    public abstract ResolvingRecipeTypeRegistryObject<R> getRecipeType();

    public abstract Supplier<? extends RecipeSerializer<R>> getRecipeSerializer();

    @Override
    public final RecipeType<?> getType() {
        return this.getRecipeType().get();
    }

    @Override
    public final RecipeSerializer<?> getSerializer() {
        return this.getRecipeSerializer().get();
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getGroup() {
        return this.group;
    }
}
