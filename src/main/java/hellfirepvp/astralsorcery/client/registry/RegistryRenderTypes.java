package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.render.RenderStateBuilder;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import static hellfirepvp.astralsorcery.client.lib.RenderTypesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRenderTypes
 * Created by HellFirePvP
 * Date: 05.06.2020 / 15:59
 */
public class RegistryRenderTypes {

    public static void init() {
        initEffectTypes();
        initEffects();
        initGuiTypes();
        initTERTypes();
        initModels();
    }

    private static void initEffectTypes() {
        EFFECT_FX_GENERIC_PARTICLE = createType("effect_fx_generic_particle", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_PARTICLE_SMALL)
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_GENERIC_PARTICLE_DEPTH = createType("effect_fx_generic_particle_depth", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_PARTICLE_SMALL)
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .disableDepthMask()
                        .disableDepth()
                        .build());
        EFFECT_FX_GENERIC_PARTICLE_ATLAS = createType("effect_fx_generic_particle_atlas", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_LIGHTNING = createType("effect_fx_lightning", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_LIGHTNING_PART)
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_LIGHTBEAM = createType("effect_fx_lightbeam", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_LIGHTBEAM)
                        .blend(Blending.ADDITIVE_ALPHA)
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_CRYSTAL = createType("effect_fx_crystal", POSITION_COLOR_TEX_NORMAL, GL11.GL_TRIANGLES, 32768,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_MODEL_CRYSTAL_WHITE)
                        .blend(Blending.DEFAULT)
                        .defaultAlpha()
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_BURST = createType("effect_fx_burst", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_DYNAMIC_TEXTURE_SPRITE = createType("effect_fx_dynamic_texture_sprite", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.DEFAULT)
                        .alpha(0.0001F)
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_TEXTURE_SPRITE = createType("effect_fx_texture_sprite", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.DEFAULT)
                        .alpha(0.0001F)
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_CUBE_OPAQUE_ATLAS = createType("effect_fx_cube_opaque_atlas", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.DEFAULT)
                        .defaultAlpha()
                        .disableCull()
                        .enableLighting()
                        .build());
        EFFECT_FX_BLOCK_TRANSLUCENT = createType("effect_fx_block_translucent", DefaultVertexFormats.BLOCK,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.ADDITIVEDARK)
                        .defaultAlpha()
                        .disableCull()
                        .build());
        EFFECT_FX_BLOCK_TRANSLUCENT_DEPTH = createType("effect_fx_block_translucent_depth", DefaultVertexFormats.BLOCK,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.ADDITIVEDARK)
                        .defaultAlpha()
                        .disableCull()
                        .disableDepth()
                        .build());
        EFFECT_FX_CUBE_TRANSLUCENT_ATLAS = createType("effect_fx_cube_translucent_atlas", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.ADDITIVEDARK)
                        .defaultAlpha()
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_CUBE_TRANSLUCENT_ATLAS_DEPTH = createType("effect_fx_cube_translucent_atlas_depth", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.ADDITIVEDARK)
                        .defaultAlpha()
                        .disableCull()
                        .disableDepthMask()
                        .disableDepth()
                        .build());
        EFFECT_FX_CUBE_AREA_OF_EFFECT = createType("effect_fx_cube_area_of_effect", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_AREA_OF_EFFECT_CUBE)
                        .blend(Blending.DEFAULT)
                        .defaultAlpha()
                        .disableCull()
                        .disableDepthMask()
                        .build());
        EFFECT_FX_COLOR_SPHERE = createType("effect_fx_color_sphere", DefaultVertexFormats.POSITION_COLOR, GL11.GL_TRIANGLES, 32768,
                RenderStateBuilder.builder()
                        .blend(Blending.DEFAULT)
                        .alpha(0.00001F)
                        .disableDepthMask()
                        .build());
    }

    private static void initEffects() {
        EFFECT_LIGHTRAY_FAN = createType("effect_lightray_fan", DefaultVertexFormats.POSITION_COLOR, GL11.GL_TRIANGLE_FAN, 32768,
                RenderStateBuilder.builder()
                        .blend(Blending.ADDITIVE_ALPHA)
                        .smoothShade()
                        .disableDepthMask()
                        .enableDiffuseLighting()
                        .build());
    }

    private static void initGuiTypes() {
        GUI_MISC_INFO_STAR = createType("gui_misc_info_star", DefaultVertexFormats.POSITION_TEX,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_STAR_1)
                        .blend(Blending.DEFAULT)
                        .defaultAlpha()
                        .build());
    }

    private static void initTERTypes() {
        TER_WELL_LIQUID = createType("ter_well_liquid", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.DEFAULT)
                        .alpha(0.00001F)
                        .disableDepthMask()
                        .build());
        TER_CHALICE_LIQUID = createType("ter_chalice_liquid", POSITION_COLOR_TEX_NORMAL,
                RenderStateBuilder.builder()
                        .altasTexture()
                        .blend(Blending.DEFAULT)
                        .alpha(0.00001F)
                        .disableDepthMask()
                        .build());
    }

    private static void initModels() {
        MODEL_ATTUNEMENT_ALTAR = createType("model_attunement_altar", DefaultVertexFormats.ENTITY,
                RenderStateBuilder.builder()
                        .texture(AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "entity", "attunement_altar"))
                        .enableLighting()
                        .enableDiffuseLighting()
                        .enableOverlay()
                        .build());

        MODEL_LENS = createType("model_lens", DefaultVertexFormats.ENTITY,
                RenderStateBuilder.builder()
                        .texture(AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "entity", "lens_frame"))
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .enableLighting()
                        .enableDiffuseLighting()
                        .enableOverlay()
                        .build());

        MODEL_LENS_COLORED = createType("model_lens_colored", DefaultVertexFormats.ENTITY,
                RenderStateBuilder.builder()
                        .texture(AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "entity", "lens_color"))
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .enableLighting()
                        .enableDiffuseLighting()
                        .enableOverlay()
                        .build());

        MODEL_OBSERVATORY = createType("model_observatory", DefaultVertexFormats.ENTITY,
                RenderStateBuilder.builder()
                        .texture(AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "entity", "observatory"))
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .enableLighting()
                        .enableDiffuseLighting()
                        .enableOverlay()
                        .build());

        MODEL_REFRACTION_TABLE = createType("model_refraction_table", DefaultVertexFormats.ENTITY,
                RenderStateBuilder.builder()
                        .texture(AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "entity", "refraction_table"))
                        .enableLighting()
                        .enableDiffuseLighting()
                        .enableOverlay()
                        .build());

        MODEL_REFRACTION_TABLE_GLASS = createType("model_refraction_table_glass", DefaultVertexFormats.ENTITY,
                RenderStateBuilder.builder()
                        .texture(AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "entity", "refraction_table"))
                        .blend(Blending.DEFAULT)
                        .disableDepthMask()
                        .enableLighting()
                        .enableDiffuseLighting()
                        .enableOverlay()
                        .build());

        MODEL_TELESCOPE = createType("model_telescope", DefaultVertexFormats.ENTITY,
                RenderStateBuilder.builder()
                        .texture(AssetLibrary.loadTexture(AssetLoader.TextureLocation.BLOCKS, "entity", "telescope"))
                        .blend(Blending.DEFAULT)
                        .disableCull()
                        .enableLighting()
                        .enableDiffuseLighting()
                        .enableOverlay()
                        .build());

        MODEL_DEMON_WINGS = createType("model_demon_wings", POSITION_COLOR_TEX_NORMAL,
                RenderStateBuilder.builder()
                        .enableLighting()
                        .enableDiffuseLighting()
                        .smoothShade()
                        .build());

        MODEL_CELESTIAL_WINGS = createType("model_celestial_wings", POSITION_COLOR_TEX_NORMAL,
                RenderStateBuilder.builder()
                        .texture(TexturesAS.TEX_MODEL_CELESTIAL_WINGS)
                        .enableDiffuseLighting()
                        .smoothShade()
                        .build());

        MODEL_WRAITH_WINGS = createType("model_wraith_wings", POSITION_COLOR_TEX_NORMAL,
                RenderStateBuilder.builder()
                        .enableLighting()
                        .enableDiffuseLighting()
                        .smoothShade()
                        .build());
    }

    private static RenderType createType(String name, VertexFormat vertexFormat, RenderType.State state) {
        return createType(name, vertexFormat, GL11.GL_QUADS, 32768, state);
    }

    private static RenderType createType(String name, VertexFormat vertexFormat, int glDrawMode, int bufferSize, RenderType.State state) {
        return createType(name, vertexFormat, glDrawMode, bufferSize, false, false, state);
    }

    private static RenderType createType(String name, VertexFormat vertexFormat, int glDrawMode, int bufferSize, boolean usesDelegateDrawing, boolean sortVertices, RenderType.State state) {
        return RenderType.makeType(AstralSorcery.key(name).toString(), vertexFormat, glDrawMode, bufferSize, usesDelegateDrawing, sortVertices, state);
    }

}
