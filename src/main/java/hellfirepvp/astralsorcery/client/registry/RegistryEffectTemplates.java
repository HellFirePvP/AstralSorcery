/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.context.*;
import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import hellfirepvp.astralsorcery.common.util.ObjectReference;

import static hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS.*;
import static hellfirepvp.astralsorcery.client.lib.SpritesAS.*;

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
        GENERIC_DEPTH_PARTICLE = register(new RenderContextGenericDepthParticle());
        GENERIC_PARTICLE       = register(new RenderContextGenericParticle());
        LIGHTNING              = register(new RenderContextLightning());

        CRYSTAL_BURST_1 = register(new RenderContextBurst(SPR_CRYSTAL_EFFECT_1));
        CRYSTAL_BURST_2 = register(new RenderContextBurst(SPR_CRYSTAL_EFFECT_2));
        CRYSTAL_BURST_3 = register(new RenderContextBurst(SPR_CRYSTAL_EFFECT_3));
        COLLECTOR_BURST = register(new RenderContextBurst(SPR_COLLECTOR_EFFECT));
        LIGHTBEAM       = register(new RenderContextLightbeam(new ObjectReference<>()));

        GENERIC_GATEWAY_PARTICLE = register(new RenderContextGenericParticle());

        setupRenderOrder();
    }

    private static void setupRenderOrder() {
        GENERIC_PARTICLE.setAfter(GENERIC_DEPTH_PARTICLE);
        LIGHTNING.setAfter(GENERIC_PARTICLE);

        CRYSTAL_BURST_1.setAfter(LIGHTNING);
        CRYSTAL_BURST_2.setAfter(LIGHTNING);
        CRYSTAL_BURST_3.setAfter(LIGHTNING);
        COLLECTOR_BURST.setAfter(LIGHTNING);
        LIGHTBEAM      .setAfter(LIGHTNING);

        GENERIC_GATEWAY_PARTICLE.setAfter(COLLECTOR_BURST);
    }

    private static <V extends EntityComplexFX, T extends BatchRenderContext<V>> T register(T ctx) {
        LIST_ALL_RENDER_CONTEXT.add(ctx);
        return ctx;
    }

}
