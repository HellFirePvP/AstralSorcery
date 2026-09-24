/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LazyRecipeHolder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LazyRecipeHolder<T extends Recipe<?>> {

    private final ResourceLocation recipeId;
    private T recipe;

    private LazyRecipeHolder(ResourceLocation recipeId) {
        this(recipeId, null);
    }

    private LazyRecipeHolder(ResourceLocation recipeId, T recipe) {
        this.recipeId = recipeId;
        this.recipe = recipe;
    }

    public static <R extends Recipe<?>> Codec<LazyRecipeHolder<R>> typedCodec(Supplier<RecipeType<R>> typeHint) {
        return typedCodec(typeHint.get());
    }

    public static <R extends Recipe<?>> Codec<LazyRecipeHolder<R>> typedCodec(RecipeType<R> typeHint) {
        return RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("recipe_id").forGetter(LazyRecipeHolder::getRecipeId)
        ).apply(inst, LazyRecipeHolder::new));
    }

    public static <T extends Recipe<?>> LazyRecipeHolder<T> of(ResourceLocation recipeId) {
        return new LazyRecipeHolder<>(recipeId);
    }

    public static <T extends Recipe<?>> LazyRecipeHolder<T> of(RecipeHolder<T> recipeHolder) {
        return new LazyRecipeHolder<>(recipeHolder.id(), recipeHolder.value());
    }

    public ResourceLocation getRecipeId() {
        return this.recipeId;
    }

    public T resolveRecipe(Level level) {
        if (this.recipe == null && level != null) {
            this.recipe = MiscUtil.cast(level.getRecipeManager().byKey(this.recipeId)
                    .map(RecipeHolder::value)
                    .orElse(null));
        }
        return this.recipe;
    }
}
