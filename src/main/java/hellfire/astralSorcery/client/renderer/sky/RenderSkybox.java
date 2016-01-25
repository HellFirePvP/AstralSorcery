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

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if(true) {
            defaultSky.render(partialTicks, world, mc);
        } else {
            astralSky.render(partialTicks, world, mc);
        }
    }

    static {
        RenderDefaultSkybox.setupDefaultSkybox();
        RenderAstralSkybox.setupSkybox();
    }

}
