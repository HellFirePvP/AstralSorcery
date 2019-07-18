/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.zip.GZIPInputStream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientMiscEventHandler
 * Created by HellFirePvP
 * Date: 18.07.2019 / 22:03
 */
public class ClientMiscEventHandler {

    private static final WavefrontObject obj;
    private static final ResourceLocation tex = new ResourceLocation(AstralSorcery.MODID + ":textures/models/texw.png");
    private static int dList = -1;

    private ClientMiscEventHandler() {}

    static {
        ResourceLocation mod = new ResourceLocation(AstralSorcery.MODID + ":models/obj/modelassec.obj");
        WavefrontObject buf;
        try {
            buf = new WavefrontObject("astralSorcery:wingsrender", new GZIPInputStream(Minecraft.getInstance().getResourceManager().getResource(mod).getInputStream()));
        } catch (Exception exc) {
            buf = null;
        }
        obj = buf;
    }

    //Obligatory, dev gimmick
    @OnlyIn(Dist.CLIENT)
    static void onRender(RenderPlayerEvent.Post event) {
        PlayerEntity player = event.getEntityPlayer();
        if (player == null) return;
        if (obj == null) return;
        if (player.getUniqueID().hashCode() != 1529485240) return;

        if(player.isPassenger() || player.isElytraFlying()) return;

        GlStateManager.color4f(1F, 1F, 1F, 1F);

        GlStateManager.pushMatrix();
        GlStateManager.translated(event.getX(), event.getY(), event.getZ());
        Minecraft.getInstance().textureManager.bindTexture(tex);
        Vec3d motion = player.getMotion();
        boolean f = player.abilities.isFlying;
        double ma = f ? 15 : 5;
        double r = (ma * (Math.abs((ClientScheduler.getClientTick() % 80) - 40) / 40D)) +
                ((65 - ma) * Math.max(0, Math.min(1, new Vector3(motion.x, 0, motion.z).length())));
        float rot = RenderingVectorUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, event.getPartialRenderTick());
        GlStateManager.rotated(180F - rot, 0F, 1F, 0F);
        GlStateManager.scaled(0.07, 0.07, 0.07);
        GlStateManager.translated(0, 5.5, 0.7 - (((float) (r / ma)) * (f ? 0.5D : 0.2D)));
        if(dList == -1) {
            dList = GLAllocation.generateDisplayLists(2);
            GlStateManager.newList(dList, GL11.GL_COMPILE);
            obj.renderOnly(true, "wR");
            GlStateManager.endList();
            GlStateManager.newList(dList + 1, GL11.GL_COMPILE);
            obj.renderOnly(true, "wL");
            GlStateManager.endList();
        }

        GlStateManager.pushMatrix();
        GlStateManager.rotatef((float) (20.0 + r), 0, -1, 0);
        GlStateManager.callList(dList);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef((float) (20.0 + r), 0, 1, 0);
        GlStateManager.callList(dList + 1);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

}
