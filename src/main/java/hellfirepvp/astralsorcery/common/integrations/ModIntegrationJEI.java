/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.*;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.*;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationJEI
 * Created by HellFirePvP
 * Date: 10.01.2017 / 23:21
 */
@JEIPlugin
public class ModIntegrationJEI implements IModPlugin {

    private static Map<Class<?>, Tuple<IRecipeWrapperFactory, String>> factoryMap = new HashMap<>();
    private static List<RecipeChange> recipePrimer = new LinkedList<>();
    private static List<Tuple<Object, ModificationAction>> unresolvedRecipes = new LinkedList<>();

    public static boolean jeiRegistrationPhase = true;

    public static final String idWell = "astralsorcery.lightwell";
    public static final String idGrindstone = "astralsorcery.grindstone";
    public static final String idInfuser = "astralsorcery.infuser";
    public static final String idTransmutation = "astralsorcery.lightTransmutation";

    public static final String idAltarDiscovery = "astralsorcery.altar.discovery";
    public static final String idAltarAttunement = "astralsorcery.altar.attunement";
    public static final String idAltarConstellation = "astralsorcery.altar.constellation";
    public static final String idAltarTrait = "astralsorcery.altar.trait";

    public static IStackHelper stackHelper;
    public static IJeiHelpers jeiHelpers;
    public static IRecipeRegistry recipeRegistry;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(ItemsAS.wand, ItemsAS.armorImbuedCape);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {}

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(
                new CategoryWell(guiHelper),
                new CategoryGrindstone(guiHelper),
                new CategoryInfuser(guiHelper),
                new CategoryTransmutation(guiHelper),

                new CategoryAltarTrait(guiHelper),
                new CategoryAltarConstellation(guiHelper),
                new CategoryAltarAttunement(guiHelper),
                new CategoryAltarDiscovery(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        jeiHelpers = registry.getJeiHelpers();
        stackHelper = jeiHelpers.getStackHelper();
        hideItems(registry.getJeiHelpers().getIngredientBlacklist());

        registerRecipeHandle(registry, WellLiquefaction.LiquefactionEntry.class,   WellRecipeWrapper::new,               idWell);
        registerRecipeHandle(registry, GrindstoneRecipe.class,                     GrindstoneRecipeWrapper::new,         idGrindstone);
        registerRecipeHandle(registry, AbstractInfusionRecipe.class,               InfuserRecipeWrapper::new,            idInfuser);
        registerRecipeHandle(registry, LightOreTransmutations.Transmutation.class, TransmutationRecipeWrapper::new,      idTransmutation);
        registerRecipeHandle(registry, TraitRecipe.class,                          AltarTraitRecipeWrapper::new,         idAltarTrait);
        registerRecipeHandle(registry, ConstellationRecipe.class,                  AltarConstellationRecipeWrapper::new, idAltarConstellation);
        registerRecipeHandle(registry, AttunementRecipe.class,                     AltarAttunementRecipeWrapper::new,    idAltarAttunement);
        registerRecipeHandle(registry, DiscoveryRecipe.class,                      AltarDiscoveryRecipeWrapper::new,     idAltarDiscovery);

        registry.addRecipeCatalyst(new ItemStack(BlocksAS.blockWell), idWell);
        registry.addRecipeCatalyst(BlockMachine.MachineType.GRINDSTONE.asStack(), idGrindstone);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.starlightInfuser), idInfuser);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.lens), idTransmutation);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.lensPrism), idTransmutation);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()), idAltarDiscovery);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()), idAltarAttunement);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), idAltarConstellation);
        registry.addRecipeCatalyst(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()), idAltarTrait);

        registry.addRecipes(InfusionRecipeRegistry.recipes, idInfuser);
        registry.addRecipes(GrindstoneRecipeRegistry.getValidRecipes(), idGrindstone);
        registry.addRecipes(LightOreTransmutations.getRegisteredTransmutations(), idTransmutation);
        registry.addRecipes(WellLiquefaction.getRegisteredLiquefactions(), idWell);

        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.DISCOVERY), idAltarDiscovery);
        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.ATTUNEMENT), idAltarAttunement);
        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.CONSTELLATION_CRAFT), idAltarConstellation);
        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.TRAIT_CRAFT), idAltarTrait);

        registry.addRecipes(Lists.newArrayList(
                RecipesAS.rCCParchment        ,
                RecipesAS.rRJournal           ,
                RecipesAS.rBlackMarbleRaw     ,
                RecipesAS.rBlackMarbleArch    ,
                RecipesAS.rBlackMarbleBricks  ,
                RecipesAS.rBlackMarbleChiseled,
                RecipesAS.rBlackMarbleEngraved,
                RecipesAS.rBlackMarblePillar  ,
                RecipesAS.rBlackMarbleRuned   ,
                RecipesAS.rMarbleArch         ,
                RecipesAS.rMarbleBricks       ,
                RecipesAS.rMarbleChiseled     ,
                RecipesAS.rMarbleEngraved     ,
                RecipesAS.rMarblePillar       ,
                RecipesAS.rMarbleRuned        ,
                RecipesAS.rMarbleStairs       ,
                RecipesAS.rMarbleSlab         ), VanillaRecipeCategoryUid.CRAFTING);

        jeiRegistrationPhase = false;
    }

    private void hideItems(IIngredientBlacklist blacklist) {
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockFakeTree));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.translucentBlock));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockVanishing));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockStructural));
        blacklist.addIngredientToBlacklist(new ItemStack(BlocksAS.blockAltar, 1, 4));
        if(Mods.GEOLOSYS.isPresent() && Mods.ORESTAGES.isPresent()) {
            ModIntegrationGeolosys.hideJEIGeolosysSample(blacklist);
        }
    }

    private <T> void registerRecipeHandle(IModRegistry registry, Class<T> recipeClass, IRecipeWrapperFactory<T> factory, String categoryId) {
        factoryMap.put(recipeClass, new Tuple<>(factory, categoryId));
        registry.handleRecipes(recipeClass, factory, categoryId);
    }

    public static boolean addRecipe(Object recipe) {
        Tuple<IRecipeWrapperFactory, String> factoryTuple = findRecipeWrapperFor(recipe);
        if(factoryTuple != null) {
            RecipeChange change = new RecipeChange(factoryTuple.key.getRecipeWrapper(recipe), factoryTuple.value, ModificationAction.ADDITION);
            if(recipeRegistry == null) {
                recipePrimer.add(change);
            } else {
                change.apply(recipeRegistry);
            }
            return true;
        }
        unresolvedRecipes.add(new Tuple<>(recipe, ModificationAction.ADDITION));
        return false;
    }

    public static boolean removeRecipe(Object recipe) {
        Tuple<IRecipeWrapperFactory, String> factoryTuple = findRecipeWrapperFor(recipe);
        if(factoryTuple != null) {
            RecipeChange change = new RecipeChange(factoryTuple.key.getRecipeWrapper(recipe), factoryTuple.value, ModificationAction.REMOVAL);
            if(recipeRegistry == null) {
                recipePrimer.add(change);
            } else {
                change.apply(recipeRegistry);
            }
            return true;
        }
        unresolvedRecipes.add(new Tuple<>(recipe, ModificationAction.REMOVAL));
        return false;
    }

    @Nullable
    private static Tuple<IRecipeWrapperFactory, String> findRecipeWrapperFor(Object recipe) {
        Class<?> recipeClass = recipe.getClass();
        Tuple<IRecipeWrapperFactory, String> factoryTuple = factoryMap.get(recipeClass);
        while (factoryTuple == null && !recipeClass.equals(Object.class)) {
            recipeClass = recipeClass.getSuperclass();
            factoryTuple = factoryMap.get(recipeClass);
        }
        return factoryTuple;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        recipeRegistry = jeiRuntime.getRecipeRegistry();

        for (RecipeChange change : recipePrimer) {
            change.apply(recipeRegistry);
        }
        recipePrimer.clear();

        Iterator<Tuple<Object, ModificationAction>> iterator = unresolvedRecipes.iterator();
        while (iterator.hasNext()) {
            Tuple<Object, ModificationAction> action = iterator.next();
            switch (action.value) {
                case ADDITION:
                    if (addRecipe(action.key)) {
                        iterator.remove();
                    }
                    break;
                case REMOVAL:
                    if (removeRecipe(action.key)) {
                        iterator.remove();
                    }
                    break;
            }
        }
        if(unresolvedRecipes.size() > 0) {
            AstralSorcery.log.warn("[AstralSorcery] JEI Initialization Ended up with " + unresolvedRecipes.size() + " unresolvable crafttweaker recipes!");
        }
    }

    private static class RecipeChange {

        private final IRecipeWrapper recipe;
        private final String category;
        private final ModificationAction action;

        private RecipeChange(IRecipeWrapper recipe, String category, ModificationAction action) {
            this.recipe = recipe;
            this.category = category;
            this.action = action;
        }

        private void apply(IRecipeRegistry recipeRegistry) {
            if(action == ModificationAction.ADDITION) {
                recipeRegistry.addRecipe(this.recipe, this.category);
            } else {
                recipeRegistry.removeRecipe(this.recipe, this.category);
            }
        }

    }

    private static enum ModificationAction {

        ADDITION,
        REMOVAL

    }

}
