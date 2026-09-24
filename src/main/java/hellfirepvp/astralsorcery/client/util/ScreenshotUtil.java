/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenshotUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenshotUtil {

    private static RenderTarget screenshotBuffer;

    public static void prepareScreenshot() {
        if (screenshotBuffer != null) {
            throw new IllegalArgumentException("Already building screenshot.");
        }
        RenderSystem.assertOnRenderThreadOrInit();

        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        screenshotBuffer = new TextureTarget(target.width, target.height, true, Minecraft.ON_OSX);
        screenshotBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);

        screenshotBuffer.bindWrite(false);
    }

    public static void makeBufferedScreenshot() {
        if (screenshotBuffer == null) {
            throw new IllegalArgumentException("Not building screenshot.");
        }

        grabScreenshot(screenshotBuffer, screenshotBuffer.getColorTextureId(), Minecraft.getInstance().gameDirectory);
        screenshotBuffer.unbindWrite();
        screenshotBuffer.clear(Minecraft.ON_OSX);
        screenshotBuffer.destroyBuffers();
        screenshotBuffer = null;
    }

    public static void makeScreenshot(RenderTarget target) {
        grabScreenshot(target, target.getColorTextureId(), Minecraft.getInstance().gameDirectory);
    }

    public static void makeDepthScreenshot(RenderTarget target) {
        grabScreenshot(target, target.getDepthTextureId(), Minecraft.getInstance().gameDirectory);
    }

    private static void grabScreenshot(RenderTarget buffer, int textureId, File dir) {
        NativeImage nativeimage = new NativeImage(buffer.width, buffer.height, false);
        RenderSystem.bindTexture(textureId);
        nativeimage.downloadTexture(0, false);
        nativeimage.flipY();

        File scDir = new File(dir, "screenshots");
        scDir.mkdir();
        File target = getFile(scDir);

        Util.ioPool().execute(() -> {
            try {
                nativeimage.writeToFile(target);
            } catch (Exception ignored) {
            } finally {
                nativeimage.close();
            }
        });
    }

    private static File getFile(File pGameDirectory) {
        String s = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        int i = 1;

        while(true) {
            File file1 = new File(pGameDirectory, s + (i == 1 ? "" : "_" + i) + ".png");
            if (!file1.exists()) {
                return file1;
            }
            ++i;
        }
    }
}
