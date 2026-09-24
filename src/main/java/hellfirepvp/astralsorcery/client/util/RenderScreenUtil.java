/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import hellfirepvp.astralsorcery.client.shader.DrawChainRenderType;
import hellfirepvp.astralsorcery.client.shader.WrappedBufferSource;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderScreenUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderScreenUtil {

    public static void renderTranslucentItem(GuiGraphics graphics, ItemStack stack, int x, int y, float partialTick, ColorWrapper color, float scale) {
        RenderTarget transparencyTarget = ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTarget().orElseThrow();
        WrappedBufferSource chainBuffers = new WrappedBufferSource(graphics.bufferSource(),
                renderType -> DrawChainRenderType.wrap("translucent_item_", renderType, transparencyTarget));
        GuiGraphics wrappedGraphics = new GuiGraphics(Minecraft.getInstance(), chainBuffers);
        wrappedGraphics.pose().mulPose(graphics.pose().last().pose());
        ShaderProgramsAS.TRANSPARENCY_COLOR.setColor(color);

        RenderUtil.withTarget(Minecraft.getInstance().getMainRenderTarget(), target -> {
            transparencyTarget.clear(Minecraft.ON_OSX);
            RenderUtil.safeCopyDepth(transparencyTarget, target);

            wrappedGraphics.pose().pushPose();
            wrappedGraphics.pose().translate(-scale * 8 + x + 8, -scale * 8 + y + 8, 0);
            wrappedGraphics.pose().scale(scale, scale, 1F);
            wrappedGraphics.renderItem(stack, 0, 0);
            wrappedGraphics.pose().popPose();

            chainBuffers.end();
            ShaderProgramsAS.TRANSPARENCY_COLOR.redirect(target, chain -> chain.process(partialTick));
            RenderUtil.safeCopyDepth(target, transparencyTarget);
        });
    }

}
