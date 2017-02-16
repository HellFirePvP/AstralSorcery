/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.CategoryInfuser;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.CategoryTransmutation;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.InfuserRecipeHandler;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.TransmutationRecipeHandler;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.AltarAttunementRecipeHandler;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.AltarConstellationRecipeHandler;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.AltarDiscoveryRecipeHandler;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.CategortAltarAttunement;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.CategoryAltarConstellation;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.CategoryAltarDiscovery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryRecipes;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationJEI
 * Created by HellFirePvP
 * Date: 10.01.2017 / 23:21
 */
@JEIPlugin
public class ModIntegrationJEI implements IModPlugin {

    public static boolean jeiRegistrationPhase = true;

    public static final String idInfuser = "astralsorcery.infuser";
    public static final String idTransmutation = "astralsorcery.lightTransmutation";

    public static final String idAltarDiscovery = "astralsorcery.altar.discovery";
    public static final String idAltarAttunement = "astralsorcery.altar.attunement";
    public static final String idAltarConstellation = "astralsorcery.altar.constellation";

    public static IStackHelper stackHelper;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void register(IModRegistry registry) {
        stackHelper = registry.getJeiHelpers().getStackHelper();
        hideItems(registry.getJeiHelpers().getItemBlacklist());
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        //REMINDER: Higher tiers *must* come before lower tiers in this list.
        registry.addRecipeCategories(
                new CategoryInfuser(guiHelper),
                new CategoryTransmutation(guiHelper),
                new CategoryAltarConstellation(guiHelper),
                new CategortAltarAttunement(guiHelper),
                new CategoryAltarDiscovery(guiHelper));

        registry.addRecipeHandlers(
                new InfuserRecipeHandler(),
                new TransmutationRecipeHandler(),
                new AltarConstellationRecipeHandler(),
                new AltarAttunementRecipeHandler(),
                new AltarDiscoveryRecipeHandler());

        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksAS.starlightInfuser), idInfuser);
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksAS.lens), idTransmutation);
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksAS.lensPrism), idTransmutation);
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()), idAltarDiscovery);
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()), idAltarAttunement);
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), idAltarConstellation);

        registry.addRecipes(InfusionRecipeRegistry.recipes);
        List<LightOreTransmutations.Transmutation> transmutations = Lists.newArrayList(LightOreTransmutations.getRegisteredTransmutations());
        transmutations.add(new LightOreTransmutations.Transmutation(Blocks.IRON_ORE.getDefaultState(), BlocksAS.customOre.getDefaultState().withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.STARMETAL), 200));
        registry.addRecipes(transmutations);

        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.DISCOVERY));
        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.ATTUNEMENT));
        registry.addRecipes(AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.CONSTELLATION_CRAFT));

        registry.addRecipes(Lists.newArrayList(
                RegistryRecipes.rRJournal      .makeNative(),
                RegistryRecipes.rBlackMarbleRaw.makeNative(),
                RegistryRecipes.rMarbleArch    .makeNative(),
                RegistryRecipes.rMarbleBricks  .makeNative(),
                RegistryRecipes.rMarbleChiseled.makeNative(),
                RegistryRecipes.rMarbleEngraved.makeNative(),
                RegistryRecipes.rMarblePillar  .makeNative(),
                RegistryRecipes.rMarbleRuned   .makeNative()));

        jeiRegistrationPhase = false;
    }

    private void hideItems(IItemBlacklist blacklist) {
        blacklist.addItemToBlacklist(new ItemStack(BlocksAS.blockFakeTree));
        blacklist.addItemToBlacklist(new ItemStack(BlocksAS.translucentBlock));
        blacklist.addItemToBlacklist(new ItemStack(BlocksAS.blockStructural));
        blacklist.addItemToBlacklist(new ItemStack(BlocksAS.blockAltar, 1, 3));
        blacklist.addItemToBlacklist(new ItemStack(BlocksAS.blockAltar, 1, 4));
        blacklist.addItemToBlacklist(new ItemStack(BlocksAS.ritualLink));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }

}
