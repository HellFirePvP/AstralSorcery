/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.renderer.RenderState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Blending
 * Created by HellFirePvP
 * Date: 08.07.2019 / 20:49
 */
public enum Blending {

    DEFAULT(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    ALPHA(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.SRC_ALPHA),
    PREALPHA(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    MULTIPLY(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    ADDITIVE(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE),
    ADDITIVEDARK(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR),
    OVERLAYDARK(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE),
    ADDITIVE_ALPHA(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE),
    CONSTANT_ALPHA(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA),
    INVERTEDADD(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);

    private final GlStateManager.SourceFactor colorSrcFactor, alphaSrcFactor;
    private final GlStateManager.DestFactor colorDstFactor, alphaDstFactor;

    Blending(GlStateManager.SourceFactor src, GlStateManager.DestFactor dst) {
        this(src, dst, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    Blending(GlStateManager.SourceFactor src, GlStateManager.DestFactor dst, GlStateManager.SourceFactor srcAlpha, GlStateManager.DestFactor dstAlpha) {
        this.colorSrcFactor = src;
        this.colorDstFactor = dst;
        this.alphaSrcFactor = srcAlpha;
        this.alphaDstFactor = dstAlpha;
    }

    public void apply() {
        RenderSystem.blendFuncSeparate(this.colorSrcFactor, this.colorDstFactor, this.alphaSrcFactor, this.alphaDstFactor);
    }

    public RenderState.TransparencyState asState() {
        return new RenderState.TransparencyState(AstralSorcery.key("blending_" + this.name().toLowerCase()).toString(), () -> {
            RenderSystem.enableBlend();
            this.apply();
        }, () -> {
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        });
    }
}
