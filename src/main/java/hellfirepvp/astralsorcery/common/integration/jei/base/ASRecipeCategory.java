/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ASRecipeCategory
 * Created by HellFirePvP
 * Date: 25.09.2026 / 14:53
 */
public abstract class ASRecipeCategory<T> implements IRecipeCategory<T> {

    private static final ResourceLocation INFO_ICON = AstralSorcery.key("textures/screen/jei/info_icon.png");

    protected final Component name;
    protected final int width, height;
    protected final IDrawable categoryIcon;

    protected ASRecipeCategory(int width, int height, IGuiHelper helper, ItemLike icon) {
        this(width, height, helper, new ItemStack(icon));
    }

    protected ASRecipeCategory(int width, int height, IGuiHelper helper, ItemStack icon) {
        this(width, height, helper.createDrawableItemStack(icon));
    }

    protected ASRecipeCategory(int width, int height, IDrawable categoryIcon) {
        this.name = named(this.getRecipeType().getUid().getPath());
        this.width = width;
        this.height = height;
        this.categoryIcon = categoryIcon;
    }

    protected static Component named(String translationKey) {
        return Component.translatable(String.format("jei.astralsorcery.category.%s", translationKey));
    }

    protected static <T> RecipeType<T> makeType(String name, Class<T> clazz) {
        return new RecipeType<>(AstralSorcery.key(name), clazz);
    }

    protected static IDrawable createBackground(IGuiHelper helper, ResourceLocation key, int width, int height) {
        return helper.drawableBuilder(key, 0, 0, width, height)
                .setTextureSize(width, height)
                .build();
    }

    protected static IDrawable createInfoIcon(IGuiHelper helper) {
        return helper.drawableBuilder(INFO_ICON, 0, 0, 12, 12)
                .setTextureSize(12, 12)
                .build();
    }

    @Override
    public Component getTitle() {
        return this.name;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return this.categoryIcon;
    }

    public abstract List<ItemStack> provideCatalyst();

    public abstract List<T> provideRecipes(RecipeManager recipeManager, IRecipeRegistration register);

    protected static <I extends RecipeInput, R extends Recipe<I>> List<R> provideRawRecipes(RecipeManager mgr, ResolvingRecipeTypeRegistryObject<R> type) {
        return mgr.getAllRecipesFor(type.get()).stream().map(RecipeHolder::value).toList();
    }
}
