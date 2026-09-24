/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InfusionRecipe
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InfusionRecipe extends CustomRecipe<InfusionRecipe, InfusionRecipeInput> {

    private static final RandomSource rand = RandomSource.create();

    public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("item_input").forGetter(InfusionRecipe::getItemInput),
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid_input").forGetter(InfusionRecipe::getFluidInput),
            Codec.INT.fieldOf("duration").forGetter(InfusionRecipe::getDuration),
            ItemStack.CODEC.fieldOf("output").forGetter(InfusionRecipe::getOutput),
            Codec.FLOAT.fieldOf("fluid_consumption_chance").forGetter(InfusionRecipe::getFluidConsumptionChance),
            Codec.BOOL.fieldOf("consume_multiple_fluids").forGetter(InfusionRecipe::consumeMultipleFluids),
            Codec.BOOL.fieldOf("accept_chalice_input").forGetter(InfusionRecipe::acceptChaliceInput)
    ).apply(inst, InfusionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.of(InfusionRecipe::write, InfusionRecipe::read);

    private final Ingredient itemInput;
    private final Fluid fluidInput;
    private final int duration;
    private final ItemStack output;

    private final float fluidConsumptionChance;
    private final boolean consumeMultipleFluids;
    private final boolean acceptChaliceInput;

    public InfusionRecipe(Ingredient itemInput, Fluid fluidInput, int duration, ItemStack output, float fluidConsumptionChance, boolean consumeMultipleFluids, boolean acceptChaliceInput) {
        this.itemInput = itemInput;
        this.fluidInput = fluidInput;
        this.duration = duration;
        this.output = output;
        this.fluidConsumptionChance = fluidConsumptionChance;
        this.consumeMultipleFluids = consumeMultipleFluids;
        this.acceptChaliceInput = acceptChaliceInput;
    }

    @Override
    public boolean consumeInputs(InfusionRecipeInput input, HolderLookup.Provider registries) {
        // Only consumes fluids
        if (this.fluidConsumptionChance <= 0) {
            return true;
        }

        boolean consumedAny = false;
        List<BlockPos> positions = new ArrayList<>(input.getFluidInputs().keySet());
        Collections.shuffle(positions);
        for (BlockPos pos : positions) {
            if (rand.nextFloat() <= this.fluidConsumptionChance) {
                if (input.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL)) {
                    consumedAny = true;
                    if (!this.consumeMultipleFluids()) {
                        return true;
                    }
                }
            }
        }
        return consumedAny;
    }

    @Override
    public void createOutput(InfusionRecipeInput input, HolderLookup.Provider registries) {
    }

    @Override
    public boolean matches(InfusionRecipeInput input, Level level) {
        for (BlockPos pos : input.getFluidInputs().keySet()) {
            FluidState state = input.getFluidInputs().get(pos);
            if (state.isEmpty() || !state.is(this.getFluidInput())) {
                return false;
            }
        }
        if (input.getItemInput().isEmpty()) {
            return false;
        }
        return this.getItemInput().test(input.getItemInput());
    }

    public Ingredient getItemInput() {
        return this.itemInput;
    }

    public Fluid getFluidInput() {
        return this.fluidInput;
    }

    public int getDuration() {
        return this.duration;
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    public float getFluidConsumptionChance() {
        return this.fluidConsumptionChance;
    }

    public boolean consumeMultipleFluids() {
        return this.consumeMultipleFluids;
    }

    public boolean acceptChaliceInput() {
        return this.acceptChaliceInput;
    }

    public FluidStack getChaliceInputFluidStack() {
        int amount = Math.round(FluidType.BUCKET_VOLUME * this.getFluidConsumptionChance());
        amount *= Mth.ceil(amount * 0.75F);
        amount = this.consumeMultipleFluids() ? amount * TileInfuser.getLiquidOffsets().size() : amount;
        return new FluidStack(this.getFluidInput(), amount);
    }

    @Override
    public Supplier<? extends RecipeSerializer<InfusionRecipe>> getRecipeSerializer() {
        return RecipeTypesAS.INFUSION_SERIALIZER;
    }

    @Override
    public ResolvingRecipeTypeRegistryObject<InfusionRecipe> getRecipeType() {
        return RecipeTypesAS.INFUSION_TYPE;
    }

    private static void write(RegistryFriendlyByteBuf buf, InfusionRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getItemInput());
        ByteBufCodecs.registry(Registries.FLUID).encode(buf, recipe.getFluidInput());
        buf.writeInt(recipe.getDuration());
        ItemStack.STREAM_CODEC.encode(buf, recipe.getOutput());
        buf.writeFloat(recipe.getFluidConsumptionChance());
        buf.writeBoolean(recipe.consumeMultipleFluids());
        buf.writeBoolean(recipe.acceptChaliceInput());
    }

    private static InfusionRecipe read(RegistryFriendlyByteBuf buf) {
        Ingredient itemInput = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        Fluid fluidInput = ByteBufCodecs.registry(Registries.FLUID).decode(buf);
        int duration = buf.readInt();
        ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
        float fluidConsumptionChance = buf.readFloat();
        boolean consumeMultipleFluids = buf.readBoolean();
        boolean acceptChaliceInput = buf.readBoolean();
        return new InfusionRecipe(itemInput, fluidInput, duration, output, fluidConsumptionChance, consumeMultipleFluids, acceptChaliceInput);
    }
}
