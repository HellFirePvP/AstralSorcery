/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ASJeiPlugin
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
@JeiPlugin
public class ASJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID = AstralSorcery.key("jei");
    private LiquidInteractionRecipeCategory interactionCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        this.interactionCategory = new LiquidInteractionRecipeCategory(registration.getJeiHelpers().getGuiHelper());
        registration.addRecipeCategories(this.interactionCategory);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(LiquidInteractionRecipeCategory.RECIPE_TYPE, collectRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new net.minecraft.world.item.ItemStack(BlocksAS.CHALICE.get()), LiquidInteractionRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void onRuntimeUnavailable() {
        if (this.interactionCategory != null) {
            this.interactionCategory.clearEntityCache();
        }
    }

    private static List<LiquidInteractionRecipe> collectRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        RecipeManager manager = level.getRecipeManager();
        List<LiquidInteractionRecipe> out = new ArrayList<>();
        for (RecipeHolder<LiquidInteractionRecipe> holder : manager.getAllRecipesFor(RecipeTypesAS.LIQUID_INTERACTION_TYPE.get())) {
            out.add(holder.value());
        }
        return out;
    }
}
