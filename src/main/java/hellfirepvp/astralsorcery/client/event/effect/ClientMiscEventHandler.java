/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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

    private static boolean attemptLoad = false;
    private static WavefrontObject obj;
    private static ResourceLocation tex = AstralSorcery.key("textures/model/texw.png");
    private static VertexBuffer vboR, vboL;

    private ClientMiscEventHandler() {}

    //Obligatory, dev gimmick
    @OnlyIn(Dist.CLIENT)
    static void onRender(RenderPlayerEvent.Post event) {
        PlayerEntity player = event.getPlayer();
        if (player == null) return;
        if (player.getUniqueID().hashCode() != 1529485240) return;

        if (!attemptLoad) {
            attemptLoad = true;
            ResourceLocation mod = new ResourceLocation(AstralSorcery.MODID + ":models/obj/modelassec.obj");
            try {
                obj = new WavefrontObject("astralSorcery:wingsrender", new GZIPInputStream(Minecraft.getInstance().getResourceManager().getResource(mod).getInputStream()));
            } catch (Exception exc) {}
        }
        if (attemptLoad && obj == null) {
            return;
        }

        if (player.isPassenger() || player.isElytraFlying()) return;

        Vec3d motion = player.getMotion();

        boolean f = player.abilities.isFlying;
        float ma = f ? 15 : 5;
        float r = (ma * (Math.abs((ClientScheduler.getClientTick() % 80) - 40) / 40F)) +
                ((65 - ma) * Math.max(0, Math.min(1, (float) new Vector3(motion.x, 0, motion.z).length())));
        float rot = RenderingVectorUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, event.getPartialRenderTick());

        MatrixStack renderStack = event.getMatrixStack();
        renderStack.push();
        float swimAngle = player.getSwimAnimation(event.getPartialRenderTick());
        if (swimAngle > 0) {
            float waterPitch = player.isInWater() ? -90.0F - player.rotationPitch : -90.0F;
            float bodySwimAngle = MathHelper.lerp(swimAngle, 0.0F, waterPitch);
            renderStack.rotate(Vector3f.YP.rotationDegrees(180 - rot));
            renderStack.rotate(Vector3f.XP.rotationDegrees(bodySwimAngle));
            if (player.isActualySwimming()) {
                renderStack.translate(0, -1, 0.3F);
            }
        } else {
            renderStack.rotate(Vector3f.YP.rotationDegrees(180 - rot));
        }

        renderStack.scale(0.07F, 0.07F, 0.07F);
        renderStack.translate(0, 5.5, 0.7 - ((r / ma) * (f ? 0.5D : 0.2D)));

        if (vboR == null) {
            vboR = obj.batchOnly(Tessellator.getInstance().getBuffer(), "wR");
        }
        if (vboL == null) {
            vboL = obj.batchOnly(Tessellator.getInstance().getBuffer(), "wL");
        }


        RenderTypesAS.MODEL_DEMON_WINGS.setupRenderState();
        RenderSystem.enableTexture();
        Minecraft.getInstance().getTextureManager().bindTexture(tex);

        renderStack.push();
        renderStack.rotate(Vector3f.YN.rotationDegrees(20 + r));
        vboR.bindBuffer();
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.setupBufferState(0);
        vboR.draw(renderStack.getLast().getMatrix(), GL11.GL_QUADS);
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.clearBufferState();
        VertexBuffer.unbindBuffer();
        renderStack.pop();

        renderStack.push();
        renderStack.rotate(Vector3f.YP.rotationDegrees(20 + r));
        vboL.bindBuffer();
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.setupBufferState(0);
        vboL.draw(renderStack.getLast().getMatrix(), GL11.GL_QUADS);
        RenderTypesAS.POSITION_COLOR_TEX_NORMAL.clearBufferState();
        VertexBuffer.unbindBuffer();
        renderStack.pop();

        BlockAtlasTexture.getInstance().bindTexture();
        RenderTypesAS.MODEL_DEMON_WINGS.clearRenderState();

        renderStack.pop();
    }

}
