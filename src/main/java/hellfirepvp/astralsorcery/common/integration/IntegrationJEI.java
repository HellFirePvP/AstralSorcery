package hellfirepvp.astralsorcery.common.integration;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.container.ContainerAltarAttunement;
import hellfirepvp.astralsorcery.common.container.ContainerAltarConstellation;
import hellfirepvp.astralsorcery.common.container.ContainerAltarDiscovery;
import hellfirepvp.astralsorcery.common.container.ContainerAltarTrait;
import hellfirepvp.astralsorcery.common.integration.jei.CategoryAltar;
import hellfirepvp.astralsorcery.common.integration.jei.CategoryInfuser;
import hellfirepvp.astralsorcery.common.integration.jei.CategoryTransmutation;
import hellfirepvp.astralsorcery.common.integration.jei.CategoryWell;
import hellfirepvp.astralsorcery.common.integration.jei.TieredAltarRecipeTransferHandler;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IntegrationJEI
 * Created by HellFirePvP
 * Date: 25.07.2020 / 09:23
 */
@JeiPlugin
public class IntegrationJEI implements IModPlugin {

    public static final ResourceLocation CATEGORY_ALTAR_ATTUNEMENT = AstralSorcery.key("altar_attunement");
    public static final ResourceLocation CATEGORY_ALTAR_CONSTELLATION = AstralSorcery.key("altar_constellation");
    public static final ResourceLocation CATEGORY_ALTAR_DISCOVERY = AstralSorcery.key("altar_discovery");
    public static final ResourceLocation CATEGORY_ALTAR_TRAIT = AstralSorcery.key("altar_trait");
    public static final ResourceLocation CATEGORY_INFUSER = AstralSorcery.key("infuser");
    public static final ResourceLocation CATEGORY_TRANSMUTATION = AstralSorcery.key("transmutation");
    public static final ResourceLocation CATEGORY_WELL = AstralSorcery.key("well");

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

        registry.addRecipeCategories(
                new CategoryAltar(CATEGORY_ALTAR_DISCOVERY, "altar_discovery", BlocksAS.ALTAR_DISCOVERY, guiHelper),
                new CategoryAltar(CATEGORY_ALTAR_ATTUNEMENT, "altar_attunement", BlocksAS.ALTAR_ATTUNEMENT, guiHelper),
                new CategoryAltar(CATEGORY_ALTAR_CONSTELLATION, "altar_constellation", BlocksAS.ALTAR_CONSTELLATION, guiHelper),
                new CategoryAltar(CATEGORY_ALTAR_TRAIT, "altar_trait", BlocksAS.ALTAR_RADIANCE, guiHelper),
                new CategoryInfuser(guiHelper),
                new CategoryTransmutation(guiHelper),
                new CategoryWell(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RecipeTypesAS.TYPE_ALTAR.getRecipes(recipe -> recipe.getAltarType().equals(AltarType.DISCOVERY)), CATEGORY_ALTAR_DISCOVERY);
        registry.addRecipes(RecipeTypesAS.TYPE_ALTAR.getRecipes(recipe -> recipe.getAltarType().equals(AltarType.ATTUNEMENT)), CATEGORY_ALTAR_ATTUNEMENT);
        registry.addRecipes(RecipeTypesAS.TYPE_ALTAR.getRecipes(recipe -> recipe.getAltarType().equals(AltarType.CONSTELLATION)), CATEGORY_ALTAR_CONSTELLATION);
        registry.addRecipes(RecipeTypesAS.TYPE_ALTAR.getRecipes(recipe -> recipe.getAltarType().equals(AltarType.RADIANCE)), CATEGORY_ALTAR_TRAIT);
        registry.addRecipes(RecipeTypesAS.TYPE_INFUSION.getAllRecipes(), CATEGORY_INFUSER);
        registry.addRecipes(RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getAllRecipes(), CATEGORY_TRANSMUTATION);
        registry.addRecipes(RecipeTypesAS.TYPE_WELL.getAllRecipes(), CATEGORY_WELL);
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
    public ResourceLocation getPluginUid() {
        return AstralSorcery.key("jei_integration");
    }
}
