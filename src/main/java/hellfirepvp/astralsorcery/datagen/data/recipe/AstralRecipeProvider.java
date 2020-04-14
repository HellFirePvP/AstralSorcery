/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe;

import hellfirepvp.astralsorcery.datagen.data.recipe.altar.AttunementAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.altar.CelestialAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.altar.DiscoveryAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.altar.RadianceAltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.infuser.InfuserRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.transmutation.BlockTransmutationRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.vanilla.VanillaTypedRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipe.well.LightwellRecipeProvider;
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

        VanillaTypedRecipeProvider.registerShapedRecipes(registrar);
        VanillaTypedRecipeProvider.registerCookingRecipes(registrar);
        VanillaTypedRecipeProvider.registerCustomRecipes(registrar);
    }
}
