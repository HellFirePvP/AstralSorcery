package hellfirepvp.astralsorcery.common.crafting.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResultRegistry;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import joptsimple.internal.Strings;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteraction
 * Created by HellFirePvP
 * Date: 29.10.2020 / 20:44
 */
public class LiquidInteraction extends CustomMatcherRecipe {

    private static final Random rand = new Random();

    private final FluidStack reactant1, reactant2;
    private final float chanceConsumeReactant1, chanceConsumeReactant2;
    private final int weight;
    private final InteractionResult result;

    public LiquidInteraction(ResourceLocation recipeId,
                             FluidStack reactant1, float chanceConsumeReactant1,
                             FluidStack reactant2, float chanceConsumeReactant2,
                             int weight, InteractionResult result) {
        super(recipeId);
        this.reactant1 = reactant1;
        this.chanceConsumeReactant1 = chanceConsumeReactant1;
        this.reactant2 = reactant2;
        this.chanceConsumeReactant2 = chanceConsumeReactant2;
        this.weight = weight;
        this.result = result;
    }

    public boolean matches(FluidStack input1, FluidStack input2) {
        return (input1.containsFluid(this.reactant1) && input2.containsFluid(this.reactant2)) ||
                (input2.containsFluid(this.reactant2) && input1.containsFluid(this.reactant1));
    }

    public boolean consumeInputs(TileChalice chalice1, TileChalice chalice2) {
        FluidStack contained1 = chalice1.getTank().getFluid();
        FluidStack contained2 = chalice2.getTank().getFluid();
        if (!this.matches(contained1, contained2)) {
            return false;
        }
        if (contained1.containsFluid(this.reactant1) && contained2.containsFluid(this.reactant2)) {
            FluidStack drained1 = chalice1.getTank().drain(this.reactant1, IFluidHandler.FluidAction.SIMULATE);
            FluidStack drained2 = chalice2.getTank().drain(this.reactant2, IFluidHandler.FluidAction.SIMULATE);
            if (drained1.containsFluid(this.reactant1) && drained2.containsFluid(this.reactant2)) {
                if (rand.nextFloat() < this.chanceConsumeReactant1) {
                    chalice1.getTank().drain(this.reactant1, IFluidHandler.FluidAction.EXECUTE);
                }
                if (rand.nextFloat() < this.chanceConsumeReactant2) {
                    chalice2.getTank().drain(this.reactant2, IFluidHandler.FluidAction.EXECUTE);
                }
                return true;
            }
        }
        if (contained1.containsFluid(this.reactant2) && contained2.containsFluid(this.reactant1)) {
            FluidStack drained1 = chalice1.getTank().drain(this.reactant2, IFluidHandler.FluidAction.SIMULATE);
            FluidStack drained2 = chalice2.getTank().drain(this.reactant1, IFluidHandler.FluidAction.SIMULATE);
            if (drained1.containsFluid(this.reactant2) && drained2.containsFluid(this.reactant1)) {
                if (rand.nextFloat() < this.chanceConsumeReactant1) {
                    chalice2.getTank().drain(this.reactant1, IFluidHandler.FluidAction.EXECUTE);
                }
                if (rand.nextFloat() < this.chanceConsumeReactant2) {
                    chalice1.getTank().drain(this.reactant2, IFluidHandler.FluidAction.EXECUTE);
                }
                return true;
            }
        }
        return false;
    }

    public FluidStack getReactant1() {
        return reactant1;
    }

    public FluidStack getReactant2() {
        return reactant2;
    }

    public InteractionResult getResult() {
        return result;
    }

    public int getWeight() {
        return weight;
    }

    @Nullable
    public static LiquidInteraction pickRecipe(Collection<LiquidInteraction> recipes) {
        return MiscUtils.getWeightedRandomEntry(recipes, rand, interaction -> interaction.weight);
    }

