/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipe
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:31
 */
public class SimpleAltarRecipe extends CustomMatcherRecipe {

    private final AltarType altarType;
    private final int starlightRequirement;
    private final int duration;
    private final AltarRecipeGrid altarRecipeGrid;
    private final ItemStack output;

    private IConstellation focusConstellation = null;
    private List<Ingredient> inputIngredient = new LinkedList<>();

    public SimpleAltarRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, ItemStack output, AltarRecipeGrid recipeGrid) {
        super(recipeId);
        this.altarType = altarType;
        this.duration = duration;
        this.starlightRequirement = starlightRequirement;
        this.altarRecipeGrid = recipeGrid;
        this.output = output;
    }

    @Nullable
    public IConstellation getFocusConstellation() {
        return focusConstellation;
    }

    public List<Ingredient> getTraitInputIngredients() {
        return inputIngredient;
    }

    public AltarRecipeGrid getInputs() {
        return altarRecipeGrid;
    }

    public int getDuration() {
        return duration;
    }

    public int getStarlightRequirement() {
        return starlightRequirement;
    }

    public List<ItemStack> doItemOutput(TileAltar altar) {
        ItemStack out = ItemUtils.copyStackWithSize(this.output, this.output.getCount());
        if (!out.isEmpty()) {
            altar.dropItemOnTop(out);
        }
        return Lists.newArrayList(out);
    }

    public ItemStack getOutputForRender() {
        return ItemUtils.copyStackWithSize(this.output, this.output.getCount());
    }

    public void setFocusConstellation(IConstellation focusConstellation) {
        this.focusConstellation = focusConstellation;
    }

    public boolean addTraitInputIngredient(Ingredient i) {
        return this.inputIngredient.add(i);
    }

    public boolean matches(TileAltar altar) {
        if (!this.getAltarType().thisIsLowerThan(altar.getAltarType())) {
            return false;
        }
        if (this.getFocusConstellation() != null) {
            IConstellation focus = altar.getFocusedConstellation();
            if (focus == null || !focus.equals(this.getFocusConstellation())) {
                return false;
            }
        }
        if (altar.getStoredStarlight() < this.getStarlightRequirement()) {
            return false;
        }

        return this.altarRecipeGrid.containsInputs(altar.getInventory(), true);
    }

    public AltarType getAltarType() {
        return altarType;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypesAS.TYPE_ALTAR.getType();
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.ALTAR_RECIPE_SERIALIZER;
    }
}
