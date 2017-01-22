/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.CategoryInfuser;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.InfuserRecipeHandler;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryRecipes;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;

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


        registry.addRecipeCategories(new CategoryInfuser(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeHandlers(new InfuserRecipeHandler());

        registry.addRecipeCategoryCraftingItem(new ItemStack(BlocksAS.starlightInfuser), idInfuser);
        registry.addRecipes(InfusionRecipeRegistry.recipes);
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
        blacklist.addItemToBlacklist(new ItemStack(ItemsAS.roseBranchBow));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }

}
