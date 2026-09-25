/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.helper.ArtifactTooltipHelper;
import hellfirepvp.astralsorcery.client.helper.StoredLumenTooltipHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTicketManager;
import hellfirepvp.astralsorcery.client.screen.effect.ticket.TooltipIdTicket;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.IntRectangle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tooltip.ArtifactDecoratedTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.GlStateBackup;
import org.joml.Matrix4f;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactDecoratedClientComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactDecoratedClientComponent implements ClientTooltipComponent {

    private final RandomSource rand = RandomSource.create();
    private final IdentifierComponent identifier;
    private final ClientTooltipComponent text;

    public ArtifactDecoratedClientComponent(IdentifierComponent identifier, FormattedText decoratedText) {
        this.identifier = identifier;
        FormattedCharSequence txtSequence = decoratedText instanceof Component cmp ? cmp.getVisualOrderText() : Language.getInstance().getVisualOrder(decoratedText);
        this.text = ClientTooltipComponent.create(txtSequence);
    }

    public static ArtifactDecoratedClientComponent create(ArtifactDecoratedTooltip tooltip) {
        return new ArtifactDecoratedClientComponent(tooltip.identifier(), tooltip.decorated());
    }

    @Override
    public int getHeight() {
        return this.text.getHeight();
    }

    @Override
    public int getWidth(Font font) {
        return this.text.getWidth(font);
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {}

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        TooltipIdTicket.Container container = this.resolveEffectContainer();
        container.setTooltipPosition(x, y);

        if (container.canAddEffects()) {
            this.createParticles(container);
        }
        container.renderAll(guiGraphics, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        RenderSystem.enableDepthTest();

        this.text.renderText(font, x, y, guiGraphics.pose().last().pose(), guiGraphics.bufferSource());
        this.text.renderImage(font, x, y, guiGraphics);
    }

    private void createParticles(TooltipIdTicket.Container container) {
        int width = this.getWidth(Minecraft.getInstance().font);
        float effectChance = width / 110F;

        for (int i = 0; i < MiscUtil.roundChanced(effectChance, this.rand); i++) {
            float scale = 8 + this.rand.nextInt(15);
            int age = 20 + this.rand.nextInt(10);

            int pX = this.rand.nextInt(this.getWidth(Minecraft.getInstance().font));
            int pY = this.rand.nextInt(this.getHeight());

            container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, pX, pY)
                    .renderOffset(container.createRenderOffset())
                    .color(FXColorFunction.constant(ColorsAS.RARITY_ARTIFACT))
                    .alpha(FXAlphaFunction.fadeIn(5).andThen(FXAlphaFunction.FADE_OUT))
                    .setScale(scale)
                    .setMaxAge(age);

            container.createParticle(EffectTemplatesAS.SCREEN_PLANE_PARTICLE, pX, pY)
                    .renderOffset(container.createRenderOffset())
                    .alpha(FXAlphaFunction.fadeIn(5).andThen(FXAlphaFunction.FADE_OUT))
                    .setScale(scale * 0.2F)
                    .setMaxAge(Mth.ceil(age * 0.7F));
        }
    }

    private TooltipIdTicket.Container resolveEffectContainer() {
        TooltipIdTicket ticket = new TooltipIdTicket(this.identifier.id());
        return ScreenEffectTicketManager.getInstance().refreshOrCreate(ticket);
    }
}
