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
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ShadersAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ShadersAS {

    public static final RenderStateShard.ShaderStateShard POSITION_COLOR_TEX_ALPHA_SHADER = new RenderStateShard.ShaderStateShard(ShadersAS::getPositionColorTexAlphaShader);
    public static final RenderStateShard.ShaderStateShard POSITION_COLOR_TEX_ALPHA_BLOOM_SHADER = new RenderStateShard.ShaderStateShard(ShadersAS::getPositionColorTexAlphaBloomShader);

    private static ShaderInstance renderTypePositionTexAlphaShader;
    private static ShaderInstance renderTypePositionColorTexAlphaShader;
    private static ShaderInstance renderTypePositionColorTexAlphaBloomShader;

    public static ShaderInstance getPositionTexAlphaShader() {
        return Objects.requireNonNull(renderTypePositionTexAlphaShader,
                "Attempted to call getPositionTexAlphaShader before shaders have finished loading.");
    }

    public static ShaderInstance getPositionColorTexAlphaShader() {
        return Objects.requireNonNull(renderTypePositionColorTexAlphaShader,
                "Attempted to call getPositionColorTexAlphaShader before shaders have finished loading.");
    }

    public static ShaderInstance getPositionColorTexAlphaBloomShader() {
        return Objects.requireNonNull(renderTypePositionColorTexAlphaBloomShader,
                "Attempted to call getPositionColorTexAlphaBloomShader before shaders have finished loading.");
    }

    public static void registerShaders(RegisterShadersEvent event) {
        try {
            registerShader(event, "position_tex_alpha", DefaultVertexFormat.POSITION_TEX, shader -> {
                renderTypePositionTexAlphaShader = shader;
            });
            registerShader(event, "position_color_tex_alpha", DefaultVertexFormat.POSITION_TEX_COLOR, shader -> {
                renderTypePositionColorTexAlphaShader = shader;
            });
            registerShader(event, "position_color_tex_alpha_bloom", DefaultVertexFormat.POSITION_TEX_COLOR, shader -> {
                renderTypePositionColorTexAlphaBloomShader = shader;
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to register shaders", e);
        }
    }

    private static void registerShader(RegisterShadersEvent event, String name, VertexFormat format, Consumer<ShaderInstance> postLoad) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), AstralSorcery.key(name), format), postLoad);
    }
}
