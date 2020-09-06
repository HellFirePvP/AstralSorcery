/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.container.ContainerAltarAttunement;
import hellfirepvp.astralsorcery.common.container.ContainerAltarConstellation;
import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
import hellfirepvp.astralsorcery.common.container.ContainerAltarTrait;
import hellfirepvp.astralsorcery.common.integration.jei.*;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationJEI
 * Created by HellFirePvP
 * Date: 25.07.2020 / 09:23
 */
@JeiPlugin
public class IntegrationJEI implements IModPlugin {

    public static final List<JEICategory<?>> CATEGORIES = new ArrayList<>();

    public static final ResourceLocation CATEGORY_ALTAR_ATTUNEMENT = AstralSorcery.key("altar_attunement");
    public static final ResourceLocation CATEGORY_ALTAR_CONSTELLATION = AstralSorcery.key("altar_constellation");
    public static final ResourceLocation CATEGORY_ALTAR_DISCOVERY = AstralSorcery.key("altar_discovery");
    public static final ResourceLocation CATEGORY_ALTAR_TRAIT = AstralSorcery.key("altar_trait");
    public static final ResourceLocation CATEGORY_INFUSER = AstralSorcery.key("infuser");
    public static final ResourceLocation CATEGORY_TRANSMUTATION = AstralSorcery.key("transmutation");
    public static final ResourceLocation CATEGORY_WELL = AstralSorcery.key("well");

    public static IJeiRuntime runtime = null;

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registry) {
        registry.useNbtForSubtypes(
                ItemsAS.ATTUNED_ROCK_CRYSTAL,
                ItemsAS.ATTUNED_CELESTIAL_CRYSTAL,
                BlocksAS.ROCK_COLLECTOR_CRYSTAL.asItem(),
                BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL.asItem(),
                BlocksAS.CELESTIAL_CRYSTAL_CLUSTER.asItem(),
                BlocksAS.GEM_CRYSTAL_CLUSTER.asItem(),
                ItemsAS.MANTLE,
                ItemsAS.RESONATOR
        );
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        CATEGORIES.add(new CategoryAltar(CATEGORY_ALTAR_DISCOVERY, "altar_discovery", BlocksAS.ALTAR_DISCOVERY, guiHelper));
        CATEGORIES.add(new CategoryAltar(CATEGORY_ALTAR_ATTUNEMENT, "altar_attunement", BlocksAS.ALTAR_ATTUNEMENT, guiHelper));
        CATEGORIES.add(new CategoryAltar(CATEGORY_ALTAR_CONSTELLATION, "altar_constellation", BlocksAS.ALTAR_CONSTELLATION, guiHelper));
        CATEGORIES.add(new CategoryAltar(CATEGORY_ALTAR_TRAIT, "altar_trait", BlocksAS.ALTAR_RADIANCE, guiHelper));
        CATEGORIES.add(new CategoryInfuser(guiHelper));
        CATEGORIES.add(new CategoryTransmutation(guiHelper));
        CATEGORIES.add(new CategoryWell(guiHelper));

        CATEGORIES.forEach(registry::addRecipeCategories);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        CATEGORIES.forEach(category -> registry.addRecipes(category.getRecipes(), category.getUid()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_DISCOVERY), CATEGORY_ALTAR_DISCOVERY);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_ATTUNEMENT), CATEGORY_ALTAR_ATTUNEMENT);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_CONSTELLATION), CATEGORY_ALTAR_CONSTELLATION);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.ALTAR_RADIANCE), CATEGORY_ALTAR_TRAIT);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.INFUSER), CATEGORY_INFUSER);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.LENS), CATEGORY_TRANSMUTATION);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.WELL), CATEGORY_WELL);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
        IStackHelper stackHelper = registry.getJeiHelpers().getStackHelper();
        IRecipeTransferHandlerHelper transferHelper = registry.getTransferHelper();

        // T1 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarDiscovery.class,
                stackHelper, transferHelper, 9), CATEGORY_ALTAR_DISCOVERY);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarAttunement.class,
                stackHelper, transferHelper, 13), CATEGORY_ALTAR_DISCOVERY);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class,
                stackHelper, transferHelper, 21), CATEGORY_ALTAR_DISCOVERY);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                stackHelper, transferHelper, 25), CATEGORY_ALTAR_DISCOVERY);

        // T2 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarAttunement.class,
                stackHelper, transferHelper, 13), CATEGORY_ALTAR_ATTUNEMENT);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class,
                stackHelper, transferHelper, 21), CATEGORY_ALTAR_ATTUNEMENT);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                stackHelper, transferHelper, 25), CATEGORY_ALTAR_ATTUNEMENT);

        // T3 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarConstellation.class,
                stackHelper, transferHelper, 21), CATEGORY_ALTAR_CONSTELLATION);
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                stackHelper, transferHelper, 25), CATEGORY_ALTAR_CONSTELLATION);

        // T4 recipes
        registry.addRecipeTransferHandler(new TieredAltarRecipeTransferHandler<>(ContainerAltarTrait.class,
                stackHelper, transferHelper, 25), CATEGORY_ALTAR_TRAIT);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return AstralSorcery.key("jei_integration");
    }
}
