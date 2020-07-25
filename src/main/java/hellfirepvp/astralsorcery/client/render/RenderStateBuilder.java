package hellfirepvp.astralsorcery.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderStateBuilder
 * Created by HellFirePvP
 * Date: 05.06.2020 / 16:27
 */
public class RenderStateBuilder {

    private final RenderType.State.Builder builder;

    private RenderStateBuilder(RenderType.State.Builder builder) {
        this.builder = builder;
    }

    public static RenderStateBuilder builder() {
        return new RenderStateBuilder(RenderType.State.getBuilder());
    }

    public RenderStateBuilder texture(AbstractRenderableTexture texture) {
        this.builder.texture(texture.asState());
        return this;
    }

    public RenderStateBuilder altasTexture() {
        this.builder.texture(BlockAtlasTexture.getInstance().asState());
        return this;
    }

    public RenderStateBuilder blend(Blending blendMode) {
        this.builder.transparency(blendMode.asState());
        return this;
    }

    public RenderStateBuilder smoothShade() {
        this.builder.shadeModel(new RenderState.ShadeModelState(true));
        return this;
    }

    public RenderStateBuilder enableItemRendering() {
        this.builder.diffuseLighting(new RenderState.DiffuseLightingState(true));
        return this;
    }

    public RenderStateBuilder disableDepth() {
        this.builder.depthTest(new RenderState.DepthTestState(GL11.GL_ALWAYS) {
            @Override
            public void setupRenderState() {
                //For some ungodly reason this might not be reset to disable depth testing by default...
                //I guess we're gonna do it manually then.
                RenderSystem.disableDepthTest();
                super.setupRenderState();
            }
        });
        return this;
    }

    public RenderStateBuilder disableDepthMask() {
        this.builder.writeMask(new RenderState.WriteMaskState(true, false));
        return this;
    }

    public RenderStateBuilder enableLighting() {
        this.builder.lightmap(new RenderState.LightmapState(true));
        return this;
    }

    public RenderStateBuilder enableDiffuseLighting() {
        this.builder.diffuseLighting(new RenderState.DiffuseLightingState(true));
        return this;
    }

    public RenderStateBuilder enableOverlay() {
        this.builder.overlay(new RenderState.OverlayState(true));
        return this;
    }

    public RenderStateBuilder disableCull() {
        this.builder.cull(new RenderState.CullState(false));
        return this;
    }

    public RenderStateBuilder alpha(float alphaThreshold) {
        this.builder.alpha(new RenderState.AlphaState(alphaThreshold));
        return this;
    }

    public RenderStateBuilder defaultAlpha() {
        this.builder.alpha(new RenderState.AlphaState(1F / 255F));
        return this;
    }

    public RenderType.State.Builder vanillaBuilder() {
        return this.builder;
    }

    public RenderType.State buildAsOverlay() {
        return this.builder.build(true);
    }

    public RenderType.State build() {
        return this.builder.build(false);
    }
}
