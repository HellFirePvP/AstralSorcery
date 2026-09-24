/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.mixin.client.AccessorPostChain;
import hellfirepvp.astralsorcery.mixin.client.AccessorPostPass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TransparencyChain
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TransparencyChain extends WrappedPostChain {

    private RenderTarget transparencyTarget;
    private WrappedPostPass blitPass;

    private RenderTarget redirectInput = null;
    private RenderTarget redirectTarget = null;

    public TransparencyChain(ResourceLocation chainKey) {
        super(chainKey);
    }

    @Override
    public void load() throws Exception {
        super.load();
        this.transparencyTarget = this.getTarget("transparency").orElse(null);

        AccessorPostChain chainAccess = this.getWrapped().map(chain -> (AccessorPostChain) chain).orElseThrow();
        int lastPass = chainAccess.getPasses().size() - 1;
        this.blitPass = WrappedPostPass.wrap(chainAccess.getPasses().get(lastPass), Minecraft.getInstance().getResourceManager());
        this.blitPass.setDepthDrawFunction(GL11.GL_LEQUAL);
        chainAccess.getPasses().set(lastPass, this.blitPass);
    }

    public Optional<RenderTarget> getTransparencyTarget() {
        return Optional.ofNullable(this.transparencyTarget);
    }

    public Supplier<RenderTarget> getTransparencyTargetSupplier() {
        return this.getTransparencyTarget()::orElseThrow;
    }

    public Optional<WrappedPostPass> getBlitPass() {
        return Optional.ofNullable(this.blitPass);
    }

    public void setColor(ColorWrapper color) {
        //There's only 1 pass in this shader chain, so it'll always be the first pass we find
        this.getWrapped().map(chain -> (AccessorPostChain) chain)
                .flatMap(chain -> chain.getPasses().stream().findFirst().map(PostPass::getEffect))
                .ifPresent(effect -> {
                    effect.safeGetUniform("ColorOverlay").set(color.getRgba());
                });
    }

    public void setIgnoreDepth(boolean ignoreDepth) {
        this.getWrapped().map(chain -> (AccessorPostChain) chain)
                .flatMap(chain -> chain.getPasses().stream().findFirst().map(PostPass::getEffect))
                .ifPresent(effect -> {
                    effect.safeGetUniform("IgnoreDepth").set(ignoreDepth ? 1 : 0);
                });
    }

    public void redirect(RenderTarget newTarget, Consumer<PostChain> redirectedFn) {
        this.getWrapped().ifPresent(chain -> {
            this.setupRedirect(newTarget);
            redirectedFn.accept(chain);
            this.revertRedirect();
        });
    }

    private void setupRedirect(RenderTarget newTarget) {
        if (this.redirectTarget != null) {
            throw new IllegalStateException("Chain already redirected!");
        }

        this.getWrapped().map(chain -> (AccessorPostChain) chain).ifPresent(chain -> {
            PostPass inPass = chain.getPasses().getFirst();
            AccessorPostPass inPassAccess = MiscUtil.cast(inPass);
            this.redirectInput = inPass.inTarget;
            inPassAccess.setInTarget(newTarget);

            PostPass outPass = chain.getPasses().getLast();
            AccessorPostPass outPassAccess = MiscUtil.cast(outPass);
            this.redirectTarget = outPass.outTarget;
            outPassAccess.setOutTarget(newTarget);
        });
    }

    private void revertRedirect() {
        this.getWrapped().map(chain -> (AccessorPostChain) chain).ifPresent(chain -> {
            AccessorPostPass inPass = MiscUtil.cast(chain.getPasses().getFirst());
            inPass.setInTarget(this.redirectInput);
            this.redirectInput = null;

            AccessorPostPass outPass = MiscUtil.cast(chain.getPasses().getLast());
            outPass.setOutTarget(this.redirectTarget);
            this.redirectTarget = null;
        });
    }
}
