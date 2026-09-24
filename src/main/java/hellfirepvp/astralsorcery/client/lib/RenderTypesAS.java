/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.render.RenderTypeBuilder;
import hellfirepvp.astralsorcery.client.resource.*;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import static hellfirepvp.astralsorcery.client.lib.ShadersAS.POSITION_COLOR_TEX_ALPHA_BLOOM_SHADER;
import static hellfirepvp.astralsorcery.client.lib.ShadersAS.POSITION_COLOR_TEX_ALPHA_SHADER;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderTypesAS {

    //Effects/FX/VFX
    public static RenderType EFFECT_FX_GENERIC_PARTICLE;
    public static RenderType EFFECT_FX_GENERIC_PARTICLE_DEPTH;
    public static RenderType EFFECT_FX_LUMEN_PARTICLE;
    public static RenderType EFFECT_FX_BLOCK_PARTICLE;
    public static RenderType EFFECT_FX_TRANSLUCENT_CUBE;
    public static RenderType EFFECT_FX_LIGHT_BEAM;
    public static RenderType EFFECT_FX_LIGHT_BEAM_TRANSFER;
    public static RenderType EFFECT_FX_LIGHTNING;
    public static RenderType EFFECT_FX_COLOR_SPHERE;
    public static RenderType EFFECT_FX_ATTUNEMENT_RELAY_FLARE;
    public static RenderType EFFECT_FX_ENTITY_FLARE;

    public static RenderType SCREEN_EFFECT_FX_PLANE_PARTICLE;
    public static RenderType SCREEN_EFFECT_FX_LUMEN_PARTICLE;
    public static RenderType SCREEN_EFFECT_FX_LIGHT_BEAM;

    public static RenderType SCREEN_EFFECT_PERK_ACTIVATABLE;
    public static RenderType SCREEN_EFFECT_PERK_ACTIVE;
    public static RenderType SCREEN_EFFECT_PERK_INACTIVE;
    public static RenderType SCREEN_EFFECT_PERK_HALO_ACTIVATABLE;
    public static RenderType SCREEN_EFFECT_PERK_HALO_ACTIVE;
    public static RenderType SCREEN_EFFECT_PERK_HALO_INACTIVE;
    public static RenderType SCREEN_EFFECT_PERK_SEARCH;
    public static RenderType SCREEN_EFFECT_PERK_SEAL;
    public static RenderType SCREEN_EFFECT_PERK_SEAL_BREAK;
    public static RenderType SCREEN_EFFECT_PERK_UNLOCK;

    //TESR Models
    public static RenderType MODEL_ATTUNEMENT_ALTAR;
    public static RenderType MODEL_LENS_SOLID;
    public static RenderType MODEL_LENS_GLASS;
    public static RenderType TER_CHALICE_LIQUID;

    //Entity stuff
    public static RenderType ENTITY_GRAPPLING_HOOK;
    public static RenderType ENTITY_GRAPPLING_HOOK_LINE;
    public static RenderType ENTITY_FLUID_INPUT_FLUID;

    //Misc Effects
    public static RenderType GATEWAY_UI_STAR;
    public static RenderType CONSTELLATION_WORLD_STAR;
    public static RenderType CONSTELLATION_WORLD_CONNECTION;

    public static void init() {
        initEffectTypes();
        initScreenEffectTypes();
        initModelTypes();
        initEntityTypes();
        initMiscTypes();
    }

    private static void initEffectTypes() {
        EFFECT_FX_GENERIC_PARTICLE = createType("effect_fx_generic_particle", DefaultVertexFormat.PARTICLE, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.PARTICLE_SMALL)
                        .blend(Blending.ALPHA)
                        .disableDepthMask()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_GENERIC_PARTICLE_DEPTH = createType("effect_fx_generic_particle_depth", DefaultVertexFormat.PARTICLE, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.PARTICLE_SMALL)
                        .blend(Blending.ALPHA)
                        .disableDepthMask()
                        .disableDepth()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_LUMEN_PARTICLE = createType("effect_fx_lumen_particle", DefaultVertexFormat.PARTICLE, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(AtlasTexture.getLumenAtlas())
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_BLOCK_PARTICLE = createType("effect_fx_block_particle", DefaultVertexFormat.PARTICLE, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(AtlasTexture.getBlockAtlas())
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_TRANSLUCENT_CUBE = createType("effect_fx_translucent_cube", DefaultVertexFormat.BLOCK, true,
                RenderTypeBuilder.builder()
                        .build());
        EFFECT_FX_LIGHT_BEAM = createType("effect_fx_light_beam", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.LIGHT_BEAM)
                        .blend(Blending.ADDITIVE_ALPHA)
                        .disableDepthMask()
                        .disableCull()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_LIGHT_BEAM_TRANSFER = createType("effect_fx_light_beam_transfer", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.LIGHT_BEAM_TRANSFER)
                        .blend(Blending.ADDITIVE_ALPHA)
                        .disableDepthMask()
                        .disableCull()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_LIGHTNING = createType("effect_fx_lightning", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_BLOOM_SHADER)
                        .texture(TexturesAS.LIGHTNING_ELEMENT)
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .disableCull()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_COLOR_SPHERE = createType("effect_fx_color_sphere", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, Short.MAX_VALUE + 1, false, true,
                RenderTypeBuilder.builder()
                        .shader(RenderStateShard.POSITION_COLOR_SHADER)
                        .blend(Blending.DEFAULT)
                        .disableTexture()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_ATTUNEMENT_RELAY_FLARE = createType("effect_fx_attunement_relay_flare", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.ATTUNEMENT_RELAY_FLARE)
                        .blend(Blending.ADDITIVE_ALPHA)
                        .disableDepthMask()
                        .disableCull()
                        .particleShaderTarget()
                        .build());
        EFFECT_FX_ENTITY_FLARE = createType("effect_fx_entity_flare", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.ENTITY_FLARE)
                        .blend(Blending.ADDITIVE_ALPHA)
                        .disableDepthMask()
                        .disableCull()
                        .particleShaderTarget()
                        .build());
    }

    private static void initScreenEffectTypes() {
        SCREEN_EFFECT_FX_PLANE_PARTICLE = createType("screen_effect_fx_plane_particle", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.PARTICLE_SMALL)
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .build());
        SCREEN_EFFECT_FX_LUMEN_PARTICLE = createType("screen_effect_fx_lumen_particle", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .atlasTexture(TexturesAS.ATLAS_LUMEN)
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .build());
        SCREEN_EFFECT_FX_LIGHT_BEAM = createType("screen_effect_fx_light_beam", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.LIGHT_BEAM)
                        .blend(Blending.ADDITIVE_ALPHA)
                        .disableDepthMask()
                        .disableCull()
                        .build());

        SCREEN_EFFECT_PERK_ACTIVATABLE = createPerkTreeTextureType("screen_effect_perk_activatable", TexturesAS.SCREEN_EFFECT_PERK_ACTIVATABLE);
        SCREEN_EFFECT_PERK_ACTIVE = createPerkTreeTextureType("screen_effect_perk_active", TexturesAS.SCREEN_EFFECT_PERK_ACTIVE);
        SCREEN_EFFECT_PERK_INACTIVE = createPerkTreeTextureType("screen_effect_perk_inactive", TexturesAS.SCREEN_EFFECT_PERK_INACTIVE);
        SCREEN_EFFECT_PERK_HALO_ACTIVATABLE = createPerkTreeTextureType("screen_effect_perk_halo_activatable", TexturesAS.SCREEN_EFFECT_PERK_HALO_ACTIVATABLE);
        SCREEN_EFFECT_PERK_HALO_ACTIVE = createPerkTreeTextureType("screen_effect_perk_halo_active", TexturesAS.SCREEN_EFFECT_PERK_HALO_ACTIVE);
        SCREEN_EFFECT_PERK_HALO_INACTIVE = createPerkTreeTextureType("screen_effect_perk_halo_inactive", TexturesAS.SCREEN_EFFECT_PERK_HALO_INACTIVE);
        SCREEN_EFFECT_PERK_SEARCH = createPerkTreeTextureType("screen_effect_perk_search", TexturesAS.SCREEN_EFFECT_PERK_SEARCH);
        SCREEN_EFFECT_PERK_SEAL = createPerkTreeTextureType("screen_effect_perk_seal", TexturesAS.SCREEN_EFFECT_PERK_SEAL);
        SCREEN_EFFECT_PERK_SEAL_BREAK = createPerkTreeTextureType("screen_effect_perk_seal_break", TexturesAS.SCREEN_EFFECT_PERK_SEAL_BREAK);
        SCREEN_EFFECT_PERK_UNLOCK = createPerkTreeTextureType("screen_effect_perk_unlock", TexturesAS.SCREEN_EFFECT_PERK_UNLOCK);
    }

    private static void initModelTypes() {
        MODEL_ATTUNEMENT_ALTAR = createType("model_attunement_altar", DefaultVertexFormat.NEW_ENTITY,
                RenderTypeBuilder.builder()
                        .shader(RenderStateShard.RENDERTYPE_ENTITY_SOLID_SHADER)
                        .texture(AssetLibrary.loadTexture(AssetLocation.BLOCKS, "attunement_altar", "entity_attunement_altar"))
                        .enableLighting()
                        .enableOverlay()
                        .build());
        MODEL_LENS_SOLID = createType("model_lens_solid", DefaultVertexFormat.NEW_ENTITY,
                RenderTypeBuilder.builder()
                        .shader(RenderStateShard.RENDERTYPE_ENTITY_CUTOUT_SHADER)
                        .texture(AssetLibrary.loadTexture(AssetLocation.BLOCKS, "lens", "entity_lens"))
                        .enableLighting()
                        .enableOverlay()
                        .build());
        MODEL_LENS_GLASS = createType("model_lens_glass", DefaultVertexFormat.NEW_ENTITY,
                RenderTypeBuilder.builder()
                        .shader(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .texture(AssetLibrary.loadTexture(AssetLocation.BLOCKS, "lens", "entity_lens_glass"))
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .enableLighting()
                        .enableOverlay()
                        .build());
        TER_CHALICE_LIQUID = createType("ter_chalice_liquid", DefaultVertexFormat.NEW_ENTITY,
                RenderTypeBuilder.builder()
                        .shader(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .blockAtlasTexture()
                        .blend(Blending.DEFAULT)
                        .enableLighting()
                        .enableOverlay()
                        .build());
    }

    private static void initEntityTypes() {
        ENTITY_GRAPPLING_HOOK = createType("entity_grappling_hook_effect", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.GRAPPLING_HOOK)
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .build());
        ENTITY_GRAPPLING_HOOK_LINE = createType("entity_grappling_hook_line", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.PARTICLE_LARGE)
                        .blend(Blending.ADDITIVE_ALPHA)
                        .disableCull()
                        .build());
        ENTITY_FLUID_INPUT_FLUID = createType("entity_fluid_input_fluid", DefaultVertexFormat.NEW_ENTITY, true,
                RenderTypeBuilder.builder()
                        .shader(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .blockAtlasTexture()
                        .blend(Blending.DEFAULT)
                        .enableLighting()
                        .enableOverlay()
                        .build());
    }

    private static void initMiscTypes() {
        GATEWAY_UI_STAR = createType("gateway_ui_star", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.STAR_1)
                        .blend(Blending.ALPHA)
                        .disableCull()
                        .disableDepthMask()
                        .particleShaderTarget()
                        .build());
        CONSTELLATION_WORLD_STAR = createType("constellation_world_star", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.STAR_1)
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .build());
        CONSTELLATION_WORLD_CONNECTION = createType("constellation_world_connection", DefaultVertexFormat.POSITION_TEX_COLOR, true,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(TexturesAS.STAR_LINE)
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .build());
    }

    private static RenderType createPerkTreeTextureType(String name, AbstractRenderTexture texture) {
        return createType(name, DefaultVertexFormat.POSITION_TEX_COLOR, false,
                RenderTypeBuilder.builder()
                        .shader(POSITION_COLOR_TEX_ALPHA_SHADER)
                        .texture(texture)
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .build());
    }

    private static RenderType createType(String name, VertexFormat vertexFormat, RenderType.CompositeState state) {
        return createType(name, vertexFormat, VertexFormat.Mode.QUADS, Short.MAX_VALUE + 1, state);
    }

    private static RenderType createType(String name, VertexFormat vertexFormat, boolean sortVertices, RenderType.CompositeState state) {
        return createType(name, vertexFormat, VertexFormat.Mode.QUADS, Short.MAX_VALUE + 1, false, sortVertices, state);
    }

    private static RenderType createType(String name, VertexFormat vertexFormat, VertexFormat.Mode glDrawMode, int bufferSize, RenderType.CompositeState state) {
        return createType(name, vertexFormat, glDrawMode, bufferSize, false, false, state);
    }

    private static RenderType createType(String name, VertexFormat vertexFormat, VertexFormat.Mode glDrawMode, int bufferSize, boolean usesDelegateDrawing, boolean sortVertices, RenderType.CompositeState state) {
        return RenderType.create(AstralSorcery.key(name).toString(), vertexFormat, glDrawMode, bufferSize, usesDelegateDrawing, sortVertices, state);
    }

}
