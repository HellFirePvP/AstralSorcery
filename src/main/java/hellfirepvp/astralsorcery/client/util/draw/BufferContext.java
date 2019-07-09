/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.draw;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldVertexBufferUploader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BufferContext
 * Created by HellFirePvP
 * Date: 08.07.2019 / 20:39
 */
public class BufferContext extends BufferBuilder {

    private static final WorldVertexBufferUploader upload = new WorldVertexBufferUploader();

    BufferContext(int size) {
        super(size);
    }

    public void draw() {
        this.finishDrawing();
        upload.draw(this);
    }

}
