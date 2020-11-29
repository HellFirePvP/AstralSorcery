/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInteraction;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResult;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteractionBuilder
 * Created by HellFirePvP
 * Date: 29.10.2020 / 21:41
 */
public class LiquidInteractionBuilder extends CustomRecipeBuilder<LiquidInteraction> {

    private final ResourceLocation id;

    private FluidStack reactant1 = FluidStack.EMPTY, reactant2 = FluidStack.EMPTY;
    private float chanceConsume1 = 1F, chanceConsume2 = 1F;
    private int weight = 1;
    private InteractionResult result = null;

    private LiquidInteractionBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static LiquidInteractionBuilder builder(ForgeRegistryEntry<?> nameProvider) {
        return new LiquidInteractionBuilder(AstralSorcery.key(nameProvider.getRegistryName().getPath()));
    }

    public static LiquidInteractionBuilder builder(ResourceLocation id) {
        return new LiquidInteractionBuilder(id);
    }

    public LiquidInteractionBuilder setReactant1(FluidStack reactant1) {
        this.reactant1 = reactant1;
        return this;
    }

    public LiquidInteractionBuilder setReactant2(FluidStack reactant2) {
        this.reactant2 = reactant2;
        return this;
    }

    public LiquidInteractionBuilder setChanceConsumeReactant1(float chanceConsume1) {
        this.chanceConsume1 = chanceConsume1;
        return this;
    }

    public LiquidInteractionBuilder setChanceConsumeReactant2(float chanceConsume2) {
        this.chanceConsume2 = chanceConsume2;
        return this;
    }

    public LiquidInteractionBuilder setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public LiquidInteractionBuilder setResult(InteractionResult result) {
        this.result = result;
        return this;
    }

    @Nonnull
    @Override
    protected LiquidInteraction validateAndGet() {
        if (this.reactant1.isEmpty()) {
            throw new IllegalArgumentException("The 1st reactant must not be empty!");
        }
        if (this.reactant2.isEmpty()) {
            throw new IllegalArgumentException("The 2nd reactant must not be empty!");
        }
        if (this.weight <= 0) {
            throw new IllegalArgumentException("Weight has to be positive!");
        }
        if (this.result == null) {
            throw new IllegalArgumentException("A result must be defined!");
        }
        return new LiquidInteraction(this.id,
                this.reactant1, this.chanceConsume1,
                this.reactant2, this.chanceConsume2,
                this.weight, this.result);
    }

    @Override
    protected CustomRecipeSerializer<LiquidInteraction> getSerializer() {
        return RecipeSerializersAS.LIQUID_INTERACTION_SERIALIZER;
    }
}
