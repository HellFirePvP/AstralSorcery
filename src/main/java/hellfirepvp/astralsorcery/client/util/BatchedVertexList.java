/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BatchedVertexList
 * Created by HellFirePvP
 * Date: 13.01.2020 / 21:03
 */
public class BatchedVertexList {

    private final VertexFormat vFormat;
    private final int vertexLength;
    private final int vertexStride;

    private int glDrawList = -1;
    private VertexBuffer vbo = null;

    private boolean initialized = false;

    public BatchedVertexList(VertexFormat vFormat, int vertexStrideLength) {
        this.vFormat = vFormat;
        this.vertexLength = vFormat.getSize();
        this.vertexStride = vertexStrideLength;
    }

    public void batch(Consumer<BufferBuilder> batchFn) {
        if (this.initialized) {
            return;
        }

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();
        if (false && GLX.useVbo()) {
            this.vbo = new VertexBuffer(this.vFormat);
            batchFn.accept(buf);
            buf.finishDrawing();
            buf.reset();
            this.vbo.bufferData(buf.getByteBuffer());
        } else {
            this.glDrawList = GLAllocation.generateDisplayLists(1);
            GlStateManager.newList(this.glDrawList, GL11.GL_COMPILE);
            batchFn.accept(buf);
            tes.draw();
            GlStateManager.endList();
        }
        this.initialized = true;
    }

    public void render() {
        if (!this.initialized) {
            return;
        }

        if (false && GLX.useVbo()) {
            this.vbo.bindBuffer();
            GlStateManager.enableClientState(GL11.GL_VERTEX_ARRAY);
            GlStateManager.vertexPointer(this.vertexLength, GL11.GL_FLOAT, this.vertexStride, 0);
            this.vbo.drawArrays(GL11.GL_QUADS);
            VertexBuffer.unbindBuffer();
            GlStateManager.disableClientState(GL11.GL_VERTEX_ARRAY);
        } else {
            GlStateManager.callList(this.glDrawList);
        }
    }

    public void reset() {
        if (this.glDrawList >= 0) {
            GLAllocation.deleteDisplayLists(this.glDrawList);
            this.glDrawList = -1;
        }
        if (this.vbo != null) {
            this.vbo.deleteGlBuffers();
        }
    }
}
