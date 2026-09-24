/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResolvingRecipeTypeRegistryObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ResolvingRecipeTypeRegistryObject<T extends Recipe<?>>(DeferredHolder<RecipeType<?>, RecipeType<T>> holder, Function<T, ItemStack> outputMatchProvider) implements Supplier<RecipeType<T>> {

    @Override
    public RecipeType<T> get() {
        return this.holder().get();
    }

    public ResourceKey<RecipeType<?>> getKey() {
        return this.holder().getKey();
    }
}
