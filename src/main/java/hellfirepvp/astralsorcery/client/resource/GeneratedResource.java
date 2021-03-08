/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GeneratedResource
 * Created by HellFirePvP
 * Date: 06.01.2021 / 11:00
 */
public class GeneratedResource extends BindableResource implements ReloadableResource {

    private final Supplier<BufferedImage> imageGen;
    private final boolean blur, clamp;

    GeneratedResource(ResourceLocation key, Supplier<BufferedImage> imageGenerator, boolean blur, boolean clamp) {
        super(NameUtil.prefixPath(key, "dynamic_"));
        this.imageGen = imageGenerator;
        this.blur = blur;
        this.clamp = clamp;
    }

    @Override
    protected Texture allocateGlId() {
        if (AssetLibrary.isReloading()) {
            return null;
        }
        TextureManager mgr = Minecraft.getInstance().getTextureManager();
        Texture resource = mgr.getTexture(this.getKey());
        if (resource != null) {
            return resource;
        }
        InMemoryTexture texture = new InMemoryTexture(this.imageGen, this.blur, this.clamp);
        mgr.loadTexture(this.getKey(), texture);
        return mgr.getTexture(this.getKey());
    }

    private static class InMemoryTexture extends Texture {

        private final Supplier<BufferedImage> imageGen;
        private final boolean blur, clamp;

        private InMemoryTexture(Supplier<BufferedImage> imageGen, boolean blur, boolean clamp) {
            this.imageGen = imageGen;
            this.blur = blur;
            this.clamp = clamp;
        }

        @Override
        public void loadTexture(IResourceManager manager) throws IOException {
            NativeImage image = NativeImage.read(NativeImage.PixelFormat.RGBA, createMemInput());
            if (!RenderSystem.isOnRenderThreadOrInit()) {
                RenderSystem.recordRenderCall(() -> this.loadImage(image, this.blur, this.clamp));
            } else {
                this.loadImage(image, this.blur, this.clamp);
            }
        }

        private void loadImage(NativeImage imageIn, boolean blurIn, boolean clampIn) {
            TextureUtil.prepareImage(this.getGlTextureId(), 0, imageIn.getWidth(), imageIn.getHeight());
            imageIn.uploadTextureSub(0, 0, 0, 0, 0, imageIn.getWidth(), imageIn.getHeight(), blurIn, clampIn, false, true);
        }

        private InputStream createMemInput() throws IOException {
            BufferedImage bufferedImage = imageGen.get();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            baos.close();
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }
}
