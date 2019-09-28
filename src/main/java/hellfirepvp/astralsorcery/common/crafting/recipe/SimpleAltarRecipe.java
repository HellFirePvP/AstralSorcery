/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.lib.AltarRecipeEffectsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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

    private ResourceLocation customRecipeType = null;
    private IConstellation focusConstellation = null;
    private List<WrappedIngredient> inputIngredient = new LinkedList<>();

    private Set<AltarRecipeEffect> craftingEffects = new HashSet<>();

    public SimpleAltarRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, ItemStack output, AltarRecipeGrid recipeGrid) {
        super(recipeId);
        this.altarType = altarType;
        this.duration = duration;
        this.starlightRequirement = starlightRequirement;
        this.altarRecipeGrid = recipeGrid;
        this.output = output;

        this.addDefaultEffects();
    }

    private void addDefaultEffects() {
        switch (this.getAltarType()) {
            case RADIANCE:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_TRAIT_FOCUS_CIRCLE);
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_TRAIT_RELAY_HIGHLIGHT);
            case CONSTELLATION:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_CONSTELLATION_LINES);
            case ATTUNEMENT:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_ATTUNEMENT_SPARKLE);
            case DISCOVERY:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_DISCOVERY_CENTRAL_BEAM);
        }
    }

    @Nullable
    public ResourceLocation getCustomRecipeType() {
        return this.customRecipeType;
    }

    public void setCustomRecipeType(@Nullable ResourceLocation customRecipeType) {
        this.customRecipeType = customRecipeType;
    }

    @Nullable
    public IConstellation getFocusConstellation() {
        return focusConstellation;
    }

    @Nonnull
    public List<WrappedIngredient> getTraitInputIngredients() {
        return inputIngredient;
    }

    public AltarRecipeGrid getInputs() {
        return altarRecipeGrid;
    }

    public Collection<AltarRecipeEffect> getCraftingEffects() {
        return craftingEffects;
    }

    public int getDuration() {
        return duration;
    }

    public int getStarlightRequirement() {
        return starlightRequirement;
    }

    public void onRecipeCompletion(TileAltar altar) {}

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ItemStack getOutputForRender(TileAltar altar) {
        return ItemUtils.copyStackWithSize(this.output, this.output.getCount());
    }

    @Nonnull
    public List<ItemStack> getOutputs(TileAltar altar) {
        return Lists.newArrayList(ItemUtils.copyStackWithSize(this.output, this.output.getCount()));
    }

    public void setFocusConstellation(IConstellation focusConstellation) {
        this.focusConstellation = focusConstellation;
    }

    public boolean addTraitInputIngredient(Ingredient i) {
        return this.inputIngredient.add(new WrappedIngredient(i));
    }

    public void addAltarEffect(AltarRecipeEffect effect) {
        this.craftingEffects.add(effect);
    }

    public boolean matches(TileAltar altar) {
        if (!this.getAltarType().isThisLEThan(altar.getAltarType())) {
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

    public void deserializeAdditionalJson(JsonObject recipeObject) throws JsonSyntaxException {}

    public void writeRecipeSync(PacketBuffer buf) {}

    public void readRecipeSync(PacketBuffer buf) {}

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
