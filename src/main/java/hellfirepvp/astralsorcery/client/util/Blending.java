/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.renderer.RenderStateShard;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: Blending
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum Blending {

    //ALPHA_REPLACE
    DEFAULT(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO),
    ALPHA(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    PREALPHA(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    MULTIPLY(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA),
    MULTIPLY_DARK(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ZERO),
    ADDITIVE(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE),
    ADDITIVEDARK(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR),
    OVERLAYDARK(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE),
    ADDITIVE_ALPHA(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE),
    ADDITIVE_ALPHA_FLAT(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE),
    INVERTEDADD(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);

    private final GlStateManager.SourceFactor colorSrcFactor, alphaSrcFactor;
    private final GlStateManager.DestFactor colorDstFactor, alphaDstFactor;

    Blending(GlStateManager.SourceFactor src, GlStateManager.DestFactor dst) {
        this(src, dst, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
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

    public RenderStateShard.TransparencyStateShard asState() {
        return new RenderStateShard.TransparencyStateShard(AstralSorcery.key("blending_" + this.name().toLowerCase(Locale.ROOT)).toString(), () -> {
            RenderSystem.enableBlend();
            this.apply();
        }, () -> {
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        });
    }
}
