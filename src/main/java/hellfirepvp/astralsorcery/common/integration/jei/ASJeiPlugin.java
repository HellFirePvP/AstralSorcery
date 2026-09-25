/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
import hellfirepvp.astralsorcery.common.component.ArtifactTypeComponent;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.integration.jei.base.ASRecipeCategory;
import hellfirepvp.astralsorcery.common.integration.jei.base.ComponentSubtypeInterpreter;
import hellfirepvp.astralsorcery.common.integration.jei.base.StatefulCategory;
import hellfirepvp.astralsorcery.common.integration.jei.category.*;
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientHelper;
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientRenderer;
import hellfirepvp.astralsorcery.common.integration.jei.ingredient.LumenIngredientType;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.RecipeUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
    private final List<ASRecipeCategory<?>> categories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IJeiHelpers jeiHelpers = reg.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        this.registerCategory(reg, new LiquidInteractionRecipeCategory(guiHelper));
        this.registerCategory(reg, new LightwellRecipeCategory(guiHelper));
        this.registerCategory(reg, new FocalTransmutationRecipeCategory(guiHelper));
        this.registerCategory(reg, new FocalCombinationRecipeCategory(guiHelper));
        this.registerCategory(reg, new InfusionRecipeCategory(guiHelper));
        this.registerCategory(reg, new LumenGenerationRecipeCategory(guiHelper));
        this.registerCategory(reg, new LumenCrystallizationRecipeCategory(guiHelper));
    }

    private void registerCategory(IRecipeCategoryRegistration register, ASRecipeCategory<?> category) {
        this.categories.add(category);
        register.addRecipeCategories(category);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ItemsAS.CONSTELLATION_PAPER.asItem(),
                new ComponentSubtypeInterpreter<>(DataComponentsAS.CONSTELLATION_PAPER, cmp -> cmp.getConstellation().orElse(null)));
        registration.registerSubtypeInterpreter(ItemsAS.ARTIFACT.asItem(),
                new ComponentSubtypeInterpreter<>(DataComponentsAS.ARTIFACT, ArtifactComponent::artifactType));
        registration.registerSubtypeInterpreter(ItemsAS.ARTIFACT_SHARD.asItem(),
                new ComponentSubtypeInterpreter<>(DataComponentsAS.ARTIFACT_TYPE, ArtifactTypeComponent::type));

        registration.registerSubtypeInterpreter(ItemsAS.LUMEN_CRYSTAL.asItem(),
                new ComponentSubtypeInterpreter<>(DataComponentsAS.LUMEN, LumenComponent::lumen));
        registration.registerSubtypeInterpreter(ItemsAS.BLOCK_LUMEN_CRYSTAL_CLUSTER.asItem(),
                new ComponentSubtypeInterpreter<>(DataComponentsAS.LUMEN, LumenComponent::lumen));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(LumenIngredientType.INSTANCE,
                RegistriesAS.REGISTRY_LUMEN.stream()
                        .filter(lumen -> lumen != LumenAS.NONE.get())
                        .map(lumen -> LumenStack.of(lumen, LumenStack.FLASK_VALUE))
                        .toList(),
                new LumenIngredientHelper(registration.getSubtypeManager()),
                new LumenIngredientRenderer(LumenIngredientRenderer.Display.ICON),
                LumenStack.CODEC);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager mgr = RecipeUtil.getRecipeManager();

        this.categories.forEach(cat -> {
            registration.addRecipes(MiscUtil.cast(cat.getRecipeType()), cat.provideRecipes(mgr, registration));
        });
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        this.categories.forEach(cat -> {
            registration.addRecipeCatalysts(cat.getRecipeType(), VanillaTypes.ITEM_STACK, cat.provideCatalyst());
        });
    }

    @Override
    public void onRuntimeUnavailable() {
        this.categories.forEach(cat -> {
            if (cat instanceof StatefulCategory stateful) {
                stateful.clear();
            }
        });
    }
}
