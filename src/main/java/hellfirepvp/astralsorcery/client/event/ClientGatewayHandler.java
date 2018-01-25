/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.ClientScreenshotCache;
import hellfirepvp.astralsorcery.client.util.UIGateway;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRequestTeleport;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.data.WorldBlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientGatewayHandler
 * Created by HellFirePvP
 * Date: 19.04.2017 / 10:39
 */
public class ClientGatewayHandler {

    public static UIGateway.GatewayEntry focusingEntry = null;
    public static int focusTicks = 0;

    private static int screenshotCooldown = 0;
    private static WorldBlockPos lastScreenshotPos = null;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(screenshotCooldown > 0) {
            screenshotCooldown--;
            if(screenshotCooldown <= 0) {
                lastScreenshotPos = null;
                screenshotCooldown = 0;
            }
        }

        UIGateway ui = EffectHandler.getInstance().getUiGateway();
        if(ui != null) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            TileCelestialGateway gate = MiscUtils.getTileAt(player.world, Vector3.atEntityCorner(player).toBlockPos(), TileCelestialGateway.class, true);
            if(gate != null && gate.hasMultiblock() && gate.doesSeeSky()) {
                if(lastScreenshotPos != null) {
                    WorldBlockPos currentPos = new WorldBlockPos(gate);
                    if(!lastScreenshotPos.equals(currentPos)) {
                        lastScreenshotPos = null;
                        screenshotCooldown = 0;
                    }
                } else {
                    captureScreenshot(gate);
                }

                UIGateway.GatewayEntry entry = ui.findMatchingEntry(MathHelper.wrapDegrees(player.rotationYaw), MathHelper.wrapDegrees(player.rotationPitch));
                if(entry == null) {
                    focusingEntry = null;
                    focusTicks = 0;
                } else {
                    if(!Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown() && !Minecraft.getMinecraft().player.isSneaking()) {
                        focusTicks = 0;
                        focusingEntry = null;
                    } else {
                        if(focusingEntry != null) {
                            if(!entry.equals(focusingEntry)) {
                                focusingEntry = null;
                                focusTicks = 0;
                            } else {
                                focusTicks++;
                            }
                        } else {
                            focusingEntry = entry;
                            focusTicks = 0;
                        }
                    }
                }
            } else {
                focusingEntry = null;
                focusTicks = 0;
            }
        } else {
            focusingEntry = null;
            focusTicks = 0;
        }

        if(focusingEntry != null) {
            Vector3 dir = focusingEntry.relativePos.clone().add(ui.getPos()).subtract(Vector3.atEntityCorner(Minecraft.getMinecraft().player).addY(1.62));
            Vector3 mov = dir.clone().normalize().multiply(0.25F).negate();
            Vector3 pos = focusingEntry.relativePos.clone().add(ui.getPos());
            if(focusTicks > 40) {
                for (Vector3 v : MiscUtils.getCirclePositions(pos, dir, EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.3 + 0.2, EffectHandler.STATIC_EFFECT_RAND.nextInt(20) + 30)) {
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                    Vector3 m = mov.clone().multiply(0.5 + EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.5);
                    p.gravity(0.004).scale(0.1F).motion(
                            m.getX(),
                            m.getY(),
                            m.getZ());
                    switch (EffectHandler.STATIC_EFFECT_RAND.nextInt(4)) {
                        case 0:
                            p.setColor(Color.WHITE);
                            break;
                        case 1:
                            p.setColor(new Color(0x69B5FF));
                            break;
                        case 2:
                            p.setColor(new Color(0x0078FF));
                            break;
                    }
                }
            } else {
                pos = focusingEntry.relativePos.clone().multiply(0.8).add(ui.getPos());
                float perc = ((float) focusTicks) / 40;
                List<Vector3> positions = MiscUtils.getCirclePositions(pos, dir.clone().negate(), EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.2 + 0.4, EffectHandler.STATIC_EFFECT_RAND.nextInt(6) + 25);
                for (int i = 0; i < positions.size(); i++) {
                    float pc = ((float) i) / ((float) positions.size());
                    if(pc >= perc) continue;

                    Vector3 v = positions.get(i);
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                    p.gravity(0.004).scale(0.08F);
                    if(EffectHandler.STATIC_EFFECT_RAND.nextInt(3) == 0) {
                        Vector3 to = pos.clone().subtract(v);
                        to.normalize().multiply(0.02);
                        p.motion(to.getX(), to.getY(), to.getZ()).setAlphaMultiplier(0.1F);
                    }
                    switch (EffectHandler.STATIC_EFFECT_RAND.nextInt(4)) {
                        case 0:
                            p.setColor(Color.WHITE);
                            break;
                        case 1:
                            p.setColor(new Color(0x69B5FF));
                            break;
                        case 2:
                            p.setColor(new Color(0x0078FF));
                            break;
                    }
                }
                positions = MiscUtils.getCirclePositions(pos, dir, EffectHandler.STATIC_EFFECT_RAND.nextFloat() * 0.2 + 0.4, EffectHandler.STATIC_EFFECT_RAND.nextInt(6) + 25);
                Collections.reverse(positions);
                for (int i = 0; i < positions.size(); i++) {
                    float pc = ((float) i) / ((float) positions.size());
                    if(pc >= perc) continue;

                    Vector3 v = positions.get(i);
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(v.getX(), v.getY(), v.getZ());
                    p.gravity(0.004).scale(0.08F);
                    if(EffectHandler.STATIC_EFFECT_RAND.nextInt(3) == 0) {
                        Vector3 to = pos.clone().subtract(v);
                        to.normalize().multiply(0.02);
                        p.motion(to.getX(), to.getY(), to.getZ()).setAlphaMultiplier(0.1F);
                    }
                    switch (EffectHandler.STATIC_EFFECT_RAND.nextInt(4)) {
                        case 0:
                            p.setColor(Color.WHITE);
                            break;
                        case 1:
                            p.setColor(new Color(0x69B5FF));
                            break;
                        case 2:
                            p.setColor(new Color(0x0078FF));
                            break;
                    }
                }
            }

            if(focusTicks > 95) { //Time explained below
                PacketChannel.CHANNEL.sendToServer(new PktRequestTeleport(focusingEntry.originalDimId, focusingEntry.originalBlockPos.up()));
                focusTicks = 0;
                focusingEntry = null;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void captureScreenshot(TileCelestialGateway gate) {
        ResourceLocation gatewayScreenshot = ClientScreenshotCache.tryQueryTextureFor(gate.getWorld().provider.getDimension(), gate.getPos());
        if(gatewayScreenshot == null && Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.rotationPitch <= 0 &&
                Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().renderGlobal.getRenderedChunks() > 200) {
            screenshotCooldown = 10;
            lastScreenshotPos = new WorldBlockPos(gate);

            ClientScreenshotCache.takeViewScreenshotFor(gate.getWorld().provider.getDimension(), gate.getPos());
        }
    }

    //40 circle, 40 portal, 15 drag

    private float fovPre = 70F;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void onRenderTransform(TickEvent.RenderTickEvent event) {
        UIGateway ui = EffectHandler.getInstance().getUiGateway();
        if(ui != null) {
            if(event.phase == TickEvent.Phase.START) {
                fovPre = Minecraft.getMinecraft().gameSettings.fovSetting;
                if(focusTicks < 80) {
                    return;
                }
                float percDone = 1F - ((focusTicks - 80F + event.renderTickTime) / 15F);
                float targetFov = 10F;
                float diff = fovPre - targetFov;
                Minecraft.getMinecraft().gameSettings.fovSetting = Math.max(targetFov, targetFov + diff * percDone);
            } else {
                Minecraft.getMinecraft().gameSettings.fovSetting = fovPre;
            }
        }
    }

}
