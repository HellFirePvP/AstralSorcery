/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.infuser;

import hellfirepvp.astralsorcery.common.crafting.builder.LiquidInfusionBuilder;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.data.IFinishedRecipe;
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
        LiquidInfusionBuilder.builder(ItemsAS.RESONATING_GEM)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(ItemsAS.AQUAMARINE)
                .setOutput(ItemsAS.RESONATING_GEM)
                .build(registrar);

        LiquidInfusionBuilder.builder(ItemsAS.GLASS_LENS)
                .setLiquidInput(FluidsAS.LIQUID_STARLIGHT_SOURCE)
                .setItemInput(Tags.Items.GLASS_PANES)
                .setOutput(ItemsAS.GLASS_LENS)
                .multiplyDuration(0.5F)
                .setFluidConsumptionChance(0.1F)
                .build(registrar);
    }
}
