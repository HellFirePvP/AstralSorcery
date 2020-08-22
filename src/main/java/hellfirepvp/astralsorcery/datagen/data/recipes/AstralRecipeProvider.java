/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes;

import hellfirepvp.astralsorcery.datagen.data.recipes.altar.AttunementAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.altar.CelestialAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.altar.DiscoveryAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.altar.RadianceAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.infuser.InfuserRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.transmutation.BlockTransmutationRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.vanilla.VanillaTypedRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.well.LightwellRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralRecipeProvider
 * Created by HellFirePvP
 * Date: 07.03.2020 / 08:00
 */
public class AstralRecipeProvider extends RecipeProvider {

    public AstralRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> registrar) {
        DiscoveryAltarRecipeProvider.registerAltarRecipes(registrar);
        AttunementAltarRecipeProvider.registerAltarRecipes(registrar);
        CelestialAltarRecipeProvider.registerAltarRecipes(registrar);
        RadianceAltarRecipeProvider.registerAltarRecipes(registrar);

        InfuserRecipeProvider.registerInfuserRecipes(registrar);
        LightwellRecipeProvider.registerLightwellRecipes(registrar);
        BlockTransmutationRecipeProvider.registerTransmutationRecipes(registrar);

        VanillaTypedRecipeProvider.registerStoneCutterRecipes(registrar);
        VanillaTypedRecipeProvider.registerShapedRecipes(registrar);
        VanillaTypedRecipeProvider.registerCookingRecipes(registrar);
        VanillaTypedRecipeProvider.registerCustomRecipes(registrar);
    }
}
