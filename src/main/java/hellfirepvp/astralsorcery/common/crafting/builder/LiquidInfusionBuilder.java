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
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInfusionBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 17:02
 */
public class LiquidInfusionBuilder extends CustomRecipeBuilder<LiquidInfusion> {

    private final ResourceLocation id;

    private Fluid liquidInput = null;
    private Ingredient itemInput = Ingredient.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    private int craftingTickTime = 200;

    private float consumptionChance = 0.3F;
    private boolean consumeMultipleFluids = false;
    private boolean acceptChaliceInput = true;
    private boolean copyNBTToOutputs = false;

    private LiquidInfusionBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static LiquidInfusionBuilder builder(ForgeRegistryEntry<?> nameProvider) {
        return new LiquidInfusionBuilder(AstralSorcery.key(nameProvider.getRegistryName().getPath()));
    }

    public static LiquidInfusionBuilder builder(ResourceLocation id) {
        return new LiquidInfusionBuilder(id);
    }

    public LiquidInfusionBuilder setLiquidInput(Fluid liquidInput) {
        this.liquidInput = liquidInput;
        return this;
    }

    public LiquidInfusionBuilder setItemInput(IItemProvider item) {
        this.itemInput = Ingredient.fromItems(item);
        return this;
    }

    public LiquidInfusionBuilder setItemInput(Tag<Item> tag) {
        this.itemInput = Ingredient.fromTag(tag);
        return this;
    }

    public LiquidInfusionBuilder setItemInput(Ingredient input) {
        this.itemInput = input;
        return this;
    }

    public LiquidInfusionBuilder setOutput(IItemProvider output) {
        return this.setOutput(new ItemStack(output));
    }

    public LiquidInfusionBuilder setOutput(ItemStack output) {
        this.output = output.copy();
        return this;
    }

    public LiquidInfusionBuilder multiplyDuration(float multiplier) {
        this.craftingTickTime *= multiplier;
        return this;
    }

    public LiquidInfusionBuilder setDuration(int craftingTickTime) {
        this.craftingTickTime = craftingTickTime;
        return this;
    }

    public LiquidInfusionBuilder setFluidConsumptionChance(float consumptionChance) {
        this.consumptionChance = consumptionChance;
        return this;
    }

    public LiquidInfusionBuilder setConsumeMultipleFluids(boolean consumeMultipleFluids) {
        this.consumeMultipleFluids = consumeMultipleFluids;
        return this;
    }

    public LiquidInfusionBuilder setAcceptChaliceInput(boolean acceptChaliceInput) {
        this.acceptChaliceInput = acceptChaliceInput;
        return this;
    }

    public LiquidInfusionBuilder setCopyNBTToOutputs(boolean copyNBTToOutputs) {
        this.copyNBTToOutputs = copyNBTToOutputs;
        return this;
    }

    @Nonnull
    @Override
    protected LiquidInfusion validateAndGet() {
        if (this.liquidInput == null) {
            throw new IllegalArgumentException("No fluid input defined!");
        }
        if (this.itemInput.hasNoMatchingItems()) {
            throw new IllegalArgumentException("No valid item for input found!");
        }
        if (this.output.isEmpty()) {
            throw new IllegalArgumentException("No output item defined!");
        }
        if (this.craftingTickTime <= 0) {
            throw new IllegalArgumentException("No duration defined!");
        }
        return new LiquidInfusion(this.id, craftingTickTime, liquidInput, itemInput, output, consumptionChance, consumeMultipleFluids, acceptChaliceInput, copyNBTToOutputs);
    }

    @Override
    protected CustomRecipeSerializer<LiquidInfusion> getSerializer() {
        return RecipeSerializersAS.LIQUID_INFUSION_SERIALIZER;
    }
}
