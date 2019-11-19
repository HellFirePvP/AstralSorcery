/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.base;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.auxiliary.tick.TickManager;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.EnumSet;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectCelestialWings
 * Created by HellFirePvP
 * Date: 13.04.2019 / 13:32
 */
public class PtEffectCelestialWings extends PatreonEffectHelper.PatreonEffect implements ITickHandler {

    private final UUID playerUUID;

    private Object objShWings;
    private BindableResource texWings;
    private int dlList = -1;

    public PtEffectCelestialWings(UUID sessionEffectId,
                                  PatreonEffectHelper.FlareColor chosenColor,
                                  UUID playerUUID) {
        super(sessionEffectId, chosenColor);
        this.playerUUID = playerUUID;
    }

    @Override
    public void initialize() {
        super.initialize();

        MinecraftForge.EVENT_BUS.register(this);
        TickManager.getInstance().register(this);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EntityPlayer player = (EntityPlayer) context[0];
        Side side = (Side) context[1];
        if (side == Side.CLIENT &&
                player != null &&
                player.getUniqueID().equals(playerUUID) &&
                (Minecraft.getMinecraft().player != null &&
                        Minecraft.getMinecraft().player.getUniqueID().equals(playerUUID) &&
                        Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) {

            spawnEffects(player);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnEffects(EntityPlayer player) {
        float yOffset = 1.5F;
        if (player.isSneaking()) {
            yOffset = 1.2F;
        }
        double offset = Math.sin(Math.abs((ClientScheduler.getClientTick() % 120) - 60F) / 60F) * 0.07;
        float rot = RenderingUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, 0F);

        Vector3 look = new Vector3(1, 0, 0).rotate(Math.toRadians(360F - rot), Vector3.RotAxis.Y_AXIS).normalize();
        Vector3 pos = Vector3.atEntityCorner(player);
        pos.setY(player.posY + yOffset + offset);

        for (int i = 0; i < 4; i++) {
            double height = -0.1 + Math.min(rand.nextFloat() * 1.3, rand.nextFloat() * 1.3);
            double distance = 1.1F - (rand.nextFloat() * 0.6) * (1 - Math.max(0, height));

            Vector3 dir = look.clone().rotate(Math.toRadians(180 * (rand.nextBoolean() ? 1 : 0)), Vector3.RotAxis.Y_AXIS)
                    .normalize()
                    .multiply(distance);

            Vector3 at = pos.clone().addY(height).add(dir);

            Color col = Color.getHSBColor(0.68F, 1, 0.6F - rand.nextFloat() * 0.5F);

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at)
                    .setColor(col)
                    .scale(0.25F + rand.nextFloat() * 0.1F)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.setMaxAge(25 + rand.nextInt(25));

            if (rand.nextInt(5) == 0) {
                p.scale(0.08F + rand.nextFloat() * 0.02F);
                p.setColor(Color.WHITE);
                p.setMaxAge(10 + rand.nextInt(5));
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || !player.getUniqueID().equals(playerUUID)) return;
        if(player.isRiding() || player.isElytraFlying()) return;

        if (objShWings == null) {
            objShWings = AssetLoader.loadObjModel(AssetLoader.ModelLocation.OBJ, "sh_wings");
        }

        if (texWings == null) {
            texWings = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "star_bg");
        }

        GlStateManager.color(1F, 1F, 1F, 1F);

        float rot = RenderingUtils.interpolateRotation(player.prevRenderYawOffset, player.renderYawOffset, event.getPartialRenderTick());

        if (dlList == -1) {
            GlStateManager.pushMatrix();
            dlList = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(dlList, GL11.GL_COMPILE);
            ((WavefrontObject) objShWings).renderAll(true);
            GlStateManager.glEndList();
            GlStateManager.popMatrix();
        }

        texWings.bind();

        float yOffset = 1.5F;
        if (player.isSneaking()) {
            yOffset = 1.2F;
        }
        double offset = Math.sin(Math.abs((ClientScheduler.getClientTick() % 120) - 60F) / 60F) * 0.07;

        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX(), event.getY() + yOffset + offset, event.getZ());
        GlStateManager.rotate(180F - rot, 0F, 1F, 0F);
        GlStateManager.scale(0.02, 0.02, 0.02);
        GlStateManager.disableLighting();

        GlStateManager.translate(-20, 0, 0);
        GlStateManager.callList(dlList);
        GlStateManager.rotate(180F, 0F, 1F, 0F);
        GlStateManager.translate(-40, 0, 0);
        GlStateManager.callList(dlList);

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        TextureHelper.refreshTextureBindState();
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Patreon - Celestial Wings " + playerUUID.toString();
    }
}
