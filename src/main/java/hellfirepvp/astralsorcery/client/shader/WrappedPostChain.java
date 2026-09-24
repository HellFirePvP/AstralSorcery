/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WrappedPostChain
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WrappedPostChain {

    private PostChain wrapped;
    private final ResourceLocation chainKey;

    public WrappedPostChain(ResourceLocation chainKey) {
        this.chainKey = chainKey;
    }

    public final Optional<PostChain> getWrapped() {
        return Optional.ofNullable(this.wrapped);
    }

    public final ResourceLocation getChainKey() {
        return this.chainKey;
    }

    public final Optional<RenderTarget> getTarget(String name) {
        return this.getWrapped().map(chain -> chain.getTempTarget(name));
    }

    public void load() throws Exception {
        TextureManager texMgr = Minecraft.getInstance().getTextureManager();
        ResourceManager resMgr = Minecraft.getInstance().getResourceManager();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();

        this.wrapped = new PostChain(texMgr, resMgr, mainTarget, this.chainKey);
    }

    public void close() {
        this.getWrapped().ifPresent(PostChain::close);
    }

    public void resize(int width, int height) {
        this.getWrapped().ifPresent(chain -> chain.resize(width, height));
    }
}
