/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEICategory
 * Created by HellFirePvP
 * Date: 05.09.2020 / 12:38
 */
public abstract class JEICategory<T> implements IRecipeCategory<T> {

    private final String locTitle;
    private final ResourceLocation uid;

    public JEICategory(ResourceLocation categoryId) {
        this(category(categoryId), categoryId);
    }

    public JEICategory(String unlocTitle, ResourceLocation uid) {
        this.locTitle = I18n.format(unlocTitle);
        this.uid = uid;
    }

    protected static String category(ResourceLocation categoryId) {
        return String.format("jei.category.%s.%s", categoryId.getNamespace(), categoryId.getPath());
    }

    protected static List<ItemStack> ingredientStacks(Ingredient ingredient) {
        return Arrays.asList(ingredient.getMatchingStacks());
    }

    protected static void initFluidInput(IGuiFluidStackGroup group, int index, int x, int y) {
        group.init(index, true, x + 1, y + 1, 16, 16, 1000, false, null);
    }

    protected static void initFluidOutput(IGuiFluidStackGroup group, int index, int x, int y) {
        group.init(index, false, x + 1, y + 1, 16, 16, 1000, false, null);
    }

    public abstract Collection<T> getRecipes();

    @Override
    public ResourceLocation getUid() {
        return uid;
    }

    @Override
    public String getTitle() {
        return locTitle;
    }
}
