package hellfire.astralSorcery.client.renderer.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 25.01.2016 01:49
 */
public class RenderSkybox extends IRenderHandler {

    private static final RenderDefaultSkybox defaultSky = new RenderDefaultSkybox();
    private static final RenderAstralSkybox astralSky = new RenderAstralSkybox();

    //private final IRenderHandler otherSkyRenderer;

    public RenderSkybox(/*IRenderHandler skyRenderer*/) {
        //this.otherSkyRenderer = skyRenderer;
    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        /*if(otherSkyRenderer != null) {
            //Expecting a world renderer that does not render the whole sky, only a part of it.
            //Its the overworld after all. The sky "should" not be changed Kappa
            otherSkyRenderer.render(partialTicks, world, mc);
        }*/

        if(!astralSky.isInitialized() && world.provider.getDimensionId() == 0) { //DimID == 0 should always be the case tho.
            astralSky.setInitialized(world.getWorldInfo().getSeed());
        }

        if(true) {
            astralSky.render(partialTicks, world, mc);
        } else {
            /*if(otherSkyRenderer == null) */
                defaultSky.render(partialTicks, world, mc);
        }
    }

    static {
        RenderDefaultSkybox.setupDefaultSkybox();
    }

}
