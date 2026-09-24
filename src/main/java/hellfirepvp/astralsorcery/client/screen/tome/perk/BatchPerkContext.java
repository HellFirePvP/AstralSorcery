/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.perk;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.screen.tome.perk.render.PerkRenderType;
import net.minecraft.client.renderer.RenderType;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BatchPerkContext
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BatchPerkContext {

    public static final int PRIORITY_BACKGROUND = 100;
    public static final int PRIORITY_FOREGROUND = 200;
    public static final int PRIORITY_OVERLAY    = 300;

    private final Set<PerkRenderType> renderTypes = new TreeSet<>();
    private final List<ByteBufferBuilder> openBuffers = new ArrayList<>();

    private final ByteBufferBuilder sharedBuffer;
    private final Map<PerkRenderType, ByteBufferBuilder> fixedBuffers = new HashMap<>();

    private final Map<PerkRenderType, BufferBuilder> drawingBuffers = new HashMap<>();

    private BatchPerkContext(Collection<PerkRenderType> renderTypes) {
        this.renderTypes.addAll(renderTypes);

        this.sharedBuffer = new ByteBufferBuilder(0x7_0000);
        this.openBuffers.add(this.sharedBuffer);

        for (PerkRenderType ort : this.renderTypes) {
            if (this.fixedBuffers.containsKey(ort)) continue;

            ByteBufferBuilder typeBuffer = new ByteBufferBuilder(0x7_0000);
            this.openBuffers.add(typeBuffer);
            this.fixedBuffers.put(ort, typeBuffer);
        }
    }

    public VertexConsumer getBuffer(PerkRenderType type) {
        if (this.openBuffers.isEmpty()) {
            throw new IllegalStateException("Tried to get buffer from already closed BatchPerkContext!");
        }
        if (this.drawingBuffers.containsKey(type)) {
            return this.drawingBuffers.get(type);
        }
        ByteBufferBuilder builder = this.fixedBuffers.getOrDefault(type, this.sharedBuffer);
        BufferBuilder drawingBuffer = new BufferBuilder(builder, type.renderType().mode(), type.renderType().format());
        this.drawingBuffers.put(type, drawingBuffer);
        return drawingBuffer;
    }

    public void draw() {
        this.renderTypes.forEach(type -> {
            if (this.drawingBuffers.containsKey(type)) {
                BufferBuilder buf = this.drawingBuffers.get(type);
                MeshData mesh = buf.build();
                if (mesh != null) {
                    type.renderType().draw(mesh);
                }
            }
        });
        this.drawingBuffers.clear();
    }

    public void free() {
        this.openBuffers.forEach(ByteBufferBuilder::close);
        this.openBuffers.clear();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<PerkRenderType> renderTypes = new ArrayList<>();

        public Builder addRenderType(PerkRenderType renderType) {
            if (this.renderTypes.stream().noneMatch(rt -> rt.equals(renderType))) {
                this.renderTypes.add(renderType);
            }
            return this;
        }

        public BatchPerkContext build() {
            return new BatchPerkContext(this.renderTypes);
        }
    }
}
