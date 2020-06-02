/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeTypeHandler;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.AltarRecipeEffectsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipe
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:31
 */
public class SimpleAltarRecipe extends CustomMatcherRecipe implements GatedRecipe.Progression {

    private final AltarType altarType;

    private int duration;
    private int starlightRequirement;
    private AltarRecipeGrid altarRecipeGrid;
    private final List<ItemStack> outputs = new LinkedList<>();
    private ResourceLocation customRecipeType = null;
    private IConstellation focusConstellation = null;
    private List<WrappedIngredient> relayInputs = new LinkedList<>();

    private Set<AltarRecipeEffect> craftingEffects = new HashSet<>();

    public SimpleAltarRecipe(ResourceLocation recipeId, AltarType altarType) {
        this(recipeId, altarType, altarType.getDefaultAltarCraftingDuration());
    }

    public SimpleAltarRecipe(ResourceLocation recipeId, AltarType altarType, int duration) {
        this(recipeId, altarType, duration, 0);
    }

    public SimpleAltarRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement) {
        this(recipeId, altarType, duration, starlightRequirement, AltarRecipeGrid.EMPTY);
    }

    public SimpleAltarRecipe(ResourceLocation recipeId, AltarType altarType, int duration, int starlightRequirement, AltarRecipeGrid grid) {
        super(recipeId);
        this.altarType = altarType;
        this.duration = duration;
        this.starlightRequirement = starlightRequirement;
        this.altarRecipeGrid = grid;

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

    public void setInputs(AltarRecipeGrid inputGrid) {
        this.altarRecipeGrid = inputGrid;
    }

    public AltarRecipeGrid getInputs() {
        return altarRecipeGrid;
    }

    public Collection<AltarRecipeEffect> getCraftingEffects() {
        return craftingEffects;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setStarlightRequirement(int starlightRequirement) {
        this.starlightRequirement = starlightRequirement;
    }

    public int getStarlightRequirement() {
        return starlightRequirement;
    }

    public void onRecipeCompletion(TileAltar altar, ActiveSimpleAltarRecipe activeRecipe) {}

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ItemStack getOutputForRender(Iterable<ItemStack> inventoryContents) {
        ItemStack first = Iterables.getFirst(this.outputs, ItemStack.EMPTY);
        return ItemUtils.copyStackWithSize(first, first.getCount());
    }

    @Nonnull
    public List<ItemStack> getOutputs(TileAltar altar) {
        return MiscUtils.transformList(this.outputs, stack -> ItemUtils.copyStackWithSize(stack, stack.getCount()));
    }

    public void setFocusConstellation(IConstellation focusConstellation) {
        this.focusConstellation = focusConstellation;
    }

    @Nonnull
    public List<WrappedIngredient> getRelayInputs() {
        return relayInputs;
    }

    public boolean addRelayInput(Ingredient i) {
        return this.relayInputs.add(new WrappedIngredient(i));
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

    public void serializeAdditionalJson(JsonObject recipeObject) {}

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
            recipe = AltarRecipeTypeHandler.convert(recipe, customType);
            recipe.setCustomRecipeType(customType);
        }

        List<ItemStack> outputs = ByteBufUtils.readList(buffer, ByteBufUtils::readItemStack);
        outputs.forEach(recipe::addOutput);
        recipe.setFocusConstellation(ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry));
        ByteBufUtils.readList(buffer, Ingredient::read).forEach(recipe::addRelayInput);
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
        ByteBufUtils.writeList(buffer, this.getRelayInputs(), (buf, ingredient) -> ingredient.getIngredient().write(buf));
        ByteBufUtils.writeList(buffer, this.getCraftingEffects(), ByteBufUtils::writeRegistryEntry);
        this.writeRecipeSync(buffer);
    }

    public final void write(JsonObject object) {
        object.addProperty("altar_type", this.getAltarType().ordinal());
        object.addProperty("duration", this.getDuration());
        object.addProperty("starlight", this.getStarlightRequirement());
        this.getInputs().serialize(object);

        if (this.getCustomRecipeType() != null) {
            object.addProperty("recipe_class", this.getCustomRecipeType().toString());
        }

        JsonArray outputs = new JsonArray();
        for (ItemStack output : this.outputs) {
            outputs.add(JsonHelper.serializeItemStack(output));
        }
        object.add("output", outputs);

        JsonObject options = new JsonObject();
        this.serializeAdditionalJson(options);
        if (!options.entrySet().isEmpty()) {
            object.add("options", options);
        }

        if (this.getFocusConstellation() != null) {
            object.addProperty("focus_constellation", this.getFocusConstellation().getRegistryName().toString());
        }

        if (!this.getRelayInputs().isEmpty()) {
            JsonArray inputs = new JsonArray();
            for (WrappedIngredient traitInput : this.getRelayInputs()) {
                inputs.add(traitInput.getIngredient().serialize());
            }
            object.add("relay_inputs", inputs);
        }

        if (!this.getCraftingEffects().isEmpty()) {
            JsonArray effects = new JsonArray();
            for (AltarRecipeEffect effect : this.getCraftingEffects()) {
                effects.add(effect.getRegistryName().toString());
            }
            object.add("effects", effects);
        }
    }

    public AltarType getAltarType() {
        return altarType;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypesAS.TYPE_ALTAR.getType();
    }

    @Override
    public CustomRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.ALTAR_RECIPE_SERIALIZER;
    }
}
