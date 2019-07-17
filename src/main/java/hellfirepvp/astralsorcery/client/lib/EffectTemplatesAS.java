/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.vfx.FXLightbeam;
import hellfirepvp.astralsorcery.client.effect.vfx.FXLightning;

import java.util.Collection;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectTemplatesAS
 * Created by HellFirePvP
 * Date: 07.07.2019 / 11:31
 */
public class EffectTemplatesAS {

    private EffectTemplatesAS() {}

    public static Collection<BatchRenderContext<?>> LIST_ALL_RENDER_CONTEXT = new LinkedList<>();

    public static BatchRenderContext<FXFacingParticle> GENERIC_PARTICLE;
    public static BatchRenderContext<FXFacingParticle> GENERIC_DEPTH_PARTICLE;
    public static BatchRenderContext<FXFacingParticle> GENERIC_GATEWAY_PARTICLE;

    public static BatchRenderContext<FXFacingParticle> CRYSTAL_BURST_1;
    public static BatchRenderContext<FXFacingParticle> CRYSTAL_BURST_2;
    public static BatchRenderContext<FXFacingParticle> CRYSTAL_BURST_3;

    public static BatchRenderContext<FXFacingParticle> COLLECTOR_BURST;
    public static BatchRenderContext<FXLightning> LIGHTNING;
    public static BatchRenderContext<FXLightbeam> LIGHTBEAM;

}
