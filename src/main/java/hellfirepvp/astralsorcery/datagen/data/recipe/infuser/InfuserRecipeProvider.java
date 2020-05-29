/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.infuser;

import hellfirepvp.astralsorcery.common.crafting.builder.LiquidInfusionBuilder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfuserRecipeProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 21:36
 */
public class InfuserRecipeProvider {

    public static void registerInfuserRecipes(Consumer<IFinishedRecipe> registrar) {
        LiquidInfusionBuilder.builder(ItemsAS.AQUAMARINE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(ItemsAS.AQUAMARINE)
                .setOutput(ItemsAS.RESONATING_GEM)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.GLASS_PANE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.GLASS_PANES)
                .setOutput(ItemsAS.GLASS_LENS)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.SAND)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.SAND)
                .setOutput(Items.CLAY)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.GRASS_BLOCK)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Blocks.DIRT)
                .setOutput(Items.GRASS_BLOCK)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.GUNPOWDER)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.GUNPOWDER)
                .setOutput(Items.GLOWSTONE_DUST)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.ENDER_PEARL)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.ENDER_PEARLS)
                .setOutput(Items.ENDER_EYE)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.REDSTONE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.DUSTS_REDSTONE)
                .setOutput(Items.GUNPOWDER)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.BONE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.BONES)
                .setOutput(new ItemStack(Items.BONE_MEAL, 4))
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.BLAZE_ROD)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.RODS_BLAZE)
                .setOutput(new ItemStack(Items.BLAZE_POWDER, 4))
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.SLIME_BALL)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.SLIMEBALLS)
                .setOutput(Items.MAGMA_CREAM)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.CARROT)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.CROPS_CARROT)
                .setOutput(Items.GOLDEN_CARROT)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.MELON_SLICE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Items.MELON_SLICE)
                .setOutput(Items.GLISTERING_MELON_SLICE)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.IRON_ORE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.ORES_IRON)
                .setOutput(new ItemStack(Items.IRON_INGOT, 3))
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.GOLD_ORE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.ORES_GOLD)
                .setOutput(new ItemStack(Items.GOLD_INGOT, 3))
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.LAPIS_ORE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.ORES_LAPIS)
                .setOutput(Items.LAPIS_BLOCK)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.REDSTONE_ORE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.ORES_REDSTONE)
                .setOutput(Items.REDSTONE_BLOCK)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.DIAMOND_ORE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.ORES_DIAMOND)
                .setOutput(new ItemStack(Items.DIAMOND, 5))
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.EMERALD_ORE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.ORES_EMERALD)
                .setOutput(new ItemStack(Items.EMERALD, 5))
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(Items.GLASS)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.GLASS)
                .setOutput(Items.ICE)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(BlocksAS.INFUSED_WOOD)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(BlocksAS.INFUSED_WOOD)
                .setOutput(BlocksAS.INFUSED_WOOD_INFUSED)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(ItemsAS.CRYSTAL_AXE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(ItemsAS.CRYSTAL_AXE)
                .setOutput(ItemsAS.INFUSED_CRYSTAL_AXE)
                .setConsumeMultipleFluids(true)
                .setAcceptChaliceInput(false)
                .setCopyNBTToOutputs(true)
                .setFluidConsumptionChance(1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(ItemsAS.CRYSTAL_PICKAXE)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(ItemsAS.CRYSTAL_PICKAXE)
                .setOutput(ItemsAS.INFUSED_CRYSTAL_PICKAXE)
                .setConsumeMultipleFluids(true)
                .setAcceptChaliceInput(false)
                .setCopyNBTToOutputs(true)
                .setFluidConsumptionChance(1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(ItemsAS.CRYSTAL_SHOVEL)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(ItemsAS.CRYSTAL_SHOVEL)
                .setOutput(ItemsAS.INFUSED_CRYSTAL_SHOVEL)
                .setConsumeMultipleFluids(true)
                .setAcceptChaliceInput(false)
                .setCopyNBTToOutputs(true)
                .setFluidConsumptionChance(1F)
                .build(registrar);

        LiquidInfusionBuilder.builder(ItemsAS.CRYSTAL_SWORD)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(ItemsAS.CRYSTAL_SWORD)
                .setOutput(ItemsAS.INFUSED_CRYSTAL_SWORD)
                .setConsumeMultipleFluids(true)
                .setAcceptChaliceInput(false)
                .setCopyNBTToOutputs(true)
                .setFluidConsumptionChance(1F)
                .build(registrar);
    }
}
