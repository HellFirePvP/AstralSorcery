/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import hellfirepvp.astralsorcery.client.effect.context.base.RenderContextCube;
import hellfirepvp.astralsorcery.client.effect.vfx.FXCube;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.common.util.object.ObjectReference;

import java.util.LinkedList;
import java.util.List;

import static hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS.*;
import static hellfirepvp.astralsorcery.client.lib.SpritesAS.*;
import static hellfirepvp.astralsorcery.client.lib.TexturesAS.TEX_MODEL_CRYSTAL_WHITE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEffectTemplates
 * Created by HellFirePvP
 * Date: 07.07.2019 / 11:31
 */
public class RegistryEffectTemplates {

    private RegistryEffectTemplates() {}

    public static void init() {
        GENERIC_DEPTH_PARTICLE = register(new RenderContextGenericDepthParticle());
        GENERIC_PARTICLE       = register(new RenderContextGenericParticle());
        GENERIC_ATLAS_PARTICLE = register(new RenderContextAtlasParticle());
        LIGHTNING              = register(new RenderContextLightning());

        CRYSTAL_BURST_1         = register(new RenderContextBurst(SPR_CRYSTAL_EFFECT_1));
        CRYSTAL_BURST_2         = register(new RenderContextBurst(SPR_CRYSTAL_EFFECT_2));
        CRYSTAL_BURST_3         = register(new RenderContextBurst(SPR_CRYSTAL_EFFECT_3));
        CRYSTAL                 = register(new RenderContextCrystal(TEX_MODEL_CRYSTAL_WHITE));
        COLLECTOR_BURST         = register(new RenderContextBurst(SPR_COLLECTOR_EFFECT));
        GEM_CRYSTAL_BURST       = register(new RenderContextBurst(SPR_GEM_CRYSTAL_BURST));
        GEM_CRYSTAL_BURST_SKY   = register(new RenderContextBurst(SPR_GEM_CRYSTAL_BURST_SKY));
        GEM_CRYSTAL_BURST_DAY   = register(new RenderContextBurst(SPR_GEM_CRYSTAL_BURST_DAY));
        GEM_CRYSTAL_BURST_NIGHT = register(new RenderContextBurst(SPR_GEM_CRYSTAL_BURST_NIGHT));
        LIGHTBEAM               = register(new RenderContextLightbeam(new ObjectReference<>()));

        TEXTURE_SPRITE = register(new RenderContextSpritePlaneDynamic());
        FACING_SPRITE = register(new RenderContextFacingSprite());

        CUBE_OPAQUE_ATLAS = register(new RenderContextCube(RenderTypesAS.EFFECT_FX_CUBE_OPAQUE_ATLAS, (ctx, pos) -> new FXCube(pos)));
        CUBE_TRANSLUCENT_ATLAS = register(new RenderContextTranslucentCube(Blending.ADDITIVEDARK));
        CUBE_TRANSLUCENT_ATLAS_IGNORE_DEPTH = register(new RenderContextTranslucentDepthCube(Blending.ADDITIVEDARK));
        CUBE_AREA_OF_EFFECT = register(new RenderContextTranslucentCube(Blending.DEFAULT, TexturesAS.TEX_AREA_OF_EFFECT_CUBE));

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
        GENERIC_ATLAS_PARTICLE.setAfter(GENERIC_PARTICLE);
        LIGHTNING.setAfter(GENERIC_ATLAS_PARTICLE);

        //L0
        List<BatchRenderContext<?>> generalGrp = new LinkedList<>();
        generalGrp.add(TEXTURE_SPRITE);
        generalGrp.add(CRYSTAL_BURST_1);
        generalGrp.add(CRYSTAL_BURST_2);
        generalGrp.add(CRYSTAL_BURST_3);
        generalGrp.add(GEM_CRYSTAL_BURST);
        generalGrp.add(GEM_CRYSTAL_BURST_SKY);
        generalGrp.add(GEM_CRYSTAL_BURST_DAY);
        generalGrp.add(GEM_CRYSTAL_BURST_NIGHT);
        generalGrp.add(COLLECTOR_BURST);
        generalGrp.add(CRYSTAL);
        generalGrp.add(LIGHTBEAM);
        generalGrp.add(FACING_SPRITE);

        generalGrp.forEach(c -> c.setAfter(LIGHTNING));

        //L1

        //L2
        CUBE_OPAQUE_ATLAS.setAfter(generalGrp);
        CUBE_AREA_OF_EFFECT.setAfter(CUBE_OPAQUE_ATLAS);
        CUBE_TRANSLUCENT_ATLAS.setAfter(CUBE_AREA_OF_EFFECT);
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
