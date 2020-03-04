/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonDataManager;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeWraithWings
 * Created by HellFirePvP
 * Date: 04.03.2020 / 23:06
 */
public class TypeWraithWings extends PatreonEffect {

    public static Object wings;
    public int wingsDList = -1;

    public final UUID playerUUID;

    public TypeWraithWings(UUID effectUUID, @Nullable FlareColor flareColor, UUID playerUUID) {
        super(effectUUID, flareColor);
        this.playerUUID = playerUUID;
    }

    @Override
    public void initialize() {
        super.initialize();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    void onRender(RenderPlayerEvent.Post event) {
        PlayerEntity player = event.getPlayer();
        if (!player.getUniqueID().equals(playerUUID) || player.isPassenger() || player.isElytraFlying()) {
            return;
        }

        if (wings == null) {
            wings = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "wraith_wings");
        }

        if (wingsDList == -1) {
            wingsDList = GLAllocation.generateDisplayLists(2);
            GlStateManager.newList(wingsDList, GL11.GL_COMPILE);
            ((WavefrontObject) wings).renderOnly(true, "Bones");
            GlStateManager.endList();
            GlStateManager.newList(wingsDList + 1, GL11.GL_COMPILE);
            ((WavefrontObject) wings).renderOnly(true, "Wing");
            GlStateManager.endList();
        }

        float rot = RenderingVectorUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, event.getPartialRenderTick());
        Vec3d motion = player.getMotion();
        double ma = 5;
        double r = (ma * (Math.abs((ClientScheduler.getClientTick() % 200) - 100) / 100D)) +
                ((15 - ma) * Math.max(0, Math.min(1, new Vector3(motion.x, 0, motion.z).length())));

        float yOffset = 1.2F;
        if (player.isSneaking() && player.onGround) {
            yOffset = 1F;
        }

        GlStateManager.color4f(1F, 1F, 1F, 1F);
        GlStateManager.disableTexture();

        GlStateManager.pushMatrix();
        GlStateManager.translated(event.getX(), event.getY(), event.getZ());


        float swimAngle = player.getSwimAnimation(event.getPartialRenderTick());
        if (swimAngle > 0) {
            float waterPitch = player.isInWater() ? -90.0F - player.rotationPitch : -90.0F;
            float bodySwimAngle = MathHelper.lerp(swimAngle, 0.0F, waterPitch);
            GlStateManager.rotated(180F - rot, 0F, 1F, 0F);
            GlStateManager.rotatef(bodySwimAngle, 1F, 0F, 0F);
            if (player.isActualySwimming()) {
                GlStateManager.translatef(0.0F, -1.0F, 0.3F);
            }
        } else {
            GlStateManager.rotated(180F - rot, 0F, 1F, 0F);
        }

        GlStateManager.translated(0, yOffset, 0);
        GlStateManager.scaled(0.32, 0.22, 0.32);

        GlStateManager.pushMatrix();
        GlStateManager.translated(-2.3, 0, 0.8);
        GlStateManager.rotatef((float) (10.0 + r), 0, 1, 0);
        GlStateManager.color4f(0.3F, 0.3F, 0.3F, 1F);
        GlStateManager.callList(wingsDList);
        GlStateManager.color4f(0F, 0F, 0F, 1F);
        GlStateManager.callList(wingsDList + 1);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.rotated(180F, 0F, 1F, 0F);
        GlStateManager.translated(-2.3, 0, -0.8);
        GlStateManager.rotatef((float) (10.0 + r), 0, -1, 0);
        GlStateManager.color4f(0.3F, 0.3F, 0.3F, 1F);
        GlStateManager.callList(wingsDList);
        GlStateManager.color4f(0F, 0F, 0F, 1F);
        GlStateManager.callList(wingsDList + 1);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        GlStateManager.enableTexture();
        GlStateManager.color4f(1F, 1F, 1F, 1F);
    }
}
