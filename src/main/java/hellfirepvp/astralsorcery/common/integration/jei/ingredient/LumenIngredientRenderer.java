/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration.jei.ingredient;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderQuadUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.common.gui.JeiTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.TooltipFlag;

import java.text.NumberFormat;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenIngredientRenderer
 * Created by HellFirePvP
 * Date: 25.09.2026 / 20:33
 */
public class LumenIngredientRenderer implements IIngredientRenderer<LumenStack> {

    private static final NumberFormat nf = NumberFormat.getIntegerInstance();

    private final Display display;
    private final TooltipMode tooltipMode;
    private final int capacity;

    public LumenIngredientRenderer(Display display) {
        this(display, display == Display.ICON ? TooltipMode.ITEM_LIST : TooltipMode.SHOW_AMOUNT, LumenStack.FLASK_VALUE);
    }

    public LumenIngredientRenderer(Display display, TooltipMode tooltipMode, int capacity) {
        this.display = display;
        this.tooltipMode = tooltipMode;
        this.capacity = capacity;
    }

    @Override
    public void render(GuiGraphics guiGraphics, LumenStack ingredient) {
        this.render(guiGraphics, ingredient, 0, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, LumenStack ingredient, int posX, int posY) {
        RenderSystem.enableBlend();

        if (this.display == Display.ICON) {
            this.renderIcon(guiGraphics, ingredient, posX, posY, 16, 16);
        } else {
            this.renderIcon(guiGraphics, ingredient, posX, posY, 8, 8);

            int barWidth = this.display.getWidth() - 8 - 2;
            this.renderBar(guiGraphics, this.display.getBarTexture(), ingredient, posX + 8 + 2, posY + 3, barWidth);
        }

        RenderSystem.disableBlend();
    }

    private void renderIcon(GuiGraphics guiGraphics, LumenStack ingredient, int posX, int posY, int width, int height) {
        if (ingredient.isEmpty()) return;

        ingredient.getLumen().getRegistryKey().map(ResourceKey::location).ifPresent(id -> {
            TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(TexturesAS.ATLAS_LUMEN);
            TextureAtlasSprite tas = atlas.getSprite(id);

            AtlasTexture.getLumenAtlas().bindTexture();
            RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
                RenderQuadUtil.rect(buf, guiGraphics.pose(), posX, posY, width, height)
                        .color(ingredient.getLumen().getColor(ClientProxy.getClientTick()))
                        .tex(UVFrame.fromAtlasSprite(tas))
                        .draw();
            });
        });
    }

    private void renderBar(GuiGraphics guiGraphics, AbstractRenderTexture texture, LumenStack ingredient, int posX, int posY, int width) {
        int filledPx = Math.min(Math.round(width * ((float) ingredient.getAmount() / this.capacity)), width);
        float filled = (float) filledPx / width;

        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), texture,
                ColorWrapper.opaque(0x666666),
                posX, posY, width, 5,
                new UVFrame(0, 0, 1, 0.5F));

        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), texture,
                ingredient.getLumen().getColor(ClientProxy.getClientTick()),
                posX, posY, filledPx, 5,
                new UVFrame(0, 0.5F, filled, 0.5F));
    }

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltip(LumenStack ingredient, TooltipFlag tooltipFlag) {
        JeiTooltip jeiTooltip = new JeiTooltip();
        getTooltip(jeiTooltip, ingredient, tooltipFlag);
        return jeiTooltip.toLegacyToComponents();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, LumenStack ingredient, TooltipFlag tooltipFlag) {
        if (ingredient.isEmpty()) return;

        tooltip.add(ingredient.getLumen().getHoverName());

        int amt = ingredient.getAmount();

        if (this.tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
            MutableComponent amountStr = Component.translatable("jei.astralsorcery.info.lumen.amount.capacity", nf.format(amt), nf.format(this.capacity));
            tooltip.add(amountStr.withStyle(ChatFormatting.GRAY));
        } else if (this.tooltipMode == TooltipMode.SHOW_AMOUNT) {
            MutableComponent amountStr = Component.translatable("jei.astralsorcery.info.lumen.amount", nf.format(amt));
            tooltip.add(amountStr.withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public int getWidth() {
        return this.display.getWidth();
    }

    @Override
    public int getHeight() {
        return this.display.getHeight();
    }

    public enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }

    public enum Display {

        ICON(16, 16, () -> TexturesAS.SCREEN_LUMEN_BAR_SMALL),
        SHORT_BAR(8 + 2 + 32, 8, () -> TexturesAS.SCREEN_LUMEN_BAR_SMALL),
        MEDIUM_BAR(8 + 2 + 48, 8, () -> TexturesAS.SCREEN_LUMEN_BAR_MEDIUM),
        LONG_BAR(8 + 2 + 64, 8, () -> TexturesAS.SCREEN_LUMEN_BAR_LARGE);

        private final int width, height;
        private final Supplier<AbstractRenderTexture> barSupplier;

        Display(int width, int height, Supplier<AbstractRenderTexture> barSupplier) {
            this.width = width;
            this.height = height;
            this.barSupplier = barSupplier;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public AbstractRenderTexture getBarTexture() {
            return this.barSupplier.get();
        }
    }
}
