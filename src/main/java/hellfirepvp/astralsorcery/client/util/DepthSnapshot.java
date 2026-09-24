/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DepthSnapshot
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
// utility to grab a snapshot of the current depth buffer and restore it later
// i didn't need it, pray you don't _need_ it either
// technically dead code, but i'll just laeve it in here for now
public class DepthSnapshot implements AutoCloseable {

    private int depthSnapshotFbo = -1;
    private int depthSnapshotRbo = -1;
    private int depthSnapshotWidth = -1;
    private int depthSnapshotHeight = -1;

    public void captureSnapshot() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        int fbWidth = mainTarget.width;
        int fbHeight = mainTarget.height;
        this.ensureBuffers(fbWidth, fbHeight);

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mainTarget.frameBufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.depthSnapshotFbo);
        GL30.glBlitFramebuffer(0, 0, fbWidth, fbHeight, 0, 0, fbWidth, fbHeight, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainTarget.frameBufferId);
    }

    public void restoreSnapshot() {
        if (this.depthSnapshotFbo == -1) return;

        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        int fbWidth = mainTarget.width;
        int fbHeight = mainTarget.height;

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.depthSnapshotFbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainTarget.frameBufferId);
        GL30.glBlitFramebuffer(0, 0, fbWidth, fbHeight, 0, 0, fbWidth, fbHeight, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainTarget.frameBufferId);
    }

    private void ensureBuffers(int width, int height) {
        if (this.depthSnapshotFbo != -1 && this.depthSnapshotWidth == width && this.depthSnapshotHeight == height) {
            return;
        }
        this.removeBuffers();

        this.depthSnapshotRbo = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.depthSnapshotRbo);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT, width, height);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

        this.depthSnapshotFbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.depthSnapshotFbo);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, this.depthSnapshotRbo);
        GL11.glDrawBuffer(GL11.GL_NONE);
        GL11.glReadBuffer(GL11.GL_NONE);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        this.depthSnapshotWidth = width;
        this.depthSnapshotHeight = height;
    }

    private void removeBuffers() {
        if (this.depthSnapshotFbo != -1) {
            GL30.glDeleteFramebuffers(this.depthSnapshotFbo);
            GL30.glDeleteRenderbuffers(this.depthSnapshotRbo);
            this.depthSnapshotFbo = -1;
            this.depthSnapshotRbo = -1;
        }
    }

    @Override
    public void close() throws Exception {
        this.removeBuffers();
    }
}
