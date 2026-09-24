/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXPersistenceFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.*;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import hellfirepvp.astralsorcery.client.screen.effect.ScreenEffectTemplate;
import hellfirepvp.astralsorcery.client.screen.effect.vfx.VSFXAtlasSpriteParticle;
import hellfirepvp.astralsorcery.client.screen.effect.vfx.VSFXLightBeam;
import hellfirepvp.astralsorcery.client.screen.effect.vfx.VSFXPlaneParticle;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EffectTemplatesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EffectTemplatesAS {

    public static Deque<EffectTemplate<?>> ALL_EFFECT_TEMPLATES = new ArrayDeque<>();

    public static EffectTemplate<VFXFacingParticle> GENERIC_PARTICLE;
    public static EffectTemplate<VFXFacingParticle> GENERIC_PARTICLE_DEPTH;
    public static EffectTemplate<VFXAtlasSpriteParticle> LUMEN_PARTICLE;
    public static EffectTemplate<VFXAtlasSpriteParticle> BLOCK_PARTICLE;
    public static VFXCube.IndividualTemplate<VFXCube> TRANSLUCENT_CUBE_SINGLE;
    public static VFXCube.BatchedTemplate<VFXCube> TRANSLUCENT_CUBE_WHITE;
    public static EffectTemplate<VFXImmediateTextureSprite> IMMEDIATE_TEXTURE_SPRITE;

    public static EffectTemplate<VFXLightBeam> LIGHT_BEAM;
    public static EffectTemplate<VFXLightBeam> LIGHT_BEAM_TRANSFER;
    public static EffectTemplate<VFXFocalLightBeam> FOCAL_LIGHT_BEAM;
    public static EffectTemplate<VFXFacingParticle> ATTUNEMENT_RELAY_FLARE;
    public static EffectTemplate<VFXLightning> LIGHTNING;
    public static EffectTemplate<VFXFacingParticle> ENTITY_FLARE;
    public static EffectTemplate<VFXColorSphere> COLOR_SPHERE;
    public static EffectTemplate<VFXFacingParticle> COLOR_SPHERE_PARTICLE;

    public static EffectTemplate<VFXImmediateFacingSprite> IMMEDIATE_FACING_SPRITE;

    public static ScreenEffectTemplate<VSFXPlaneParticle> SCREEN_PLANE_PARTICLE;
    public static ScreenEffectTemplate<VSFXAtlasSpriteParticle> SCREEN_LUMEN_PARTICLE;
    public static ScreenEffectTemplate<VSFXLightBeam> SCREEN_LIGHT_BEAM;

    public static void init() {
        GENERIC_PARTICLE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE, true, pos -> {
            return new VFXFacingParticle(pos, TexturesAS.PARTICLE_SMALL)
                    .setAlpha(0.85F)
                    .setScale(0.2F)
                    .alpha(FXAlphaFunction.PYRAMID);
        }));
        GENERIC_PARTICLE_DEPTH = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE_DEPTH, true, pos -> {
            return new VFXFacingParticle(pos, TexturesAS.PARTICLE_SMALL)
                    .setAlpha(0.85F)
                    .setScale(0.2F)
                    .alpha(FXAlphaFunction.PYRAMID);
        }));
        LUMEN_PARTICLE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_LUMEN_PARTICLE, true, pos -> {
            return new VFXAtlasSpriteParticle(pos, AtlasTexture.getLumenAtlas())
                    .setScale(0.1F)
                    .alpha(FXAlphaFunction.PYRAMID);
        }));
        BLOCK_PARTICLE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_BLOCK_PARTICLE, true, pos -> {
            return new VFXAtlasSpriteParticle(pos, AtlasTexture.getBlockAtlas())
                    .setScale(0.1F)
                    .alpha(FXAlphaFunction.PYRAMID);
        }));
        TRANSLUCENT_CUBE_SINGLE = register(new VFXCube.IndividualTemplate<>(RenderTypesAS.EFFECT_FX_TRANSLUCENT_CUBE, pos -> {
            return new VFXCube(pos)
                    .setRenderState(Blocks.STONE.defaultBlockState())
                    .alpha(FXAlphaFunction.PYRAMID);
        }));
        TRANSLUCENT_CUBE_WHITE = register(new VFXCube.BatchedTemplate<>(RenderTypesAS.EFFECT_FX_TRANSLUCENT_CUBE, pos -> {
            return new VFXCube(pos)
                    .setRenderState(Blocks.STONE.defaultBlockState());
        })).alpha(FXAlphaFunction.CONSTANT).color(FXColorFunction.WHITE).setAlpha(0.6F);
        IMMEDIATE_TEXTURE_SPRITE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE, true, VFXImmediateTextureSprite::new));

        LIGHT_BEAM = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_LIGHT_BEAM, true, pos -> {
            return new VFXLightBeam(pos, SpritesAS.SPRITE_LIGHT_BEAM)
                    .alpha(FXAlphaFunction.PYRAMID)
                    .setMaxAge(64);
        }));
        LIGHT_BEAM_TRANSFER = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_LIGHT_BEAM_TRANSFER, true, pos -> {
            return new VFXLightBeam(pos, SpritesAS.SPRITE_LIGHT_BEAM_TRANSFER)
                    .alpha(FXAlphaFunction.PYRAMID)
                    .setMaxAge(64);
        }));
        FOCAL_LIGHT_BEAM = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_LIGHT_BEAM, true, pos -> {
            return new VFXFocalLightBeam(pos, SpritesAS.SPRITE_LIGHT_BEAM)
                    .alpha(FXAlphaFunction.PYRAMID)
                    .persistence(FXPersistenceFunction.ALWAYS_PERSIST)
                    .setMaxAge(64);
        }));

        ATTUNEMENT_RELAY_FLARE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_ATTUNEMENT_RELAY_FLARE, true, pos -> {
            return new VFXFacingParticle(pos, SpritesAS.SPRITE_ATTUNEMENT_RELAY_FLARE)
                    .setMaxAge(48);
        }));
        LIGHTNING = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_LIGHTNING, true, VFXLightning::new));
        ENTITY_FLARE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_ENTITY_FLARE, true, pos -> {
            return new VFXFacingParticle(pos, SpritesAS.SPRITE_ENTITY_FLARE)
                    .setMaxAge(48);
        }));
        COLOR_SPHERE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_COLOR_SPHERE, true, VFXColorSphere::new));
        COLOR_SPHERE_PARTICLE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE, true, GENERIC_PARTICLE::createParticle));

        IMMEDIATE_FACING_SPRITE = register(new EffectTemplate<>(RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE, true, VFXImmediateFacingSprite::new));

        SCREEN_PLANE_PARTICLE = register(new ScreenEffectTemplate<>(RenderTypesAS.SCREEN_EFFECT_FX_PLANE_PARTICLE, true, (ticket, x, y) -> {
            return new VSFXPlaneParticle(ticket, x, y, TexturesAS.PARTICLE_SMALL)
                    .setScale(4F)
                    .alpha(FXAlphaFunction.fadeIn(10).andThen(FXAlphaFunction.FADE_OUT))
                    .setMaxAge(64);
        }));
        SCREEN_LUMEN_PARTICLE = register(new ScreenEffectTemplate<>(RenderTypesAS.SCREEN_EFFECT_FX_LUMEN_PARTICLE, true, (ticket, x, y) -> {
            return new VSFXAtlasSpriteParticle(ticket, x, y, AtlasTexture.getLumenAtlas())
                    .setScale(6F)
                    .alpha(FXAlphaFunction.PYRAMID)
                    .setMaxAge(64);
        }));
        SCREEN_LIGHT_BEAM = register(new ScreenEffectTemplate<>(RenderTypesAS.SCREEN_EFFECT_FX_LIGHT_BEAM, true, (ticket, x, y) -> {
            return new VSFXLightBeam(ticket, x, y, SpritesAS.SPRITE_LIGHT_BEAM)
                    .alpha(FXAlphaFunction.PYRAMID)
                    .setMaxAge(64);
        }));
    }

    private static <V extends EntityVisualFX, T extends EffectTemplate<V>> T register(T ctx) {
        ALL_EFFECT_TEMPLATES.add(ctx);
        return ctx;
    }
}
