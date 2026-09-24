/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WrappedPostPass
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WrappedPostPass extends PostPass {

    private final List<IntSupplier> auxAssets = Lists.newArrayList();
    private final List<String> auxNames = Lists.newArrayList();
    private final List<Integer> auxWidths = Lists.newArrayList();
    private final List<Integer> auxHeights = Lists.newArrayList();
    private Matrix4f shaderOrthoMatrix;

    private int depthDrawFn = GL11.GL_ALWAYS;

    protected WrappedPostPass(ResourceProvider resourceProvider, EffectInstance effect, RenderTarget inTarget, RenderTarget outTarget, boolean useLinearFilter) throws IOException {
        super(resourceProvider, effect.getName(), inTarget, outTarget, useLinearFilter);
        effect.close();
    }

    public static WrappedPostPass wrap(PostPass pass, ResourceProvider resourceProvider) throws IOException {
        return new WrappedPostPass(resourceProvider, pass.getEffect(), pass.inTarget, pass.outTarget,
                pass.getFilterMode() == GL11.GL_LINEAR);
    }

    @Override
    public void addAuxAsset(String auxName, IntSupplier auxFramebuffer, int width, int height) {
        this.auxNames.add(this.auxNames.size(), auxName);
        this.auxAssets.add(this.auxAssets.size(), auxFramebuffer);
        this.auxWidths.add(this.auxWidths.size(), width);
        this.auxHeights.add(this.auxHeights.size(), height);
    }

    @Override
    public void setOrthoMatrix(Matrix4f shaderOrthoMatrix) {
        this.shaderOrthoMatrix = shaderOrthoMatrix;
    }

    public void setDepthDrawFunction(int depthDrawFn) {
        this.depthDrawFn = depthDrawFn;
    }

    @Override
    public void process(float partialTicks) {
        EffectInstance effect = this.getEffect();
        Window window = Minecraft.getInstance().getWindow();

        this.inTarget.unbindWrite();
        float outWidth = (float) this.outTarget.width;
        float outHeight = (float) this.outTarget.height;
        RenderSystem.viewport(0, 0, this.outTarget.width, this.outTarget.height);
        effect.setSampler("DiffuseSampler", this.inTarget::getColorTextureId);

        for (int i = 0; i < this.auxAssets.size(); i++) {
            effect.setSampler(this.auxNames.get(i), this.auxAssets.get(i));
            effect.safeGetUniform("AuxSize" + i).set((float) this.auxWidths.get(i), (float) this.auxHeights.get(i));
        }

        effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
        effect.safeGetUniform("InSize").set((float) this.inTarget.width, (float) this.inTarget.height);
        effect.safeGetUniform("OutSize").set(outWidth, outHeight);
        effect.safeGetUniform("Time").set(partialTicks);
        effect.safeGetUniform("ScreenSize").set(window.getWidth(), window.getHeight());
        effect.apply();
        this.outTarget.clear(Minecraft.ON_OSX);
        this.outTarget.bindWrite(false);
        RenderSystem.depthFunc(this.depthDrawFn);
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.addVertex(0.0F, 0.0F, 500.0F);
        bufferbuilder.addVertex(outWidth, 0.0F, 500.0F);
        bufferbuilder.addVertex(outWidth, outHeight, 500.0F);
        bufferbuilder.addVertex(0.0F, outHeight, 500.0F);
        BufferUploader.draw(bufferbuilder.buildOrThrow());
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        effect.clear();
        this.outTarget.unbindWrite();
        this.inTarget.unbindRead();

        for (Object object : this.auxAssets) {
            if (object instanceof RenderTarget target) {
                target.unbindRead();
            }
        }
    }
}
