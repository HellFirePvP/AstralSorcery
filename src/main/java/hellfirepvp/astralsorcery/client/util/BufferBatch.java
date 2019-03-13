/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldVertexBufferUploader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BufferBatch
 * Created by HellFirePvP
 * Date: 24.11.2018 / 11:06
 */
public class BufferBatch {

    private final BufferBuilder buffer;
    private final WorldVertexBufferUploader vbProcessor;

    private BufferBatch() {
        this.buffer = new BufferBuilder(0x200_000);
        this.vbProcessor = new WorldVertexBufferUploader();
    }

    public static BufferBatch make() {
        return new BufferBatch();
    }

    public BufferBuilder getBuffer() {
        return buffer;
    }

    public void draw() {
        this.buffer.finishDrawing();
        this.vbProcessor.draw(this.buffer);
    }

}
