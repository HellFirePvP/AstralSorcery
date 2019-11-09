/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.CustomAltarRecipeHandler;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.AltarRecipeEffectsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipe
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:31
 */
public class SimpleAltarRecipe extends CustomMatcherRecipe implements GatedRecipe.Progression {

    private final AltarType altarType;
    private final int starlightRequirement;
    private final int duration;
    private final AltarRecipeGrid altarRecipeGrid;

    private final List<ItemStack> outputs = new LinkedList<>();
    private ResourceLocation customRecipeType = null;
    private IConstellation focusConstellation = null;
    private List<WrappedIngredient> inputIngredient = new LinkedList<>();

    private Set<AltarRecipeEffect> craftingEffects = new HashSet<>();

    public SimpleAltarRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, AltarRecipeGrid recipeGrid) {
        super(recipeId);
        this.altarType = altarType;
        this.duration = duration;
        this.starlightRequirement = starlightRequirement;
        this.altarRecipeGrid = recipeGrid;

        this.addDefaultEffects();
    }

    private void addDefaultEffects() {
        switch (this.getAltarType()) {
            case RADIANCE:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_TRAIT_FOCUS_CIRCLE);
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_TRAIT_RELAY_HIGHLIGHT);
                this.addAltarEffect(AltarRecipeEffectsAS.ALTAR_DEFAULT_SPARKLE);
            case CONSTELLATION:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_CONSTELLATION_LINES);
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_CONSTELLATION_FINISH);
            case ATTUNEMENT:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_ATTUNEMENT_SPARKLE);
            case DISCOVERY:
                this.addAltarEffect(AltarRecipeEffectsAS.BUILTIN_DISCOVERY_CENTRAL_BEAM);
        }
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return this.getAltarType().getAssociatedTier();
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
    public ItemStack getOutputForRender(Iterable<ItemStack> inventoryContents) {
        ItemStack first = Iterables.getFirst(this.outputs, ItemStack.EMPTY);
        return ItemUtils.copyStackWithSize(first, first.getCount());
    }

    @Nonnull
    public List<ItemStack> getOutputs(TileAltar altar) {
        return MiscUtils.transform(this.outputs, stack -> ItemUtils.copyStackWithSize(stack, stack.getCount()));
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

    public void addOutput(ItemStack output) {
        this.outputs.add(ItemUtils.copyStackWithSize(output, output.getCount()));
    }

    public boolean matches(LogicalSide side, PlayerEntity crafter, TileAltar altar, boolean ignoreStarlightRequirement) {
        if (crafter == null) {
            return false;
        }
        boolean hasProgress;
        if (side.isClient()) {
            hasProgress = this.hasProgressionClient();
        } else {
            hasProgress = this.hasProgressionServer(crafter);
        }
        if (!hasProgress) {
            return false;
        }

        if (!this.getAltarType().isThisLEThan(altar.getAltarType())) {
            return false;
        }
        if (this.getFocusConstellation() != null) {
            IConstellation focus = altar.getFocusedConstellation();
            if (focus == null || !focus.equals(this.getFocusConstellation())) {
                return false;
            }
        }
        if (!ignoreStarlightRequirement && altar.getStoredStarlight() < this.getStarlightRequirement()) {
            return false;
        }

        return this.altarRecipeGrid.containsInputs(altar.getInventory(), true);
    }

    public void deserializeAdditionalJson(JsonObject recipeObject) throws JsonSyntaxException {}

    public void writeRecipeSync(PacketBuffer buf) {}

    public void readRecipeSync(PacketBuffer buf) {}

    public static SimpleAltarRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        AltarType type = ByteBufUtils.readEnumValue(buffer, AltarType.class);
        int duration = buffer.readInt();
        int starlight = buffer.readInt();
        AltarRecipeGrid grid = AltarRecipeGrid.read(buffer);
        SimpleAltarRecipe recipe = new SimpleAltarRecipe(recipeId, type, duration, starlight, grid);
        ResourceLocation customType = ByteBufUtils.readOptional(buffer, ByteBufUtils::readResourceLocation);
        if (customType != null) {
            recipe = CustomAltarRecipeHandler.convert(recipe, customType);
            recipe.setCustomRecipeType(customType);
        }

        List<ItemStack> outputs = ByteBufUtils.readList(buffer, ByteBufUtils::readItemStack);
        outputs.forEach(recipe::addOutput);
        recipe.setFocusConstellation(ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry));
        ByteBufUtils.readList(buffer, Ingredient::read).forEach(recipe::addTraitInputIngredient);
        List<AltarRecipeEffect> effects = ByteBufUtils.readList(buffer, ByteBufUtils::readRegistryEntry);
        for (AltarRecipeEffect effect : effects) {
            recipe.addAltarEffect(effect);
        }
        recipe.readRecipeSync(buffer);
        return recipe;
    }

    public final void write(PacketBuffer buffer) {
        ByteBufUtils.writeEnumValue(buffer, this.getAltarType());
        buffer.writeInt(this.getDuration());
        buffer.writeInt(this.getStarlightRequirement());
        this.getInputs().write(buffer);
        ByteBufUtils.writeOptional(buffer, this.getCustomRecipeType(), ByteBufUtils::writeResourceLocation);

        ByteBufUtils.writeList(buffer, this.outputs, ByteBufUtils::writeItemStack);
        ByteBufUtils.writeOptional(buffer, this.getFocusConstellation(), ByteBufUtils::writeRegistryEntry);
        ByteBufUtils.writeList(buffer, this.getTraitInputIngredients(), (buf, ingredient) -> ingredient.getIngredient().write(buf));
        ByteBufUtils.writeList(buffer, this.getCraftingEffects(), ByteBufUtils::writeRegistryEntry);
        this.writeRecipeSync(buffer);
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
