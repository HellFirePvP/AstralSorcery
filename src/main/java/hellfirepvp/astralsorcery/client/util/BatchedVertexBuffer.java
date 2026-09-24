/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BatchedVertexBuffer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BatchedVertexBuffer {

    private VertexBuffer vbo = null;
    private boolean initialized = false;

    public void initialize(Supplier<MeshData> batch) {
        if (this.initialized) {
            return;
        }

        this.vbo = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.vbo.bind();
        this.vbo.upload(batch.get());
        VertexBuffer.unbind();

        this.initialized = true;
    }

    public void draw() {
        if (!this.initialized) return;

        this.vbo.bind();
        this.vbo.draw();
        VertexBuffer.unbind();
    }

    public void drawWithShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        this.drawWithShader(modelViewMatrix, projectionMatrix, RenderSystem.getShader());
    }

    public void drawWithShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader) {
        if (!this.initialized) return;

        this.vbo.bind();
        this.vbo.drawWithShader(modelViewMatrix, projectionMatrix, shader);
        VertexBuffer.unbind();
    }

    public void close() {
        if (this.vbo != null) this.vbo.close();
        this.initialized = false;
    }
}
