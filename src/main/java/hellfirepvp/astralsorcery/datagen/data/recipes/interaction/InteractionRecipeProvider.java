package hellfirepvp.astralsorcery.datagen.data.recipes.interaction;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.builder.LiquidInteractionBuilder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InteractionRecipeProvider
 * Created by HellFirePvP
 * Date: 29.10.2020 / 21:49
 */
public class InteractionRecipeProvider {

    public static void registerLiquidInteractionRecipes(Consumer<IFinishedRecipe> registrar) {
        // Lava/Water
        LiquidInteractionBuilder.builder(AstralSorcery.key("water_lava_cobblestone"))
                .setReactant1(new FluidStack(Fluids.WATER, 10))
                .setChanceConsumeReactant1(0F)
                .setReactant2(new FluidStack(Fluids.LAVA, 10))
                .setChanceConsumeReactant2(0F)
                .setWeight(6)
                .setOutput(new ItemStack(Items.COBBLESTONE))
                .build(registrar);
        LiquidInteractionBuilder.builder(AstralSorcery.key("water_lava_stone"))
                .setReactant1(new FluidStack(Fluids.WATER, 10))
                .setChanceConsumeReactant1(0.2F)
                .setReactant2(new FluidStack(Fluids.LAVA, 10))
                .setChanceConsumeReactant2(0.2F)
                .setWeight(3)
                .setOutput(new ItemStack(Items.STONE))
                .build(registrar);
        LiquidInteractionBuilder.builder(AstralSorcery.key("water_lava_obsidian"))
                .setReactant1(new FluidStack(Fluids.WATER, 10))
                .setReactant2(new FluidStack(Fluids.LAVA, 10))
                .setWeight(1)
                .setOutput(new ItemStack(Items.OBSIDIAN))
                .build(registrar);

        // LS/Water
        LiquidInteractionBuilder.builder(AstralSorcery.key("liquidstarlight_water_ice"))
                .setReactant1(new FluidStack(FluidsAS.LIQUID_STARLIGHT_SOURCE, 10))
                .setReactant2(new FluidStack(Fluids.WATER, 10))
                .setOutput(new ItemStack(Items.ICE))
                .build(registrar);

        // LS/Lava
        LiquidInteractionBuilder.builder(AstralSorcery.key("liquidstarlight_lava_sand"))
                .setReactant1(new FluidStack(FluidsAS.LIQUID_STARLIGHT_SOURCE, 10))
                .setChanceConsumeReactant1(0.15F)
                .setReactant2(new FluidStack(Fluids.LAVA, 10))
                .setChanceConsumeReactant1(0.15F)
                .setWeight(160)
                .setOutput(new ItemStack(Items.SAND))
                .build(registrar);
        LiquidInteractionBuilder.builder(AstralSorcery.key("liquidstarlight_lava_aquamarine"))
                .setReactant1(new FluidStack(FluidsAS.LIQUID_STARLIGHT_SOURCE, 10))
                .setReactant2(new FluidStack(Fluids.LAVA, 10))
                .setWeight(1)
                .setOutput(new ItemStack(BlocksAS.AQUAMARINE_SAND_ORE))
                .build(registrar);
    }
}
