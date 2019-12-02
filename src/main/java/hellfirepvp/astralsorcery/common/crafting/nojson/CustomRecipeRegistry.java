package hellfirepvp.astralsorcery.common.crafting.nojson;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomRecipeRegistry
 * Created by HellFirePvP
 * Date: 02.12.2019 / 18:54
 */
public abstract class CustomRecipeRegistry<R extends CustomRecipe> {

    private Map<ResourceLocation, R> recipes = new HashMap<>();

    public abstract void init();

    public void register(@Nonnull R recipe) {
        this.recipes.put(recipe.getKey(), recipe);
    }

    @Nullable
    public R getRecipe(ResourceLocation key) {
        return this.recipes.get(key);
    }

    @Nonnull
    public Collection<R> getRecipes() {
        return recipes.values();
    }
}
