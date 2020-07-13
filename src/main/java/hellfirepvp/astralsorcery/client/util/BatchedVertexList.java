/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
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
    private VertexBuffer vbo = null;
    private boolean initialized = false;

    public BatchedVertexList(VertexFormat vFormat) {
        this.vFormat = vFormat;
    }

    public void batch(Consumer<BufferBuilder> batchFn) {
        if (this.initialized) {
            return;
        }

        BufferBuilder buf = Tessellator.getInstance().getBuffer();
        this.vbo = new VertexBuffer(this.vFormat);
        batchFn.accept(buf);
        buf.finishDrawing();
        this.vbo.upload(buf);

        this.initialized = true;
    }

    public void render(MatrixStack renderStack) {
        if (!this.initialized) {
            return;
        }

        this.vbo.bindBuffer();
        this.vFormat.setupBufferState(0L);
        this.vbo.draw(renderStack.getLast().getMatrix(), GL11.GL_QUADS);
        this.vFormat.clearBufferState();
        VertexBuffer.unbindBuffer();
    }

    public void reset() {
        if (this.vbo != null) {
            this.vbo.close();
        }

        this.initialized = false;
    }
}
