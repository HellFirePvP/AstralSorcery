/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.context.RenderContextCrystalBurst;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import static hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS.*;
import static hellfirepvp.astralsorcery.client.lib.SpritesAS.*;
import static hellfirepvp.astralsorcery.client.lib.TexturesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEffectTemplates
 * Created by HellFirePvP
 * Date: 07.07.2019 / 11:31
 */
public class RegistryEffectTemplates {

    private RegistryEffectTemplates() {}

    public static void registerTemplates() {
        GENERIC_PARTICLE = new BatchRenderContext<>(TEX_STATIC_FLARE, 0,
                (ctx) -> {
                    GlStateManager.disableAlphaTest();
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.disableCull();
                    GlStateManager.depthMask(false);
                    ctx.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                },
                () -> {
                    GlStateManager.depthMask(true);
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlphaTest();
                },
                (ctx, pos) -> new FXFacingParticle(pos).setScaleMultiplier(0.2F));

        CRYSTAL_BURST_1 = new RenderContextCrystalBurst(SPR_CRYSTAL_EFFECT_1);
        CRYSTAL_BURST_2 = new RenderContextCrystalBurst(SPR_CRYSTAL_EFFECT_2);
        CRYSTAL_BURST_3 = new RenderContextCrystalBurst(SPR_CRYSTAL_EFFECT_3);
    }

}
