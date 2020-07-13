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
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 17:01
 */
public class WellRecipeBuilder extends CustomRecipeBuilder<WellLiquefaction> {

    private final ResourceLocation id;

    private Ingredient input = Ingredient.EMPTY;
    private Fluid output = Fluids.EMPTY;

    private float productionMultiplier = 0.5F;
    private float shatterMultiplier = 15F;
    private Color catalystColor = Color.WHITE;

    private WellRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static WellRecipeBuilder builder(ForgeRegistryEntry<?> nameProvider) {
        return new WellRecipeBuilder(AstralSorcery.key(nameProvider.getRegistryName().getPath()));
    }

    public static WellRecipeBuilder builder(ResourceLocation id) {
        return new WellRecipeBuilder(id);
    }

    public WellRecipeBuilder setItemInput(IItemProvider item) {
        this.input = Ingredient.fromItems(item);
        return this;
    }

    public WellRecipeBuilder setItemInput(Tag<Item> tag) {
        this.input = Ingredient.fromTag(tag);
        return this;
    }

    public WellRecipeBuilder setItemInput(Ingredient input) {
        this.input = input;
        return this;
    }

    public WellRecipeBuilder setLiquidOutput(Fluid output) {
        this.output = output;
        return this;
    }

    public WellRecipeBuilder color(Color color) {
        this.catalystColor = color;
        return this;
    }

    public WellRecipeBuilder productionMultiplier(float multiplier) {
        this.productionMultiplier = multiplier;
        return this;
    }

    public WellRecipeBuilder shatterMultiplier(float multiplier) {
        this.shatterMultiplier = multiplier;
        return this;
    }

    @Nonnull
    @Override
    protected WellLiquefaction validateAndGet() {
        if (this.input.hasNoMatchingItems()) {
            throw new IllegalArgumentException("No valid item for input found!");
        }
        if (this.output == Fluids.EMPTY) {
            throw new IllegalArgumentException("No output fluid defined!");
        }
        return new WellLiquefaction(this.id, this.input, this.output, this.catalystColor, this.productionMultiplier, this.shatterMultiplier);
    }

    @Override
    protected CustomRecipeSerializer<WellLiquefaction> getSerializer() {
        return RecipeSerializersAS.WELL_LIQUEFACTION_SERIALIZER;
    }
}
