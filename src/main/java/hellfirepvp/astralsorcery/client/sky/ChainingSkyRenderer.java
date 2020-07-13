/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.sky.astral.AstralSkyRenderer;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.client.IRenderHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChainingSkyRenderer
 * Created by HellFirePvP
 * Date: 13.01.2020 / 19:48
 */
public class ChainingSkyRenderer implements IRenderHandler {

    private IRenderHandler existingSkyRenderer;

    public ChainingSkyRenderer(IRenderHandler existingSkyRenderer) {
        this.existingSkyRenderer = existingSkyRenderer;
    }

    @Override
    public void render(int ticks, float partialTicks, ClientWorld world, Minecraft mc) {
        EventFlags.SKY_RENDERING.executeWithFlag(() -> {

            //Massive assumptions here noone messed with the rendering stack.
            MatrixStack renderStack = new MatrixStack();
            ActiveRenderInfo ari = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
            renderStack.rotate(Vector3f.ZP.rotationDegrees(0));
            renderStack.rotate(Vector3f.XP.rotationDegrees(ari.getPitch()));
            renderStack.rotate(Vector3f.YP.rotationDegrees(ari.getYaw() + 180.0F));

            int dimId = world.getDimension().getType().getId();
            if (world.dimension.isSurfaceWorld()) {
                if (RenderingConfig.CONFIG.weakSkyRenders.get().contains(dimId)) {
                    if (existingSkyRenderer != null) {
                        existingSkyRenderer.render(ticks, partialTicks, world, mc);
                    } else {
                        IRenderHandler existing = world.getDimension().getSkyRenderer();
                        world.getDimension().setSkyRenderer(null);
                        Minecraft.getInstance().worldRenderer.renderSky(renderStack, partialTicks);
                        world.getDimension().setSkyRenderer(existing);
                    }

                    this.renderConstellations(world, renderStack, partialTicks);
                } else {
                    AstralSkyRenderer.INSTANCE.render(ticks, partialTicks, world, mc);
                }
            } else {
                IRenderHandler existing = world.getDimension().getSkyRenderer();
                world.getDimension().setSkyRenderer(null);
                //Actually ends up calling renderEndSky
                Minecraft.getInstance().worldRenderer.renderSky(renderStack, partialTicks);
                world.getDimension().setSkyRenderer(existing);
            }
        });
    }

    private void renderConstellations(ClientWorld world, MatrixStack renderStack, float pTicks) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        Blending.ADDITIVE_ALPHA.apply();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(false);
        float alphaSubRain = 1.0F - world.getRainStrength(pTicks);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alphaSubRain);

        renderStack.push();
        renderStack.rotate(Vector3f.XP.rotationDegrees(180));
        AstralSkyRenderer.renderConstellationsSky(world, renderStack, pTicks);
        renderStack.pop();

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.depthMask(true);
        RenderSystem.disableTexture();
        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }
}
