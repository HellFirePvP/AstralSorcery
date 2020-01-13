/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.sky.astral.AstralSkyRenderer;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import net.minecraft.client.Minecraft;
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
            int dimId = world.getDimension().getType().getId();
            if (RenderingConfig.CONFIG.weakSkyRenders.get().contains(dimId)) {
                if (existingSkyRenderer != null) {
                    existingSkyRenderer.render(ticks, partialTicks, world, mc);
                } else {
                    IRenderHandler existing = world.getDimension().getSkyRenderer();
                    world.getDimension().setSkyRenderer(null);
                    Minecraft.getInstance().worldRenderer.renderSky(partialTicks);
                    world.getDimension().setSkyRenderer(existing);
                }

                //TODO render CST only
            } else {
                AstralSkyRenderer.INSTANCE.render(ticks, partialTicks, world, mc);
            }
        });
    }
}
