/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.perk;

import hellfirepvp.astralsorcery.client.util.BufferBatch;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TreeMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BatchPerkContext
 * Created by HellFirePvP
 * Date: 24.11.2018 / 11:13
 */
@SideOnly(Side.CLIENT)
public class BatchPerkContext {

    public static final int PRIORITY_BACKGROUND = 100;
    public static final int PRIORITY_FOREGROUND = 200;
    public static final int PRIORITY_OVERLAY    = 300;

    private TreeMap<TextureObjectGroup, BufferBatch> bufferGroups = new TreeMap<>();

    public TextureObjectGroup addContext(AbstractRenderableTexture tex, int sortPriority) {
        TextureObjectGroup group = MiscUtils.iterativeSearch(bufferGroups.keySet(), gr -> gr.getResource().equals(tex));
        if (group == null) {
            group = new TextureObjectGroup(tex, sortPriority);
            bufferGroups.put(group, BufferBatch.make());
        }
        return group;
    }

    @Nonnull
    public BufferBatch getContext(TextureObjectGroup grp) {
        BufferBatch ctx = bufferGroups.get(grp);
        if (ctx == null) {
            throw new IllegalArgumentException("Unknown TextureGroup!");
        }
        return ctx;
    }

    public void draw() {
        for (TextureObjectGroup group : bufferGroups.keySet()) {
            BufferBatch batch = bufferGroups.get(group);
            group.getResource().bindTexture();
            batch.draw();
        }
    }

    public void beginDrawingPerks() {
        for (TextureObjectGroup group : bufferGroups.keySet()) {
            bufferGroups.get(group)
                    .getBuffer()
                    .begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
    }

    public class TextureObjectGroup implements Comparable<TextureObjectGroup> {

        private final AbstractRenderableTexture resource;
        private final int priority;

        private TextureObjectGroup(AbstractRenderableTexture resource, int priority) {
            this.resource = resource;
            this.priority = priority;
        }

        public AbstractRenderableTexture getResource() {
            return resource;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TextureObjectGroup that = (TextureObjectGroup) o;
            return Objects.equals(resource, that.resource);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resource);
        }

        @Override
        public int compareTo(TextureObjectGroup o) {
            return Integer.compare(priority, o.priority);
        }

    }

}
