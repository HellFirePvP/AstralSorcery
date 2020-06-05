package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.render.RenderStateBuilder;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
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

        CONSTELLATION_STARS_IN_WORLD = createType("constellation_stars_in_world", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder().texture(TexturesAS.TEX_STAR_1).build());
        CONSTELLATION_LINES_IN_WORLD = createType("constellation_lines_in_world", DefaultVertexFormats.POSITION_COLOR_TEX,
                RenderStateBuilder.builder().texture(TexturesAS.TEX_STAR_CONNECTION).build());
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
                        .texture(BlockAtlasTexture.getInstance())
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
                        .blend(Blending.PREALPHA)
                        .defaultAlpha()
                        .disableCull()
                        .disableDepthMask()
                        .build());

        EFFECT_FX_BLOCK_TRANSLUCENT = createType("effect_fx_block_translucent", DefaultVertexFormats.BLOCK,
                RenderStateBuilder.builder()
                        .texture(BlockAtlasTexture.getInstance())
                        .blend(Blending.ADDITIVEDARK)
                        .defaultAlpha()
                        .disableCull()
                        .build());
        EFFECT_FX_BLOCK_TRANSLUCENT_DEPTH = createType("effect_fx_block_translucent_depth", DefaultVertexFormats.BLOCK,
                RenderStateBuilder.builder()
                        .texture(BlockAtlasTexture.getInstance())
                        .blend(Blending.ADDITIVEDARK)
                        .defaultAlpha()
                        .disableCull()
                        .disableDepth()
                        .build());
        EFFECT_FX_COLOR_SPHERE = createType("effect_fx_color_sphere", DefaultVertexFormats.POSITION_COLOR, GL11.GL_TRIANGLES, 32768,
                RenderStateBuilder.builder()
                        .blend(Blending.DEFAULT)
                        .alpha(0.00001F)
                        .disableDepthMask()
                        .build());

        EFFECT_FX_CUBE_OPAQUE_ATLAS = createType("effect_fx_cube_opaque_atlas", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP,
                RenderStateBuilder.builder()
                        .texture(BlockAtlasTexture.getInstance())
                        .blend(Blending.DEFAULT)
                        .defaultAlpha()
                        .disableCull()
                        .enableLighting()
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
