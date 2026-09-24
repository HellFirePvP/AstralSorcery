/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe;

import hellfirepvp.astralsorcery.common.util.data.LazyRecipeHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ActiveRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ActiveRecipe<T extends Recipe<?>> {

    private final LazyRecipeHolder<T> recipeReference;

    protected ActiveRecipe(LazyRecipeHolder<T> recipeReference) {
        this.recipeReference = recipeReference;
    }

    protected LazyRecipeHolder<T> getRecipeReference() {
        return this.recipeReference;
    }

    public final ResourceLocation getRecipeId() {
        return this.recipeReference.getRecipeId();
    }

    public Optional<T> getRecipe(Level level) {
        return Optional.ofNullable(this.recipeReference.resolveRecipe(level));
    }

    public boolean isValid(Level level) {
        return this.getRecipe(level).isPresent();
    }
}
