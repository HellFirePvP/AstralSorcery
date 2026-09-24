/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.tooltip;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicketManager;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.TooltipIdTicket;
import hellfirepvp.astralsorcery.client.util.ComponentEffectUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.util.ColorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tooltip.StoredLumenDisplayTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StoredLumenClientComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StoredLumenClientComponent implements ClientTooltipComponent {

    public static final int EFFECT_BAR_WIDTH = 64 + 2 * 3;
    private static final int EFFECT_BOTTOM_PADDING = 3;
    private static final int EFFECT_ROW_HEIGHT = 5 /* bar */ + 6 /* top padding */ + EFFECT_BOTTOM_PADDING;
    private static final int TEXT_X_OFFSET = 3;

    private final RandomSource rand = RandomSource.create();
    private final ItemStack stack;
    private final IdentifierComponent identifier;
    private final StoredLumenComponent dataComponent;
    private final int maxComponentWidth;
    private final IntRectangle dimensions;

    protected StoredLumenClientComponent(ItemStack stack, IdentifierComponent identifier, StoredLumenComponent dataComponent, int maxComponentWidth) {
        this.stack = stack;
        this.identifier = identifier;
        this.dataComponent = dataComponent;
        this.maxComponentWidth = Math.max(maxComponentWidth, EFFECT_BAR_WIDTH);

        this.dimensions = this.calcDimensions();
    }

    public static StoredLumenClientComponent create(StoredLumenDisplayTooltip tooltip) {
        return new StoredLumenClientComponent(tooltip.stack(), tooltip.identifier(), tooltip.lumenComponent(), tooltip.maxComponentWidth());
    }

    private IntRectangle calcDimensions() {
        Font font = Minecraft.getInstance().font;

        int totalHeight = 0;
        int totalWidth = EFFECT_BAR_WIDTH;
        for (StoredLumenComponent.StoredLumen store : this.dataComponent.getLumenDisplay()) {
            List<Component> displayTexts = this.dataComponent.getLumenDisplayTexts(LogicalSide.CLIENT, this.stack, store.lumen());
            for (Component displayText : displayTexts) {
                int msgWidth = TEXT_X_OFFSET + font.width(displayText);
                if (msgWidth > totalWidth) {
                    totalWidth = msgWidth;
                }
            }
        }

        int componentWidth = Math.min(totalWidth, this.maxComponentWidth);
        for (StoredLumenComponent.StoredLumen store : this.dataComponent.getLumenDisplay()) {
            totalHeight += EFFECT_ROW_HEIGHT;

            List<Component> displayTexts = this.dataComponent.getLumenDisplayTexts(LogicalSide.CLIENT, this.stack, store.lumen());
            if (!displayTexts.isEmpty()) {
                for (Component cmp : displayTexts) {
                    totalHeight += font.split(cmp, componentWidth).size() * (font.lineHeight + 1);
                }
            }
        }
        return new IntRectangle(0, 0, componentWidth, totalHeight);
    }

    @Override
    public int getHeight() {
        return this.dimensions.height();
    }

    @Override
    public int getWidth(Font font) {
        return this.dimensions.width();
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        TooltipIdTicket.Container container = this.resolveEffectContainer();
        container.setTooltipPosition(x, y);
        long effectTick = ClientProxy.getClientTick();

        if (container.canAddEffects()) {
            this.createParticles(container);
        }
        container.renderAll(guiGraphics, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));

        int stepY = y;
        for (StoredLumenComponent.StoredLumen store : this.dataComponent.getLumenDisplay()) {
            this.renderLumenBar(guiGraphics, store, effectTick, x + 3, stepY + 6);
            stepY += EFFECT_ROW_HEIGHT;

            List<Component> displayTexts = this.dataComponent.getLumenDisplayTexts(LogicalSide.CLIENT, this.stack, store.lumen());
            stepY += -EFFECT_BOTTOM_PADDING + 1;

            int color = store.lumen().getColor(effectTick).getColor();
            if (store.amount() <= 200) {
                color = ColorUtil.blendColors(color, 0xFF676767, Math.max(0.1F, store.amount() / 200F));
            }

            for (Component displayText : displayTexts) {
                ComponentEffectUtil.applyStyle(displayText, Style.EMPTY.withColor(color));
                for (FormattedCharSequence text : font.split(displayText, this.maxComponentWidth)) {
                    guiGraphics.drawString(font, text, TEXT_X_OFFSET + x, stepY, ColorWrapper.WHITE.getColor(), true);
                    stepY += font.lineHeight + 1;
                }
            }
        }
    }

    private void renderLumenBar(GuiGraphics guiGraphics, StoredLumenComponent.StoredLumen store, long effectTick, int xOffset, int yOffset) {
        int filledPx = Math.round(64 * ((float) store.amount() / store.maxAmount()));
        float filled = filledPx / 64F;

        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), TexturesAS.SCREEN_LUMEN_BAR_LARGE,
                ColorWrapper.opaque(0x666666),
                xOffset, yOffset, 64, 5,
                new UVFrame(0, 0, 1, 0.5F));

        RenderingDrawUtil.drawTexturedRectColor(guiGraphics.pose(), TexturesAS.SCREEN_LUMEN_BAR_LARGE,
                store.lumen().getColor(effectTick),
                xOffset, yOffset, filledPx, 5,
                new UVFrame(0, 0.5F, filled, 0.5F));
    }

    private void createParticles(TooltipIdTicket.Container container) {
        long tick = ClientProxy.getClientTick();
        Font font = Minecraft.getInstance().font;
        int stepY = 0;

        for (StoredLumenComponent.StoredLumen storedLumen : this.dataComponent.getLumenDisplay()) {
            Lumen lumen = storedLumen.lumen();
            float fillRate = (float) storedLumen.amount() / (float) storedLumen.maxAmount();
            float effectChance = (float) Math.pow(fillRate, 1.2F);

            for (int j = 0; j < 2 && rand.nextFloat() < effectChance; j++) {
                ColorWrapper color = ColorWrapper.WHITE;
                if (rand.nextBoolean() && rand.nextFloat() < effectChance) {
                    color = lumen.getColor(tick);
                }

                int pX = 3 + rand.nextInt(64);
                int pY = stepY + 6 + rand.nextInt(5);
                container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, pX, pY)
                        .renderOffset(container.createRenderOffset())
                        .color(FXColorFunction.constant(color))
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setScale(9 + rand.nextInt(3))
                        .setGravity(Vector3.y(-0.01 - rand.nextFloat() * 0.005))
                        .setMaxAge(50 + rand.nextInt(10));
            }

            if (rand.nextInt(4) == 0 && rand.nextFloat() < effectChance) {
                int pX = 2 + rand.nextInt(64 + 2);
                int pY = stepY + 6 + rand.nextInt(5);
                container.createParticle(EffectTemplatesAS.SCREEN_LUMEN_PARTICLE, pX, pY)
                        .setSprite(TexturesAS.ATLAS_LUMEN, RegistriesAS.REGISTRY_LUMEN.getKey(lumen))
                        .renderOffset(container.createRenderOffset())
                        .color(FXColorFunction.constant(lumen.getColor(tick)))
                        .setGravity(Vector3.y(-0.006 - rand.nextFloat() * 0.002))
                        .setMaxAge(50 + rand.nextInt(10));
            }

            ColorWrapper color = ColorWrapper.WHITE;
            if (rand.nextBoolean() && rand.nextFloat() < effectChance) {
                color = lumen.getColor(tick);
            }
            int pX = 3 + rand.nextInt(64);
            int pY = stepY + 6 + rand.nextInt(5);
            container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, pX, pY)
                    .renderOffset(container.createRenderOffset())
                    .color(FXColorFunction.constant(color))
                    .alpha(FXAlphaFunction.PYRAMID)
                    .setScale(14 + rand.nextInt(2))
                    .setMaxAge(80);

            stepY += EFFECT_ROW_HEIGHT;

            List<Component> displayTexts = this.dataComponent.getLumenDisplayTexts(LogicalSide.CLIENT, this.stack, lumen);
            stepY += displayTexts.size() * (font.lineHeight + 1);
        }
    }

    private TooltipIdTicket.Container resolveEffectContainer() {
        TooltipIdTicket ticket = new TooltipIdTicket(this.identifier.id());
        return ScreenEffectTicketManager.getInstance().refreshOrCreate(ticket);
    }
}
