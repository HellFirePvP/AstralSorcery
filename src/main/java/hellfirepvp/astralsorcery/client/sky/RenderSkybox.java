/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.common.data.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderSkybox
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:44
 */
public class RenderSkybox extends IRenderHandler {

    private static boolean inRender = false;

    private static final RenderDefaultSkybox defaultSky = new RenderDefaultSkybox();
    private static final RenderAstralSkybox astralSky = new RenderAstralSkybox();

    private final IRenderHandler otherSkyRenderer;
    private final World world;

    public RenderSkybox(World world, IRenderHandler skyRenderer) {
        this.otherSkyRenderer = skyRenderer;
        this.world = world;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if (!astralSky.isInitialized() && !AssetLibrary.reloading) {
            astralSky.setInitialized(world.getWorldInfo().getSeed());
        }

        if (inRender) return;

        inRender = true;

        if (Config.weakSkyRendersWhitelist.contains(world.provider.getDimension())) {
            if(otherSkyRenderer != null) {
                otherSkyRenderer.render(partialTicks, world, mc);
            } else {
                RenderGlobal rg = Minecraft.getMinecraft().renderGlobal;
                //Make vanilla guess
                if(world.provider.getDimensionType().getId() == 1) {
                    rg.renderSkyEnd();
                } else if(Minecraft.getMinecraft().world.provider.isSurfaceWorld()) {
                    IRenderHandler render = world.provider.getSkyRenderer();
                    world.provider.setSkyRenderer(null);

                    if(Minecraft.getMinecraft().gameSettings.anaglyph) {
                        EntityRenderer.anaglyphField = 0;
                        GL11.glColorMask(false, true, true, false);
                        rg.renderSky(partialTicks,0);
                        EntityRenderer.anaglyphField = 1;
                        GL11.glColorMask(true, false, false, false);
                        rg.renderSky(partialTicks,1);
                        GL11.glColorMask(true, true, true, false);
                    } else {
                        rg.renderSky(partialTicks, 2);
                    }

                    world.provider.setSkyRenderer(render);
                }
            }
            RenderAstralSkybox.renderConstellationsWrapped(world, partialTicks);
        } else {
            astralSky.render(partialTicks, world, mc);
        }

        inRender = false;
    }

    public static void resetAstralSkybox() {
        astralSky.refreshRender();
    }

    static {
        RenderDefaultSkybox.setupDefaultSkybox();
    }

}
