/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.VFXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.network.play.PktRequestGatewayTeleport;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GatewayInterfaceInteractHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GatewayInterfaceInteractHelper {

    private static final RandomSource rand = RandomSource.create();

    private static final int TELEPORT_FOCUS_START = 25;
    private static final int TELEPORT_FOCUS_WINDUP = 50;
    private static final int TELEPORT_TRIGGER = 65;

    private static GatewayUserInterface.GatewayTarget focusedTarget = null;
    private static int focusTick = 0;

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(GatewayInterfaceInteractHelper::onClientInteractTick);
        bus.addListener(GatewayInterfaceInteractHelper::onFovModifier);
    }

    private static void onClientInteractTick(ClientTickEvent.Post event) {
        if (!focusTarget()) return;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        GatewayUserInterface ui = GatewayInterfaceRenderHelper.getInstance().getCurrentUI().orElseThrow();
        ColorWrapper color = ColorsAS.DYE_COLORS[focusedTarget.getEntry().getColor().getId()];

        Vector3 entryPos = focusedTarget.getRelativePos().copy().add(ui.getRenderPos());
        Vector3 effectCenter = focusedTarget.getRelativePos().copy().multiply(0.9F).add(ui.getRenderPos());
        Vector3 dir = entryPos.copy().subtract(camera.getPosition());
        Vector3 mov = dir.copy().normalize().multiply(0.25F).negate();

        float radius = rand.nextFloat() * 0.3F + 0.3F;
        if (focusTick <= TELEPORT_FOCUS_START) {
            float percRun = focusTick / (float) TELEPORT_FOCUS_START;

            int count = rand.nextInt(10) + 30;
            List<Vector3> positions = VectorUtil.getCircleOffsets(effectCenter, dir.copy().negate(), radius, count);
            for (int i = 0; i < positions.size(); i++) {
                float circlePart = i / (float) positions.size();
                if (circlePart >= percRun) continue;

                Vector3 at = positions.get(i);
                ColorWrapper effectColor = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, color, color.brighter()).orElseThrow();
                VFXFacingParticle p = EffectHelper.of(EffectTemplatesAS.COLOR_SPHERE_PARTICLE)
                        .spawn(at)
                        .setScale(0.1F)
                        .color(FXColorFunction.constant(effectColor));
                if (rand.nextFloat() <= percRun * 0.3F) {
                    Vector3 dirCenter = entryPos.copy().subtract(at).normalize().multiply(0.03F);
                    p.setAlpha(0.25F).setMotion(dirCenter);
                }
            }

            positions = VectorUtil.getCircleOffsets(effectCenter, dir, radius, count);
            Collections.reverse(positions);
            for (int i = 0; i < positions.size(); i++) {
                float circlePart = i / (float) positions.size();
                if (circlePart >= percRun) continue;

                Vector3 at = positions.get(i);
                ColorWrapper effectColor = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, color, color.brighter()).orElseThrow();
                VFXFacingParticle p = EffectHelper.of(EffectTemplatesAS.COLOR_SPHERE_PARTICLE)
                        .spawn(at)
                        .setScale(0.1F)
                        .color(FXColorFunction.constant(effectColor));
                if (rand.nextFloat() <= percRun * 0.3F) {
                    Vector3 dirCenter = entryPos.copy().subtract(at).normalize().multiply(0.03F);
                    p.setAlpha(0.25F).setMotion(dirCenter);
                }
            }
        }
        if (focusTick > TELEPORT_FOCUS_START) {
            VectorUtil.getCircleOffsets(effectCenter, dir, radius * 0.9F, rand.nextInt(20) + 30).forEach(v -> {
                ColorWrapper effectColor = MiscUtil.getRandomEntry(rand, ColorWrapper.WHITE, color, color.brighter()).orElseThrow();
                EffectHelper.of(EffectTemplatesAS.COLOR_SPHERE_PARTICLE)
                        .spawn(v)
                        .color(FXColorFunction.constant(effectColor))
                        .setScale(0.1F)
                        .setMotion(mov.copy().multiply(0.5 + rand.nextFloat() * 0.5F));
            });
        }

        if (focusTick == TELEPORT_TRIGGER) {
            Minecraft.getInstance().player.setShiftKeyDown(false);
            PacketDistributor.sendToServer(PktRequestGatewayTeleport.teleportTo(focusedTarget.getEntryLevelKey(), focusedTarget.getEntry().getPos()));
        }
    }

    private static boolean focusTarget() {
        Player player = Minecraft.getInstance().player;
        Level level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            resetInteract();
            return false;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        GatewayUserInterface ui = GatewayInterfaceRenderHelper.getInstance().getCurrentUI().orElse(null);
        if (ui == null) {
            resetInteract();
            return false;
        }

        TileCelestialGateway gateway = MiscUtil.getTileAt(level, player.blockPosition(), TileCelestialGateway.class, true).orElse(null);
        if (gateway == null || !gateway.hasStructure() || !gateway.doesSeeSky()) {
            resetInteract();
            return false;
        }

        GatewayUserInterface.GatewayTarget matchingTarget = GatewayInterfaceRenderHelper.getInstance()
                .findMatching(camera.getYRot(), camera.getXRot()).orElse(null);
        if (matchingTarget == null) {
            resetInteract();
            return false;
        }

        if (!Minecraft.getInstance().options.keyUse.isDown() && !Minecraft.getInstance().options.keyShift.isDown()) {
            resetInteract();
            return false;
        }

        if (focusedTarget != null && !focusedTarget.equals(matchingTarget)) {
            resetInteract();
        }

        focusedTarget = matchingTarget;
        focusTick++;
        return true;
    }

    private static void resetInteract() {
        focusedTarget = null;
        focusTick = 0;
    }

    private static void onFovModifier(ComputeFovModifierEvent event) {
        GatewayInterfaceRenderHelper.getInstance().getCurrentUI().ifPresent(currentUI -> {
            if (focusTick < TELEPORT_FOCUS_WINDUP) return;
            float focusZoomTick = focusTick - TELEPORT_FOCUS_WINDUP + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
            int frame = TELEPORT_TRIGGER - TELEPORT_FOCUS_WINDUP;
            float percZoom = 1F - (focusZoomTick / frame);
            percZoom = Mth.clamp(percZoom, 0F, 1F);
            percZoom = Mth.sqrt(percZoom);
            float targetModifier = 0.9F;
            float modifier = (1F - targetModifier) + (targetModifier * percZoom);
            event.setNewFovModifier(event.getNewFovModifier() * modifier);
        });
    }
}
