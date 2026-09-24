/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.DeferredTooltipUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.TooltipUtil;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeToggleButtonElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeToggleButtonElement extends AbstractWidget {

    private final AbstractRenderTexture texture;
    private final UVFrame disabled;
    private final UVFrame enabled;
    private final Consumer<Boolean> isSetChangeConsumer;

    private boolean isSet = false;

    public TomeToggleButtonElement(int x, int y, Consumer<Boolean> isSetChangeConsumer) {
        this(x, y, 16, 16,
                TexturesAS.SCREEN_TOME_SLICE_ICONS,
                new UVFrame(0, 0, 0.5F, 1F),
                new UVFrame(0.5F, 0, 0.5F, 1F),
                isSetChangeConsumer);
    }

    public TomeToggleButtonElement(int x, int y, int width, int height,
                                   AbstractRenderTexture texture, UVFrame disabled, UVFrame enabled,
                                   Consumer<Boolean> isSetChangeConsumer) {
        super(x - width / 2, y - height / 2, width, height, GameNarrator.NO_TITLE);
        this.texture = texture;
        this.disabled = disabled;
        this.enabled = enabled;
        this.isSetChangeConsumer = isSetChangeConsumer;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        UVFrame uv = this.isSet() ? this.enabled : this.disabled;

        poseStack.pushPose();
        poseStack.translate(this.getX(), this.getY(), 0);

        RenderSystem.enableBlend();
        PoseStack.Pose pose = poseStack.last();
        this.texture.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, GameRenderer::getPositionTexShader, buf -> {
            buf.addVertex(pose, 0,     0,      0).setUv(uv.u(),               uv.v());
            buf.addVertex(pose, 0,     height, 0).setUv(uv.u(),               uv.v() + uv.vHeight());
            buf.addVertex(pose, width, height, 0).setUv(uv.u() + uv.uWidth(), uv.v() + uv.vHeight());
            buf.addVertex(pose, width, 0,      0).setUv(uv.u() + uv.uWidth(), uv.v());
        });
        poseStack.popPose();

        if (this.isHovered()) {
            DeferredTooltipUtil.drawTooltip(guiGraphics, graphics -> {
                TooltipUtil.blueColor(() -> {
                    Component cmp = Component.translatable("tome.research.info.structure.slice_view_toggle").withColor(ColorsAS.TOME_TEXT_COLOR.getColor());
                    graphics.renderTooltip(Minecraft.getInstance().font, cmp, mouseX, mouseY);
                    graphics.bufferSource().endBatch();
                });
            });
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        this.toggle();
    }

    private void toggle() {
        this.isSet = !this.isSet;
        this.isSetChangeConsumer.accept(this.isSet());
    }

    public boolean isSet() {
        return this.isSet;
    }

    public void setIsSet(boolean set) {
        this.isSet = set;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        } else if (CommonInputs.selected(keyCode)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.toggle();
            return true;
        } else {
            return false;
        }
    }
}
