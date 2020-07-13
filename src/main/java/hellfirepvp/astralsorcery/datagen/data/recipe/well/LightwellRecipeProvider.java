/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.well;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crafting.builder.WellRecipeBuilder;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightwellRecipeProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 22:13
 */
public class LightwellRecipeProvider {

    public static void registerLightwellRecipes(Consumer<IFinishedRecipe> registrar) {
        WellRecipeBuilder.builder(AstralSorcery.key("starlight_aquamarine"))
                .setItemInput(ItemsAS.AQUAMARINE)
                .setLiquidOutput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .color(new Color(0x00A7FF))
                .productionMultiplier(0.5F)
                .shatterMultiplier(15F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("starlight_resonating_gem"))
                .setItemInput(ItemsAS.RESONATING_GEM)
                .setLiquidOutput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .color(new Color(0x00A7FF))
                .productionMultiplier(0.75F)
                .shatterMultiplier(20F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("starlight_rock_crystal"))
                .setItemInput(ItemsAS.ROCK_CRYSTAL)
                .setLiquidOutput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .color(new Color(0x00A7FF))
                .productionMultiplier(0.07F)
                .shatterMultiplier(2F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("starlight_attuned_rock_crystal"))
                .setItemInput(ItemsAS.ATTUNED_ROCK_CRYSTAL)
                .setLiquidOutput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .color(new Color(0x00A7FF))
                .productionMultiplier(0.09F)
                .shatterMultiplier(5F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("starlight_celestial_crystal"))
                .setItemInput(ItemsAS.CELESTIAL_CRYSTAL)
                .setLiquidOutput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .color(new Color(0x00A7FF))
                .productionMultiplier(0.1F)
                .shatterMultiplier(4F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("starlight_attuned_celestial_crystal"))
                .setItemInput(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL)
                .setLiquidOutput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .color(new Color(0x00A7FF))
                .productionMultiplier(0.11F)
                .shatterMultiplier(8F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("lava_magma_block"))
                .setItemInput(Items.MAGMA_BLOCK)
                .setLiquidOutput(Fluids.LAVA)
                .color(new Color(0xFF350C))
                .productionMultiplier(0.7F)
                .shatterMultiplier(40F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("lava_netherrack"))
                .setItemInput(Items.NETHERRACK)
                .setLiquidOutput(Fluids.LAVA)
                .color(new Color(0xFF350C))
                .productionMultiplier(0.5F)
                .shatterMultiplier(10F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("water_ice"))
                .setItemInput(Items.ICE)
                .setLiquidOutput(Fluids.WATER)
                .color(new Color(0x5369FF))
                .productionMultiplier(1F)
                .shatterMultiplier(30F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("water_packed_ice"))
                .setItemInput(Items.PACKED_ICE)
                .setLiquidOutput(Fluids.WATER)
                .color(new Color(0x5369FF))
                .productionMultiplier(0.4F)
                .shatterMultiplier(60F)
                .build(registrar);

        WellRecipeBuilder.builder(AstralSorcery.key("water_snow"))
                .setItemInput(Items.SNOW_BLOCK)
                .setLiquidOutput(Fluids.WATER)
                .color(new Color(0x5369FF))
                .productionMultiplier(1.5F)
                .shatterMultiplier(20F)
                .build(registrar);
    }
}
