/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.event.effect.GatewayUIRenderHandler;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.GatewayUI;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktRequestTeleport;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatewayInteractionHandler
 * Created by HellFirePvP
 * Date: 12.09.2020 / 22:15
 */
public class GatewayInteractionHandler {

    private static final Random rand = new Random();

    public static GatewayUI.GatewayEntry focusingEntry = null;
    public static int focusTicks = 0;

    public static void clientTick(TickEvent.ClientTickEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        World world = Minecraft.getInstance().world;
        if (player == null || world == null) {
            focusingEntry = null;
            focusTicks = 0;
            return;
        }

        GatewayUI ui = GatewayUIRenderHandler.getInstance().getCurrentUI();
        if (ui == null) {
            focusingEntry = null;
            focusTicks = 0;
            return;
        }

        //TODO
        TileCelestialGateway gateway = MiscUtils.getTileAt(world, Vector3.atEntityCorner(player).toBlockPos(), TileCelestialGateway.class, true);
        if (gateway == null || !gateway.hasMultiblock() || !gateway.doesSeeSky()) {
            focusingEntry = null;
            focusTicks = 0;
            return;
        }

        GatewayUI.GatewayEntry entry = GatewayUIRenderHandler.getInstance().
                findMatchingEntry(MathHelper.wrapDegrees(player.rotationYaw), MathHelper.wrapDegrees(player.rotationPitch));
        if (entry == null) {
            focusingEntry = null;
            focusTicks = 0;
            return;
        }

        if (!Minecraft.getInstance().gameSettings.keyBindUseItem.isKeyDown() &&
                !Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown()) {
            focusingEntry = null;
            focusTicks = 0;
            return;
        }

        if (focusingEntry != null && !entry.equals(focusingEntry)) {
            focusingEntry = null;
            focusTicks = 0;
            return;
        }

        focusingEntry = entry;
        focusTicks++;

        Vector3 dir = focusingEntry.getRelativePos().clone().add(ui.getRenderCenter()).subtract(player.getEyePosition(1F));
        Vector3 mov = dir.clone().normalize().multiply(0.25F).negate();
        Vector3 pos = focusingEntry.getRelativePos().clone().add(ui.getRenderCenter());

        if (focusTicks <= 40) {
            pos = focusingEntry.getRelativePos().clone().multiply(0.8).add(ui.getRenderCenter());

            float perc = ((float) focusTicks) / 40;
            List<Vector3> positions = MiscUtils.getCirclePositions(pos, dir.clone().negate(), rand.nextFloat() * 0.2 + 0.4, rand.nextInt(6) + 25);
            for (int i = 0; i < positions.size(); i++) {
                float pc = ((float) i) / ((float) positions.size());
                if (pc >= perc) continue;

                Color color = MiscUtils.eitherOf(rand,
                        Color.WHITE,
                        ColorsAS.EFFECT_BLUE_LIGHT,
                        ColorsAS.EFFECT_BLUE_DARK);
                Vector3 at = positions.get(i);
                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_GATEWAY_PARTICLE)
                        .spawn(at)
                        .setScaleMultiplier(0.08F)
                        .color(VFXColorFunction.constant(color));
                if (rand.nextInt(3) == 0) {
                    Vector3 to = pos.clone().subtract(at);
                    to.normalize().multiply(0.02);
                    p.setMotion(to)
                            .setAlphaMultiplier(0.1F);
                }
            }

            positions = MiscUtils.getCirclePositions(pos, dir, rand.nextFloat() * 0.2 + 0.4, rand.nextInt(6) + 25);
            Collections.reverse(positions);
            for (int i = 0; i < positions.size(); i++) {
                float pc = ((float) i) / ((float) positions.size());
                if (pc >= perc) continue;

                Color color = MiscUtils.eitherOf(rand,
                        Color.WHITE,
                        ColorsAS.EFFECT_BLUE_LIGHT,
                        ColorsAS.EFFECT_BLUE_DARK);
                Vector3 at = positions.get(i);
                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_GATEWAY_PARTICLE)
                        .spawn(at)
                        .setScaleMultiplier(0.08F)
                        .color(VFXColorFunction.constant(color));
                if (rand.nextInt(3) == 0) {
                    Vector3 to = pos.clone().subtract(at);
                    to.normalize().multiply(0.02);
                    p.setMotion(to)
                            .setAlphaMultiplier(0.1F);
                }
            }
        } else {
            for (Vector3 v : MiscUtils.getCirclePositions(pos, dir, rand.nextFloat() * 0.3 + 0.2, rand.nextInt(20) + 30)) {

                Color color = MiscUtils.eitherOf(rand,
                        Color.WHITE,
                        ColorsAS.EFFECT_BLUE_LIGHT,
                        ColorsAS.EFFECT_BLUE_DARK);
                Vector3 m = mov.clone().multiply(0.5 + rand.nextFloat() * 0.5);
                EffectHelper.of(EffectTemplatesAS.GENERIC_GATEWAY_PARTICLE)
                        .spawn(v)
                        .setScaleMultiplier(0.1F)
                        .setMotion(m)
                        .color(VFXColorFunction.constant(color));
            }
        }

        if (focusTicks > 95) {
            Minecraft.getInstance().player.setSneaking(false);
            PktRequestTeleport pkt = new PktRequestTeleport(focusingEntry.getNodeDimension(), focusingEntry.getNode().getPos());
            PacketChannel.CHANNEL.sendToServer(pkt);
            focusingEntry = null;
            focusTicks = 0;
        }
    }
}
