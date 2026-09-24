/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WrappedBufferSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WrappedBufferSource extends MultiBufferSource.BufferSource {

    //We gotta make our own buffers, cause relying on the base buffer isn't feasible if it's anything except raw vanilla
    //for giga custom wrapped types with custom stuff. hoped to avoid it but here we are. not fun to be here.
    private static final int MAX_POOLED_BUFFERS = 4;
    private static final Deque<ByteBufferBuilder> BUFFER_POOL = new ArrayDeque<>();

    private final MultiBufferSource wrapped;
    private final Function<RenderType, RenderType> wrapper;
    private final SequencedMap<RenderType, RenderType> wrappedTypes = new LinkedHashMap<>();
    private final Map<RenderType, RenderBatch> batches = new HashMap<>();

    public WrappedBufferSource(MultiBufferSource wrapped, Function<RenderType, RenderType> wrapper) {
        super(null, null);
        this.wrapped = wrapped;
        this.wrapper = wrapper;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        RenderType wrapped = this.wrappedTypes.computeIfAbsent(renderType, this.wrapper);

        //cleanup old batch if not chainable
        RenderBatch batch = this.batches.get(wrapped);
        if (batch != null && !wrapped.canConsolidateConsecutiveGeometry()) {
            this.batches.remove(wrapped);
            batch.draw(wrapped);
            batch = null;
        }

        if (batch == null) {
            if (this.wrapped instanceof BufferSource src) {
                src.endLastBatch();
            }
            batch = RenderBatch.of(wrapped);
            this.batches.put(wrapped, batch);
        }
        return batch.builder();
    }

    public void end() {
        new ArrayList<>(this.wrappedTypes.reversed().keySet()).forEach(this::drawWrapped);
    }

    @Override
    public void endLastBatch() {
        //might as well draw all wrapped types we have
        this.end();
    }

    @Override
    public void endBatch() {
        this.end();
    }

    @Override
    public void endBatch(RenderType renderType) {
        this.drawWrapped(renderType);
    }

    protected void drawWrapped(RenderType raw) {
        RenderType chainType = this.wrappedTypes.remove(raw);
        if (chainType == null) {
            return;
        }
        RenderBatch batch = this.batches.remove(chainType);
        if (batch != null) {
            batch.draw(chainType);
        }
    }

    private static ByteBufferBuilder fetchBuffer(int initialSize) {
        ByteBufferBuilder pooled = BUFFER_POOL.pollLast();
        return pooled != null ? pooled : new ByteBufferBuilder(initialSize);
    }

    private static void closeBuffer(ByteBufferBuilder buffer) {
        if (BUFFER_POOL.size() >= MAX_POOLED_BUFFERS) {
            buffer.close();
        } else {
            BUFFER_POOL.addLast(buffer);
        }
    }

    private record RenderBatch(ByteBufferBuilder buffer, BufferBuilder builder) {

        private static RenderBatch of(RenderType rType) {
            ByteBufferBuilder buffer = fetchBuffer(rType.bufferSize());
            return new RenderBatch(buffer, new BufferBuilder(buffer, rType.mode(), rType.format()));
        }

        private void draw(RenderType rType) {
            try {
                MeshData mesh = this.builder.build();
                if (mesh != null) {
                    if (rType.sortOnUpload()) {
                        mesh.sortQuads(this.buffer, RenderSystem.getVertexSorting());
                    }
                    rType.draw(mesh);
                }
            } finally {
                this.buffer.discard();
                closeBuffer(this.buffer);
            }
        }
    }
}