    public static LiquidInteraction read(ResourceLocation recipeId, JsonObject json) {
        String fluidKey1 = JSONUtils.getString(json, "reactant1");
        Fluid reactant1 = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidKey1));
        if (reactant1 == null) {
            throw new JsonSyntaxException("Unknown fluid: " + fluidKey1);
        }
        int amount1 = JSONUtils.getInt(json, "reactant1Amount");
        CompoundNBT tag1 = null;
        if (JSONUtils.hasField(json, "reactant1Tag")) {
            String jsonTag1 = JSONUtils.getString(json, "reactant1Tag");
            try {
                tag1 = JsonToNBT.getTagFromJson(jsonTag1);
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid Json: " + jsonTag1);
            }
        }
        FluidStack r1 = new FluidStack(reactant1, amount1, tag1);

        String fluidKey2 = JSONUtils.getString(json, "reactant2");
        Fluid reactant2 = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidKey2));
        if (reactant2 == null) {
            throw new JsonSyntaxException("Unknown fluid: " + fluidKey2);
        }
        int amount2 = JSONUtils.getInt(json, "reactant2Amount");
        CompoundNBT tag2 = null;
        if (JSONUtils.hasField(json, "reactant2Tag")) {
            String jsonTag2 = JSONUtils.getString(json, "reactant2Tag");
            try {
                tag2 = JsonToNBT.getTagFromJson(jsonTag2);
            } catch (CommandSyntaxException e) {
                throw new JsonSyntaxException("Invalid Json: " + jsonTag2);
            }
        }
        FluidStack r2 = new FluidStack(reactant2, amount2, tag2);

        float chance1 = JSONUtils.getFloat(json, "chanceConsumeReactant1");
        float chance2 = JSONUtils.getFloat(json, "chanceConsumeReactant2");
        int weight = JSONUtils.getInt(json, "weight");

        JsonObject ctResult = JSONUtils.getJsonObject(json, "result");
        ResourceLocation id = new ResourceLocation(JSONUtils.getString(ctResult, "id"));
        InteractionResult result = InteractionResultRegistry.create(id);
        if (result == null) {
            throw new JsonSyntaxException("Unknown result type: " + id.toString() +
                    "; expected one of " + Strings.join(InteractionResultRegistry.getKeysAsStrings(), ", "));
        }
        JsonObject resultData = JSONUtils.getJsonObject(ctResult, "data");
        result.read(resultData);

        return new LiquidInteraction(recipeId, r1, chance1, r2, chance2, weight, result);
    }

    public final void write(JsonObject object) {
        object.addProperty("reactant1", this.reactant1.getFluid().getRegistryName().toString());
        object.addProperty("reactant1Amount", this.reactant1.getAmount());
        if (this.reactant1.hasTag()) {
            object.addProperty("reactant1Tag", this.reactant1.getTag().toString());
        }
        object.addProperty("reactant2", this.reactant2.getFluid().getRegistryName().toString());
        object.addProperty("reactant2Amount", this.reactant2.getAmount());
        if (this.reactant2.hasTag()) {
            object.addProperty("reactant2Tag", this.reactant2.getTag().toString());
        }
        object.addProperty("chanceConsumeReactant1", this.chanceConsumeReactant1);
        object.addProperty("chanceConsumeReactant2", this.chanceConsumeReactant2);
        object.addProperty("weight", this.weight);

        JsonObject ctResult = new JsonObject();
        ctResult.addProperty("id", this.result.getId().toString());
        JsonObject resultObj = new JsonObject();
        this.result.write(resultObj);
        ctResult.add("data", resultObj);
        object.add("result", ctResult);
    }

    public static LiquidInteraction read(ResourceLocation recipeId, PacketBuffer buffer) {
        FluidStack reactant1 = ByteBufUtils.readFluidStack(buffer);
        FluidStack reactant2 = ByteBufUtils.readFluidStack(buffer);
        float chanceConsumeReactant1 = buffer.readFloat();
        float chanceConsumeReactant2 = buffer.readFloat();
        int weight = buffer.readInt();
        ResourceLocation key = new ResourceLocation(ByteBufUtils.readString(buffer));
        InteractionResult result = InteractionResultRegistry.create(key);
        if (result == null) {
            return null;
        }
        result.read(buffer);
        return new LiquidInteraction(recipeId, reactant1, chanceConsumeReactant1, reactant2, chanceConsumeReactant2, weight, result);
    }

    public final void write(PacketBuffer buffer) {
        ByteBufUtils.writeFluidStack(buffer, this.reactant1);
        ByteBufUtils.writeFluidStack(buffer, this.reactant2);
        buffer.writeFloat(this.chanceConsumeReactant1);
        buffer.writeFloat(this.chanceConsumeReactant2);
        buffer.writeInt(this.weight);
        ByteBufUtils.writeString(buffer, this.result.getId().toString());
        this.result.write(buffer);
    }

    @Override
    public CustomRecipeSerializer<?> getSerializer() {
        return RecipeSerializersAS.LIQUID_INTERACTION_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypesAS.TYPE_LIQUID_INTERACTION.getType();
    }
}
