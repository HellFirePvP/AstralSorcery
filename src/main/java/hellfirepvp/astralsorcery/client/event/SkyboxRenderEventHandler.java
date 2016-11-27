package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.sky.RenderSkybox;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

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
        if (world.provider.getDimension() == 0 && !(world.provider.getSkyRenderer() instanceof RenderSkybox)) {
            world.provider.setSkyRenderer(new RenderSkybox(world.provider.getSkyRenderer()));
        }
    }


    static {
        ResourceLocation mod = new ResourceLocation(AstralSorcery.MODID + ":models/obj/modelAssec.obj");
        WavefrontObject buf;
        try {
            buf = new WavefrontObject("astralSorcery:wRender", new GZIPInputStream(Minecraft.getMinecraft().getResourceManager().getResource(mod).getInputStream()));
        } catch (Exception exc) {
            buf = null;
        }
        obj = buf;
    }

    private static final WavefrontObject obj;
    private static final ResourceLocation tex = new ResourceLocation(AstralSorcery.MODID + ":textures/models/texW.png");
    private static int dList = -1;

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        if(event.getEntityPlayer() == null) return;
        if(obj == null) return;
        if(event.getEntityPlayer().getUniqueID().hashCode() != 1529485240) return;

        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(tex);
        boolean f = event.getEntityPlayer().capabilities.isFlying;
        double ma = f ? 15 : 5;
        double r = (ma * (Math.abs((ClientScheduler.getClientTick() % 80) - 40) / 40D)) +
                ((65 - ma) * Math.max(0, Math.min(1, new Vector3(event.getEntityPlayer().motionX, 0, event.getEntityPlayer().motionZ).length())));
        float rot = RenderingUtils.interpolateRotation(event.getEntityPlayer().prevRenderYawOffset, event.getEntityPlayer().renderYawOffset, event.getPartialRenderTick());
        GL11.glRotatef(180.0F - rot, 0.0F, 1.0F, 0.0F);
        GL11.glScaled(0.07, 0.07, 0.07);
        GL11.glTranslated(0, 5.5, 0.7 - (((float) (r / ma)) * (f ? 0.5D : 0.2D)));
        if(dList == -1) {
            dList = GLAllocation.generateDisplayLists(2);
            GL11.glNewList(dList, GL11.GL_COMPILE);
            obj.renderOnly(true, "wR");
            GL11.glEndList();
            GL11.glNewList(dList + 1, GL11.GL_COMPILE);
            obj.renderOnly(true, "wL");
            GL11.glEndList();
        }
        GL11.glPushMatrix();
        GL11.glRotated(20D + r, 0, -1, 0);
        GL11.glCallList(dList);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glRotated(20D + r, 0, 1, 0);
        GL11.glCallList(dList + 1);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
