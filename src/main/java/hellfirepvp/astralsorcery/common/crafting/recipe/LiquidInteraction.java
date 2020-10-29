package hellfirepvp.astralsorcery.common.crafting.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomMatcherRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
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
    private final ItemStack output;

    public LiquidInteraction(ResourceLocation recipeId,
                             FluidStack reactant1, float chanceConsumeReactant1,
                             FluidStack reactant2, float chanceConsumeReactant2,
                             int weight, ItemStack output) {
        super(recipeId);
        this.reactant1 = reactant1;
        this.chanceConsumeReactant1 = chanceConsumeReactant1;
        this.reactant2 = reactant2;
        this.chanceConsumeReactant2 = chanceConsumeReactant2;
        this.weight = weight;
        this.output = output;
    }

    public boolean matches(FluidStack input1, FluidStack input2) {
        return (this.reactant1.containsFluid(input1) && this.reactant2.containsFluid(input2)) ||
                (this.reactant1.containsFluid(input2) && this.reactant2.containsFluid(input1));
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
        ItemStack output = JsonHelper.getItemStack(json, "output");

        return new LiquidInteraction(recipeId, r1, chance1, r2, chance2, weight, output);
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
        object.add("output", JsonHelper.serializeItemStack(this.output));
    }

    public static LiquidInteraction read(ResourceLocation recipeId, PacketBuffer buffer) {
        FluidStack reactant1 = ByteBufUtils.readFluidStack(buffer);
        FluidStack reactant2 = ByteBufUtils.readFluidStack(buffer);
        float chanceConsumeReactant1 = buffer.readFloat();
        float chanceConsumeReactant2 = buffer.readFloat();
        int weight = buffer.readInt();
        ItemStack output = ByteBufUtils.readItemStack(buffer);
        return new LiquidInteraction(recipeId, reactant1, chanceConsumeReactant1, reactant2, chanceConsumeReactant2, weight, output);
    }

    public final void write(PacketBuffer buffer) {
        ByteBufUtils.writeFluidStack(buffer, this.reactant1);
        ByteBufUtils.writeFluidStack(buffer, this.reactant2);
        buffer.writeFloat(this.chanceConsumeReactant1);
        buffer.writeFloat(this.chanceConsumeReactant2);
        buffer.writeInt(this.weight);
        ByteBufUtils.writeItemStack(buffer, this.output);
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
