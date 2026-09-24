/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeSliceArrowElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeSliceArrowElement extends AbstractWidget {

    private final boolean pointsUp;
    private final AbstractRenderTexture texture;
    private final Runnable onClick;

    public TomeSliceArrowElement(int x, int y, boolean pointsUp, Runnable onClick) {
        this(x, y, pointsUp, TexturesAS.SCREEN_TOME_SLICE_ARROWS, onClick);
    }

    public TomeSliceArrowElement(int x, int y, boolean pointsUp, AbstractRenderTexture texture, Runnable onClick) {
        super(x - 4, y - 7, 8, 14, GameNarrator.NO_TITLE);
        this.pointsUp = pointsUp;
        this.texture = texture;
        this.onClick = onClick;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();
        float u = this.pointsUp ? 0 : 0.5F;
        float v = this.isHovered() ? 0.5F : 0;

        poseStack.pushPose();
        poseStack.translate(this.getX() + this.width / 2F, this.getY() + this.height / 2F, 0);
        if (this.isHovered()) {
            poseStack.scale(1.1F, 1.1F, 1.0F);
        } else {
            double tick = ClientProxy.getClientTick() + partialTick;
            float scale = 1.0F + (float) (Math.sin(tick * 0.25F) * 0.05F);
            poseStack.scale(scale, scale, 1.0F);
        }
        poseStack.translate(-(this.width / 2F), -(this.height / 2F), 0);

        PoseStack.Pose pose = poseStack.last();
        this.texture.bindTexture();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, GameRenderer::getPositionTexShader, buf -> {
            buf.addVertex(pose, 0,     0,      0).setUv(u,        v);
            buf.addVertex(pose, 0,     height, 0).setUv(u,        v + 0.5F);
            buf.addVertex(pose, width, height, 0).setUv(u + 0.5F, v + 0.5F);
            buf.addVertex(pose, width, 0,      0).setUv(u + 0.5F, v);
        });
        poseStack.popPose();
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        this.onClick.run();
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
            this.onClick.run();
            return true;
        } else {
            return false;
        }
    }
}
