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
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Either;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.DeferredTooltipUtil;
import hellfirepvp.astralsorcery.client.util.tooltip.TooltipUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeInfoStarElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeInfoStarElement extends AbstractWidget {

    private final Supplier<List<Either<FormattedText, TooltipComponent>>> hoverText;

    public TomeInfoStarElement(int x, int y, Supplier<List<Either<FormattedText, TooltipComponent>>> hoverText) {
        super(x, y, 16, 16, GameNarrator.NO_TITLE);
        this.hoverText = hoverText;
    }

    public static TomeInfoStarElement ofTooltipComponents(int x, int y, Supplier<List<? extends TooltipComponent>> text) {
        return new TomeInfoStarElement(x, y, () -> text.get().stream()
                .map(Either::<FormattedText, TooltipComponent>right)
                .toList());
    }

    public static TomeInfoStarElement ofChatComponents(int x, int y, Supplier<List<? extends FormattedText>> text) {
        return new TomeInfoStarElement(x, y, () -> text.get().stream()
                .map(Either::<FormattedText, TooltipComponent>left)
                .toList());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float tick = ClientProxy.getClientTick() + partialTick;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.getX(), this.getY(), 0);
        guiGraphics.pose().translate(this.getWidth() / 2F, this.getHeight() / 2F, 0);

        TexturesAS.SCREEN_TOME_INFO_STAR.bindTexture();
        RenderSystem.enableBlend();
        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX, GameRenderer::getPositionTexShader, buf -> {
            float deg = (tick * 2) % 360F;
            float wh = this.getWidth() - (this.getWidth() / 6F) * (Mth.sin((float) Math.toRadians(((tick) * 4) % 360F)) + 1F);
            drawInfoStarSingle(guiGraphics.pose(), buf, wh, Math.toRadians(deg));

            deg = ((tick + 22.5F) * 2) % 360F;
            wh = this.getWidth() - (this.getWidth() / 6F) * (Mth.sin((float) Math.toRadians(((tick + 45F) * 4) % 360F)) + 1F);
            drawInfoStarSingle(guiGraphics.pose(), buf, wh, Math.toRadians(deg));
        });
        guiGraphics.pose().popPose();

        if (this.isHovered()) {
            DeferredTooltipUtil.drawTooltip(guiGraphics, graphics -> {
                TooltipUtil.blueColor(() -> {
                    graphics.renderComponentTooltipFromElements(Minecraft.getInstance().font, this.hoverText.get(), this.getX() + 8, this.getY(), ItemStack.EMPTY);
                });
            });
        }
        RenderSystem.disableBlend();
    }

    private static void drawInfoStarSingle(PoseStack renderStack, VertexConsumer vb, float widthHeight, double deg) {
        Vector3 offset = new Vector3(-widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv01   = new Vector3(-widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv11   = new Vector3( widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv10   = new Vector3( widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);

        Matrix4f matr = renderStack.last().pose();
        vb.addVertex(matr, (float) uv01.getX(),   (float) uv01.getY(),   0).setUv(0, 1);
        vb.addVertex(matr, (float) uv11.getX(),   (float) uv11.getY(),   0).setUv(1, 1);
        vb.addVertex(matr, (float) uv10.getX(),   (float) uv10.getY(),   0).setUv(1, 0);
        vb.addVertex(matr, (float) offset.getX(), (float) offset.getY(), 0).setUv(0, 0);
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
