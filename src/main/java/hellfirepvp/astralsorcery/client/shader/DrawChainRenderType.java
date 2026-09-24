/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DrawChainRenderType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DrawChainRenderType extends RenderType {

    private static final Map<String, DrawChainRenderType> WRAPPED_CACHE = new HashMap<>();

    protected final RenderType decorated;
    private final Supplier<RenderTarget> newTarget;
    private final Runnable preRender, postRender;

    private int thisFrameTarget = 0;

    protected DrawChainRenderType(RenderType decorated, Supplier<RenderTarget> newTarget, Runnable preRender, Runnable postRender) {
        super(decorated.name, decorated.format, decorated.mode, decorated.bufferSize, decorated.affectsCrumbling, decorated.sortOnUpload, decorated.setupState, decorated.clearState);
        this.decorated = decorated;
        this.newTarget = newTarget;
        this.preRender = preRender;
        this.postRender = postRender;
    }

    public static DrawChainRenderType wrap(String id, RenderType type, RenderTarget newTarget) {
        return wrap(id, type, newTarget, () -> {}, () -> {});
    }

    public static DrawChainRenderType wrap(String id, RenderType type, RenderTarget newTarget, Runnable preRender, Runnable postRender) {
        return wrap(id, type, () -> newTarget, preRender, postRender);
    }

    public static DrawChainRenderType wrap(String id, RenderType type, Supplier<RenderTarget> newTarget, Runnable preRender, Runnable postRender) {
        String reference = String.format("%s_%s", id, type.name);
        if (WRAPPED_CACHE.containsKey(reference)) return WRAPPED_CACHE.get(reference);

        DrawChainRenderType drawType = new DrawChainRenderType(type, newTarget, preRender, postRender);
        WRAPPED_CACHE.put(reference, drawType);
        return drawType;
    }

    @Override
    public void setupRenderState() {
        this.decorated.setupRenderState();

        this.preRender.run();
        this.thisFrameTarget = GlStateManager.getBoundFramebuffer();
        this.newTarget.get().bindWrite(false);
    }

    @Override
    public void clearRenderState() {
        this.newTarget.get().unbindWrite();
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.thisFrameTarget);
        this.postRender.run();
        this.thisFrameTarget = 0;
        this.decorated.clearRenderState();
    }

    @Override
    public void draw(MeshData meshData) {
        this.setupRenderState();
        BufferUploader.drawWithShader(meshData);
        this.clearRenderState();
    }

    @Override
    public String toString() {
        return this.decorated.toString();
    }

    @Override
    public int bufferSize() {
        return this.decorated.bufferSize();
    }

    @Override
    public VertexFormat format() {
        return this.decorated.format();
    }

    @Override
    public VertexFormat.Mode mode() {
        return this.decorated.mode();
    }

    @Override
    public Optional<RenderType> outline() {
        return this.decorated.outline();
    }

    @Override
    public boolean isOutline() {
        return this.decorated.isOutline();
    }

    @Override
    public boolean affectsCrumbling() {
        return this.decorated.affectsCrumbling();
    }

    @Override
    public boolean canConsolidateConsecutiveGeometry() {
        return this.decorated.canConsolidateConsecutiveGeometry();
    }

    @Override
    public boolean sortOnUpload() {
        return this.decorated.sortOnUpload();
    }
}
