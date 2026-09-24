/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.LiquidInteractionRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResult;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultDropItem;
import hellfirepvp.astralsorcery.common.recipe.liquid.interaction.result.LiquidInteractionResultSpawnEntity;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionRecipeCategory
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionRecipeCategory implements IRecipeCategory<LiquidInteractionRecipe> {

    public static final ResourceLocation UID = AstralSorcery.key("liquid_interaction");
    public static final RecipeType<LiquidInteractionRecipe> RECIPE_TYPE = new RecipeType<>(UID, LiquidInteractionRecipe.class);

    private static final DecimalFormat FORMAT_CHANCE = new DecimalFormat("0.00");

    private static final int WIDTH = 112;
    private static final int HEIGHT = 54;
    private static final int SLOT_A_X = 3;
    private static final int SLOT_B_X = 93;
    private static final int SLOT_Y = 19;
    private static final int OUTPUT_X = 47;
    private static final int OUTPUT_Y = 18;

    private final IDrawable icon;
    private final Map<EntityType<?>, Entity> entityPreviewCache = new IdentityHashMap<>();

    public void clearEntityCache() {
        this.entityPreviewCache.clear();
    }

    public LiquidInteractionRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlocksAS.CHALICE.get()));
    }

    @Override
    public RecipeType<LiquidInteractionRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.astralsorcery.category.liquid_interaction");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LiquidInteractionRecipe recipe, IFocusGroup focuses) {
        addReactantSlot(builder, recipe.getReactantA(), SLOT_A_X, SLOT_Y);
        addReactantSlot(builder, recipe.getReactantB(), SLOT_B_X, SLOT_Y);

        if (recipe.getResult() instanceof LiquidInteractionResultDropItem dropItem) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                    .addItemStack(dropItem.getOutput());
        }
    }

    private static void addReactantSlot(IRecipeLayoutBuilder builder, SizedFluidIngredient reactant, int x, int y) {
        List<FluidStack> variants = collectVariants(reactant);
        builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                .setFluidRenderer(reactant.amount(), false, 16, 16)
                .addIngredients(NeoForgeTypes.FLUID_STACK, variants);
    }

    private static List<FluidStack> collectVariants(SizedFluidIngredient reactant) {
        FluidStack[] samples = reactant.getFluids();
        Set<Fluid> seen = new HashSet<>();
        List<FluidStack> variants = new ArrayList<>();
        for (FluidStack sample : samples) {
            if (sample.isEmpty() || !seen.add(sample.getFluid())) {
                continue;
            }
            variants.add(sample.copyWithAmount(reactant.amount()));
        }
        return variants;
    }

    @Override
    public void draw(LiquidInteractionRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        this.icon.draw(graphics, 3, 36);
        this.icon.draw(graphics, 93, 36);

        if (recipe.getResult() instanceof LiquidInteractionResultSpawnEntity spawnEntity) {
            this.drawEntityPreview(graphics, spawnEntity);
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        int totalWeight = 0;
        for (RecipeHolder<LiquidInteractionRecipe> holder :
                mc.level.getRecipeManager().getAllRecipesFor(RecipeTypesAS.LIQUID_INTERACTION_TYPE.get())) {
            LiquidInteractionRecipe other = holder.value();
            if (sharesReactants(recipe, other)) {
                totalWeight += other.getWeight();
            }
        }
        if (totalWeight > 0) {
            float perc = ((float) recipe.getWeight() / totalWeight) * 100F;
            Component label = Component.translatable("jei.astralsorcery.tip.chance", FORMAT_CHANCE.format(perc)).withStyle(ChatFormatting.DARK_GRAY);
            int width = mc.font.width(label);
            graphics.drawString(mc.font, label, 74 - width, 44, 0x333333, false);
        }
    }

    private void drawEntityPreview(GuiGraphics graphics, LiquidInteractionResultSpawnEntity spawnEntity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity preview = this.entityPreviewCache.computeIfAbsent(spawnEntity.getEntityType(), type -> type.create(mc.level));
        if (preview == null) {
            return;
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(OUTPUT_X + 8, OUTPUT_Y + 16, 50);
        pose.scale(15F, 15F, 15F);
        pose.mulPose(Axis.XP.rotationDegrees(180F));
        pose.mulPose(Axis.YP.rotationDegrees(145F));

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);

        MultiBufferSource.BufferSource buffer = graphics.bufferSource();
        dispatcher.render(preview, 0, 0, 0, 0, 0, pose, buffer, LightmapUtil.getPackedFullbrightCoords());
        buffer.endBatch();

        dispatcher.setRenderShadow(true);
        pose.popPose();
    }

    private static boolean sharesReactants(LiquidInteractionRecipe a, LiquidInteractionRecipe b) {
        FluidStack[] sampleA1 = a.getReactantA().getFluids();
        FluidStack[] sampleA2 = a.getReactantB().getFluids();
        if (sampleA1.length == 0 || sampleA2.length == 0) {
            return false;
        }
        return b.matches(sampleA1[0], sampleA2[0]);
    }
}
