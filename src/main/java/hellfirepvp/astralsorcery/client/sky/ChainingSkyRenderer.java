/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.sky.astral.AstralSkyRenderer;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
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
            int dimId = world.getDimension().getType().getId();

            if (world.dimension.getType() == DimensionType.THE_END) {
                IRenderHandler existing = world.getDimension().getSkyRenderer();
                world.getDimension().setSkyRenderer(null);
                //Actually ends up calling renderEndSky
                Minecraft.getInstance().worldRenderer.renderSky(partialTicks);
                world.getDimension().setSkyRenderer(existing);

            } else if (world.dimension.isSurfaceWorld()) {
                if (RenderingConfig.CONFIG.weakSkyRenders.get().contains(dimId)) {
                    if (existingSkyRenderer != null) {
                        existingSkyRenderer.render(ticks, partialTicks, world, mc);
                    } else {
                        IRenderHandler existing = world.getDimension().getSkyRenderer();
                        world.getDimension().setSkyRenderer(null);
                        Minecraft.getInstance().worldRenderer.renderSky(partialTicks);
                        world.getDimension().setSkyRenderer(existing);
                    }

                    this.renderConstellations(world, partialTicks);
                } else {
                    AstralSkyRenderer.INSTANCE.render(ticks, partialTicks, world, mc);
                }
            }
        });
    }

    private void renderConstellations(World world, float pTicks) {
        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        Blending.ADDITIVE_ALPHA.applyStateManager();
        GlStateManager.enableTexture();
        GlStateManager.depthMask(false);
        float alphaSubRain = 1.0F - world.getRainStrength(pTicks);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, alphaSubRain);

        GlStateManager.pushMatrix();
        GlStateManager.rotatef(180F, 1F, 0F, 0F);

        AstralSkyRenderer.renderConstellationsSky(world, pTicks);

        GlStateManager.popMatrix();

        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.depthMask(true);
        GlStateManager.disableTexture();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }
}
