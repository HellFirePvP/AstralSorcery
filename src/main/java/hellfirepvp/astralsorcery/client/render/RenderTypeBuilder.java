/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderTypeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderTypeBuilder {

    private final RenderType.CompositeState.CompositeStateBuilder builder;

    private RenderTypeBuilder(RenderType.CompositeState.CompositeStateBuilder builder) {
        this.builder = builder;
    }

    public static RenderTypeBuilder builder() {
        return new RenderTypeBuilder(RenderType.CompositeState.builder());
    }

    public RenderTypeBuilder shader(RenderStateShard.ShaderStateShard shaderState) {
        this.builder.setShaderState(shaderState);
        return this;
    }

    public RenderTypeBuilder texture(AbstractRenderTexture texture) {
        this.builder.setTextureState(texture.asState());
        return this;
    }

    public RenderTypeBuilder blockAtlasTexture() {
        this.builder.setTextureState(AtlasTexture.getBlockAtlas().asState());
        return this;
    }

    public RenderTypeBuilder atlasTexture(ResourceLocation atlasKey) {
        this.builder.setTextureState(new RenderStateShard.TextureStateShard(atlasKey, false, false));
        return this;
    }

    public RenderTypeBuilder disableTexture() {
        this.builder.setTextureState(new RenderStateShard.EmptyTextureStateShard(() -> {}, () -> {}));
        return this;
    }

    public RenderTypeBuilder blend(Blending blendMode) {
        this.builder.setTransparencyState(blendMode.asState());
        return this;
    }

    public RenderTypeBuilder disableDepth() {
        this.builder.setDepthTestState(new RenderStateShard.DepthTestStateShard("always", GL11.GL_ALWAYS) {
            @Override
            public void setupRenderState() {
                //For some ungodly reason removing depth testing doesn't actually set it to false by default
                //We gotta do it manually then.
                RenderSystem.disableDepthTest();
                super.setupRenderState();
            }
        });
        return this;
    }

    public RenderTypeBuilder disableDepthMask() {
        this.builder.setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false));
        return this;
    }

    public RenderTypeBuilder enableLighting() {
        this.builder.setLightmapState(new RenderStateShard.LightmapStateShard(true));
        return this;
    }

    public RenderTypeBuilder enableOverlay() {
        this.builder.setOverlayState(new RenderStateShard.OverlayStateShard(true));
        return this;
    }

    public RenderTypeBuilder disableCull() {
        this.builder.setCullState(new RenderStateShard.CullStateShard(false));
        return this;
    }

    public RenderTypeBuilder particleShaderTarget() {
        this.builder.setOutputState(RenderStateShard.PARTICLES_TARGET);
        return this;
    }

    public RenderType.CompositeState.CompositeStateBuilder vanillaBuilder() {
        return this.builder;
    }

    public RenderType.CompositeState buildAsOverlay() {
        return this.builder.createCompositeState(true);
    }

    public RenderType.CompositeState build() {
        return this.builder.createCompositeState(false);
    }
}
