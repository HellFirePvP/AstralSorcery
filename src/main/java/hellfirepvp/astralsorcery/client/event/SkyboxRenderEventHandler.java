package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.client.sky.RenderSkybox;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyboxRenderEventHandler
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:43
 */
public class SkyboxRenderEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world.provider.getDimension() == 0 && !(world.provider.getSkyRenderer() instanceof RenderSkybox)) {
            world.provider.setSkyRenderer(new RenderSkybox(world.provider.getSkyRenderer()));
        }
    }

}
