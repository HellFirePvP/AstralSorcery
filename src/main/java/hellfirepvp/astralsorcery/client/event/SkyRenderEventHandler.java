/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.sky.ChainingSkyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyRenderEventHandler
 * Created by HellFirePvP
 * Date: 12.01.2020 / 22:16
 */
public class SkyRenderEventHandler {

    public static void onRender(RenderWorldLastEvent event) {
        World world = Minecraft.getInstance().world;
        if (world != null && world.getDimension().isSurfaceWorld()) {
            IRenderHandler render = world.getDimension().getSkyRenderer();
            if (!(render instanceof ChainingSkyRenderer)) {
                int dimId = world.getDimension().getType().getId();
                if (RenderingConfig.CONFIG.skyRenderDimensions.get().contains(dimId)) {
                    world.getDimension().setSkyRenderer(new ChainingSkyRenderer(world.getDimension().getSkyRenderer()));
                }
            }
        }
    }

}
