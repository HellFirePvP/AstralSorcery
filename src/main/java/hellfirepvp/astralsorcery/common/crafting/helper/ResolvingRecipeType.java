/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.RecipeHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResolvingRecipeType
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:52
 */
public class ResolvingRecipeType<C extends IItemHandler, T extends IHandlerRecipe<C>, R extends RecipeCraftingContext<T, C>> {

    private final ResourceLocation id;
    private final Class<T> baseClass;
    private final BiPredicate<T, R> matchFct;
    private final IRecipeType<T> type;

    public ResolvingRecipeType(String name, Class<T> baseClass, BiPredicate<T, R> matchFct) {
        this(AstralSorcery.key(name), baseClass, matchFct);
    }

    public ResolvingRecipeType(ResourceLocation id, Class<T> baseClass, BiPredicate<T, R> matchFct) {
        this.id = id;
        this.baseClass = baseClass;
        this.matchFct = matchFct;
        this.type = new IRecipeType<T>() {
            @Override
            public String toString() {
                return ResolvingRecipeType.this.id.getPath();
            }
        };
        Registry.register(Registry.RECIPE_TYPE, this.getRegistryName(), this.getType());
    }

    @Nonnull
    public Collection<T> getAllRecipes() {
        RecipeManager mgr = RecipeHelper.getRecipeManager();
        if (mgr == null) {
            return Collections.emptyList();
        }
        Collection<IRecipe<IInventory>> recipeSet = mgr.getRecipes(this.type).values();
        List<T> recipes = new ArrayList<>(recipeSet.size());
        for (IRecipe<IInventory> rec : recipeSet) {
            recipes.add((T) rec);
        }
        return recipes;
    }

    public final Class<T> getBaseClass() {
        return baseClass;
    }

    public IRecipeType<T> getType() {
        return type;
    }

    public ResourceLocation getRegistryName() {
        return id;
    }

    @Nullable
    public T findRecipe(R context) {
        return MiscUtils.iterativeSearch(this.getAllRecipes(), (recipe) -> this.matchFct.test(recipe, context));
    }

}
