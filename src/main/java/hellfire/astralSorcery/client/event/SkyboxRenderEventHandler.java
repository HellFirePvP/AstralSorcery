package hellfire.astralSorcery.client.event;

import hellfire.astralSorcery.client.renderer.sky.RenderSkybox;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 25.01.2016 01:47
 */
public class SkyboxRenderEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(RenderWorldLastEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world.provider.getDimensionId() == 0 && !(world.provider.getSkyRenderer() instanceof RenderSkybox)) {
            world.provider.setSkyRenderer(new RenderSkybox());
        }
    }

}
