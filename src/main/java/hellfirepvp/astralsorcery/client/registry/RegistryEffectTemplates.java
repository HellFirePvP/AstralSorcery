/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.*;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.context.base.RenderContextColorSphere;
import hellfirepvp.astralsorcery.common.util.ObjectReference;

import java.util.LinkedList;
import java.util.List;

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

        TEXTURE_SPRITE = register(new RenderContextSpritePlaneDynamic());
        FACING_SPRITE = register(new RenderContextFacingSprite());

        CUBE_OPAQUE_ATLAS = register(new RenderContextOpaqueCube());
        CUBE_TRANSLUCENT_ATLAS = register(new RenderContextTranslucentCube());
        CUBE_TRANSLUCENT_ATLAS_IGNORE_DEPTH = register(new RenderContextTranslucentDepthCube());

        BLOCK_TRANSLUCENT = register(new RenderContextTranslucentBlock());
        BLOCK_TRANSLUCENT_IGNORE_DEPTH = register(new RenderContextTranslucentDepthBlock());

        COLOR_SPHERE = register(new RenderContextColorSphere());
        GENERIC_GATEWAY_PARTICLE = register(new RenderContextGenericParticle());

        setupRenderOrder();
    }

    // Hint: Textures after generalGrp, before cubes
    //       cubes after textures, before gateway
    private static void setupRenderOrder() {
        GENERIC_PARTICLE.setAfter(GENERIC_DEPTH_PARTICLE);
        LIGHTNING.setAfter(GENERIC_PARTICLE);

        //L0
        List<BatchRenderContext<?>> generalGrp = new LinkedList<>();
        generalGrp.add(TEXTURE_SPRITE);
        generalGrp.add(CRYSTAL_BURST_1);
        generalGrp.add(CRYSTAL_BURST_2);
        generalGrp.add(CRYSTAL_BURST_3);
        generalGrp.add(COLLECTOR_BURST);
        generalGrp.add(LIGHTBEAM);
        generalGrp.add(FACING_SPRITE);

        generalGrp.forEach(c -> c.setAfter(LIGHTNING));

        //L1

        //L2
        CUBE_OPAQUE_ATLAS.setAfter(generalGrp);
        CUBE_TRANSLUCENT_ATLAS.setAfter(CUBE_OPAQUE_ATLAS);
        CUBE_TRANSLUCENT_ATLAS_IGNORE_DEPTH.setAfter(CUBE_TRANSLUCENT_ATLAS);

        BLOCK_TRANSLUCENT.setAfter(CUBE_TRANSLUCENT_ATLAS_IGNORE_DEPTH);
        BLOCK_TRANSLUCENT_IGNORE_DEPTH.setAfter(BLOCK_TRANSLUCENT);

        COLOR_SPHERE.setAfter(BLOCK_TRANSLUCENT_IGNORE_DEPTH);
        GENERIC_GATEWAY_PARTICLE.setAfter(COLOR_SPHERE);
    }

    private static <V extends EntityVisualFX, T extends BatchRenderContext<V>> T register(T ctx) {
        LIST_ALL_RENDER_CONTEXT.add(ctx);
        return ctx;
    }

}
