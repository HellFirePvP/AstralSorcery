/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.page;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.config.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.StaticIdentifierTicket;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.DebugConstellation;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.recipe.altar.output.AltarOutputReplaceWithInput;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.tome.TomePage;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.IngredientUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.CountIngredient;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector2f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderPageAltar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderPageAltar extends RenderPageRecipe<AltarRecipe> {

    public RenderPageAltar(@Nullable ResearchNode node, int nodePage, ResourceLocation recipeId) {
        super(node, nodePage, RecipeTypesAS.ALTAR_CRAFTING_TYPE, recipeId);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float pTicks, float mouseX, float mouseY) {
        this.clearFrame();
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;

        this.resolveRecipeOrWriteError(guiGraphics, x, y).map(RecipeHolder::value).ifPresent(recipe -> {
            this.renderPageOverlay(guiGraphics, x, y, this.resolveBackgroundTexture(recipe));
            this.renderRecipeHeader(guiGraphics, x, y, recipe.isOnlyNight());

            recipe.getFocusConstellation().ifPresent(cst -> {
                this.renderNeededConstellation(guiGraphics, x, y, recipe, cst);
            });

            this.renderOutputs(guiGraphics, connection.registryAccess(), x, y, recipe);
            this.renderInputGrid(guiGraphics, x, y, recipe);
            this.renderInnerRelayGrid(guiGraphics, x, y, recipe);
            this.renderOuterRelayGrid(guiGraphics, x, y, recipe);
            this.renderAdditionalInputs(guiGraphics, x, y, recipe);
            this.renderLumenInputs(guiGraphics, x, y, recipe);
            this.renderStarlightInputs(guiGraphics, x, y, recipe);

            this.renderEffects(guiGraphics, pTicks);
            this.renderHoverTooltips(guiGraphics, mouseX, mouseY);
        });
    }

    private void renderNeededConstellation(GuiGraphics guiGraphics, int x, int y, AltarRecipe recipe, BaseConstellation focusConstellation) {
        PlayerProgress progress = ResearchManager.getClientProgress();
        int cstX = x + 57;
        int cstY = y + 18;
        int size = 62;

        ColorWrapper color = ColorWrapper.WHITE;
        if (progress.hasDiscoveredConstellation(focusConstellation)) {
            color = focusConstellation.getConstellationColor();
        }

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderConstellationUtil.drawConstellationUI(color, focusConstellation, guiGraphics.pose(),
                cstX, cstY, size, size,
                1.4F, () -> 1F, progress.hasSeenConstellation(focusConstellation), false);
        RenderSystem.disableBlend();

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(progress.hasSeenConstellation(focusConstellation) ?
                Component.translatable("tome.research.info.recipe_constellation", focusConstellation.getColoredName()).withStyle(ChatFormatting.GRAY) :
                Component.translatable("tome.research.info.recipe_constellation.unknown").withStyle(ChatFormatting.GRAY));

        if (recipe.getBaseFocusShatterChance() > 0) {
            float displayChance = recipe.getBaseFocusShatterChance() * 100F;
            int estChance = Math.round(displayChance);

            if (displayChance < 1F) {
                tooltip.add(Component.translatable("tome.research.info.focus_shatter.small").withStyle(ChatFormatting.GRAY));
            } else if (displayChance >= 100F) {
                tooltip.add(Component.translatable("tome.research.info.focus_shatter.guaranteed").withStyle(ChatFormatting.GRAY));
            } else {
                // In case it rounds up from 99.5
                if (estChance >= 100) estChance = 99;
                tooltip.add(Component.translatable("tome.research.info.focus_shatter", estChance).withStyle(ChatFormatting.GRAY));
            }
        }

        this.thisFrameHovers.put(new IntRectangle(cstX, cstY, size, size), new TooltipInfo(tooltip));
    }

    private void renderOutputs(GuiGraphics guiGraphics, HolderLookup.Provider registries, int x, int y, AltarRecipe recipe) {
        int midX = x + TomePage.DEFAULT_WIDTH / 2;

        AltarCraftingInput displayInput = recipe.createInputForDisplay(ClientProxy.getClientTick());
        List<ItemStack> outputs = recipe.getOutputsForDisplay(displayInput, registries);
        ItemStack mainOutput = outputs.isEmpty() ? ItemStack.EMPTY : outputs.getFirst();
        this.renderOutput(guiGraphics, midX - 8, y + 18 + 60 / 2 - 8, 16, 16, mainOutput);

        List<ItemStack> remainingResults = outputs.size() <= 1 ? List.of() : outputs.subList(1, Math.min(outputs.size(), 5));
        int offsetX = midX + 60 / 2;
        int offsetY = y + 18;

        for (int i = 0; i < remainingResults.size(); i++) {
            ItemStack output = remainingResults.get(i);

            this.renderOutputSized(guiGraphics, offsetX, offsetY + i * 14, 12, 12, output, 0.75F);
        }
    }

    private void renderInputGrid(GuiGraphics guiGraphics, int x, int y, AltarRecipe recipe) {
        int centerOffsetX = x + 61;
        int centerOffsetY = y + 118;

        AltarRecipeGrid grid = recipe.getGrid();
        for (int index = 0; index < 9; index++) {
            int offsetX = (index % 3) * 18;
            int offsetY = (index / 3) * 18;
            IngredientBridge ingredient = grid.getInputs().get(index);
            if (!ingredient.isEmpty()) {
                long indexTick = ClientProxy.getClientTick() + index * 20L - 80L;
                this.renderInput(guiGraphics, centerOffsetX + offsetX, centerOffsetY + offsetY, indexTick, ingredient);
            }
        }
    }

    protected void renderInnerRelayGrid(GuiGraphics guiGraphics, int x, int y, AltarRecipe recipe) {
        int innerOffsetX = x + 42;
        int innerOffsetY = y + 99;

        for (int xx = 0; xx < 3; xx++) {
            for (int yy = 0; yy < 3; yy++) {
                int realSlot = (xx + 1) + (yy + 1) * 5;

                int offsetX = innerOffsetX + xx * 37;
                int offsetY = innerOffsetY + yy * 37;

                IngredientBridge relayInput = recipe.getGrid().getRelayInputs().get(realSlot);
                if (!relayInput.isEmpty()) {
                    this.renderInput(guiGraphics, offsetX, offsetY, relayInput);
                }
            }
        }
    }

    protected void renderOuterRelayGrid(GuiGraphics guiGraphics, int x, int y, AltarRecipe recipe) {
        TileAltar.getOuterRelaySlots().forEach(slot -> {
            IngredientBridge relayInput = recipe.getGrid().getRelayInputs().get(slot);
            if (relayInput.isEmpty()) {
                return;
            }

            int slotX = slot % 5;
            int slotY = slot / 5;
            int offsetX = x + switch (slotX) {
                case 1 -> 42;
                case 2 -> 79;
                case 3 -> 116;
                case 4 -> 133;
                default -> 25;
            };
            int offsetY = y + switch (slotY) {
                case 1 -> 99;
                case 2 -> 136;
                case 3 -> 173;
                case 4 -> 190;
                default -> 82;
            };

            this.renderInput(guiGraphics, offsetX, offsetY, relayInput);
        });
    }

    private void renderAdditionalInputs(GuiGraphics guiGraphics, int x, int y, AltarRecipe recipe) {
        int midX = x + TomePage.DEFAULT_WIDTH / 2 - 8;
        int midY = y + 145 - 8;
        float ringSize = 85F;

        int time = 2800;
        float effectPart = (ClientProxy.getClientTick() % time) / (float) time;

        List<CountIngredient> otherIngredients = recipe.getRequiredAdditionalInputs().stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .toList();
        List<FluidStack> otherFluidIngredients = recipe.getRequiredFluid().stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .toList();
        float count = otherIngredients.size() + otherFluidIngredients.size();
        if (!RenderingConfig.CONFIG.animateAdditionalInputs.get()) {
            int width = Mth.floor(count * 16 + (count - 1) * 4);
            midX = x + TomePage.DEFAULT_WIDTH / 2 - width / 2;
            midY = y + 10;
        }

        for (int i = 0; i < otherIngredients.size(); i++) {
            CountIngredient input = otherIngredients.get(i);

            float rotRad = (i / count) * Mth.TWO_PI;
            rotRad += Mth.PI;
            rotRad += effectPart * Mth.TWO_PI;

            float offsetX = Mth.sin(rotRad) * ringSize;
            float offsetY = Mth.cos(rotRad) * ringSize;
            if (!RenderingConfig.CONFIG.animateAdditionalInputs.get()) {
                offsetX = i * 20F;
                offsetY = 200;
            }

            long offset = ClientProxy.getClientTick() + i * 20L;
            this.renderInputExact(guiGraphics, midX + offsetX, midY + offsetY, offset, input.ingredient(), input.count());
        }
        for (int i = 0; i < otherFluidIngredients.size(); i++) {
            FluidStack fluidInput = otherFluidIngredients.get(i);

            float rotRad = ((i + otherIngredients.size()) / count) * Mth.TWO_PI;
            rotRad += Mth.PI;
            rotRad += effectPart * Mth.TWO_PI;

            float offsetX = Mth.sin(rotRad) * ringSize;
            float offsetY = Mth.cos(rotRad) * ringSize;
            if (!RenderingConfig.CONFIG.animateAdditionalInputs.get()) {
                offsetX = (i + otherIngredients.size()) * 20F;
                offsetY = 200;
            }

            this.renderLiquidInputExact(guiGraphics, midX + offsetX, midY + offsetY, fluidInput);
        }
    }

    private void renderLumenInputs(GuiGraphics guiGraphics, int x, int y, AltarRecipe recipe) {
        int offsetX = x + 14;
        int offsetY = y + 18;

        int maxCapacity = recipe.getRequiredLumen().stream().mapToInt(LumenStack::getAmount).max().orElse(0);
        if (maxCapacity <= 0) return;
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        PlayerProgress prog = ResearchManager.getClientProgress();

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();

        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(TexturesAS.ATLAS_LUMEN);
        for (LumenStack lumen : recipe.getRequiredLumen()) {
            if (lumen.isEmpty()) continue;
            final int thisOffsetX = offsetX;
            final int thisOffsetY = offsetY;

            lumen.getLumen().getRegistryKey().map(ResourceKey::location).ifPresent(id -> {
                TextureAtlasSprite tas = atlas.getSprite(id);

                AtlasTexture.getLumenAtlas().bindTexture();
                RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                    RenderQuadUtil.rect(buf, guiGraphics.pose(), thisOffsetX, thisOffsetY, 8, 8)
                            .color(lumen.getLumen().getColor(ClientProxy.getClientTick()))
                            .tex(UVFrame.fromAtlasSprite(tas))
                            .draw();
                });
            });

            int barX = offsetX + 9;
            int barY = offsetY + 1;
            int filledPx = Math.round(32 * ((float) lumen.getAmount() / maxCapacity));
            float filled = filledPx / 32F;

            TexturesAS.SCREEN_LUMEN_BAR_SMALL.bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                RenderQuadUtil.rect(buf, guiGraphics.pose(), barX, barY, 32, 5)
                        .color(ColorWrapper.opaque(0x666666))
                        .tex(new UVFrame(0, 0, 1, 0.5F))
                        .draw();

                RenderQuadUtil.rect(buf, guiGraphics.pose(), barX, barY, filledPx, 5)
                        .color(lumen.getLumen().getColor(ClientProxy.getClientTick()))
                        .tex(new UVFrame(0, 0.5F, filled, 0.5F))
                        .draw();
            });

            IntRectangle rctLumen = new IntRectangle(offsetX, offsetY, 41, 8);
            ColorWrapper color = lumen.getLumen().getColor(ClientProxy.getClientTick());
            MutableComponent name = lumen.getLumen().getHoverName().copy()
                    .withColor(color.getColor());
            if (!lumen.getLumen().maySee(level, prog)) {
                name = Component.translatable("lumen.astralsorcery.type.unknown").withColor(color.getColor());
            }

            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.literal(lumen.getAmount() + " ").withStyle(ChatFormatting.GRAY)
                    .append(name));
            this.thisFrameHovers.put(rctLumen, new TooltipInfo(tooltip));

            offsetY += 10;
        }

        RenderSystem.disableBlend();
    }

    private void renderStarlightInputs(GuiGraphics guiGraphics, int x, int y, AltarRecipe recipe) {
        if (recipe.getRequiredStarlight().isEmpty()) return;
        int offsetX = x + TomePage.DEFAULT_WIDTH / 2 + 60 / 2 + 20 + 8;
        int offsetY = y + 18;

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("tome.research.info.focused_starlight").withStyle(ChatFormatting.GRAY));
        for (BaseConstellation cst : recipe.getRequiredStarlight()) {
            tooltip.add(Component.literal("- ").withStyle(ChatFormatting.GRAY)
                    .append(cst.getColoredName()));
        }

        this.thisFrameHovers.put(new IntRectangle(offsetX - 6, offsetY, 12, 60), new TooltipInfo(tooltip));

        StaticIdentifierTicket.Container container = this.getEffectContainer();
        if (container.canAddEffects()) {
            RandomSource rand = RandomSource.create();

            if (ClientProxy.getClientTick() % 30 == 0) {
                Set<BaseConstellation> required = recipe.getRequiredStarlight();
                ColorWrapper color = ColorWrapper.WHITE;
                if (rand.nextInt(3 + required.size()) >= 3) {
                    color = MiscUtil.getRandomEntry(required, rand).map(BaseConstellation::getConstellationColor).orElse(ColorWrapper.WHITE);
                }

                float rX = rand.nextFloat();
                float rY = rand.nextFloat();
                container.createParticle(EffectTemplatesAS.SCREEN_LIGHT_BEAM, offsetX + rX, offsetY + 58 + rY)
                        .setup(new Vector2f(offsetX + rX, y + 18), 50F, 50F)
                        .color(FXColorFunction.constant(color));
            }

            for (int i = 0; i < 2; i++) {
                Vector3 offset = Vector3.random(rand).setZ(0).multiply(0.3F);
                Vector3 motion = new Vector3(offset.getX(), offset.getY() * 0.2F, 0);
                container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, offsetX + offset.getX() * 10F, offsetY + 58 + offset.getY() * 4F)
                        .color(rand.nextBoolean() ? FXColorFunction.WHITE : FXColorFunction.constant(ColorsAS.ROCK_CRYSTAL))
                        .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT))
                        .setScale(5F + rand.nextFloat() * 2F)
                        .setMotion(motion.multiply(0.4F));
            }
        }
    }

    private AbstractRenderTexture resolveBackgroundTexture(AltarRecipe recipe) {
        return switch (recipe.getRequiredType()) {
            case ILLUMINATION -> TexturesAS.SCREEN_TOME_PAGE_GRID_ALTAR_T1;
            case RESONANCE -> {
                int[] outerGridIndices = new int[] {
                        0,  1,  2,  3,  4,
                        5,              9,
                        10,             14,
                        15,             19,
                        20, 21, 22, 23, 24
                };
                List<IngredientBridge> relayInputs = recipe.getGrid().getRelayInputs();
                for (int slot : outerGridIndices) {
                    if (!relayInputs.get(slot).isEmpty()) {
                        yield TexturesAS.SCREEN_TOME_PAGE_GRID_ALTAR_T2_EXPANDED;
                    }
                }
                yield TexturesAS.SCREEN_TOME_PAGE_GRID_ALTAR_T2;
            }
            case LUMINANCE -> TexturesAS.SCREEN_TOME_PAGE_GRID_ALTAR_T3;
            case RADIANCE -> TexturesAS.SCREEN_TOME_PAGE_GRID_ALTAR_T4;
        };
    }

    protected void renderInputExact(GuiGraphics graphics, float offsetX, float offsetY, long tickOffset, Ingredient input, int stackCount) {
        ItemStack display = IngredientUtil.getRandomDisplayStack(input, tickOffset);
        if (!display.isEmpty()) {
            display = display.copyWithCount(stackCount);
            this.renderScaledItem(graphics, offsetX, offsetY, display, 1F);

            int minX = Mth.floor(offsetX);
            int minY = Mth.floor(offsetY);
            this.addHoverInfos(minX, minY, Mth.ceil(offsetX) + 16 - minX, Mth.ceil(offsetY) + 16 - minY, new TooltipInfo(display, this.getTooltip(display, input)));
        }
    }

    protected void renderLiquidInputExact(GuiGraphics graphics, float offsetX, float offsetY, FluidStack fluidStack) {
        TextureAtlasSprite sprite = RenderSpriteUtil.getTexture(fluidStack);
        int tint = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
        ColorWrapper color = ColorWrapper.opaque(tint);
        PoseStack pose = graphics.pose();

        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        AtlasTexture.getBlockAtlas().bindTexture();

        pose.pushPose();
        pose.translate(0, 0, 300);
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            RenderQuadUtil.rect(buf, graphics.pose(), offsetX, offsetY, 16, 16)
                    .color(color)
                    .tex(sprite)
                    .draw();
        });
        pose.popPose();

        RenderSystem.disableBlend();

        int minX = Mth.floor(offsetX);
        int minY = Mth.floor(offsetY);
        this.addHoverInfos(minX, minY, Mth.ceil(offsetX) + 16 - minX, Mth.ceil(offsetY) + 16 - minY, new TooltipInfo(this.getInputTooltip(fluidStack)));
    }

    public void renderOutputSized(GuiGraphics graphics, int offsetX, int offsetY, int width, int height, ItemStack output, float size) {
        if (output.isEmpty()) return;

        this.renderScaledItem(graphics, offsetX, offsetY, output, size);

        List<Component> tooltip = new ArrayList<>();
        this.addTooltip(output, tooltip);
        if (Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
            this.resolveRecipe().ifPresent(recipe -> {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("screen.astralsorcery.element.recipe_debug.registry_name", recipe.id().toString())
                        .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
                tooltip.add(Component.translatable("screen.astralsorcery.element.recipe_debug.ctrl_copy")
                        .withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));

            });
        }
        this.addHoverInfos(offsetX, offsetY, width, height, new TooltipInfo(output, tooltip));
        this.recipeIdCopyHovers.add(new IntRectangle(offsetX, offsetY, width, height));
    }
}
