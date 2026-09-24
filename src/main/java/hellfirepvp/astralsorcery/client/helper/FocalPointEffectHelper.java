/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.focal.observer.FocusCrystalFilamentObserver;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.RayTraceUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointEffectHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointEffectHelper {

    public static void onClientTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().isPaused()) return;

        ClientLevel level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (level != null && player != null) {
            double maxEffectDistance = (Minecraft.getInstance().options.getEffectiveRenderDistance() * 2) * 16;

            Vec3 playerPosition = player.position();
            SyncDataManager.getInstance().getClientData(SyncDataTypesAS.FOCAL_POINT)
                    .getNodes(level, new Vector3(player), maxEffectDistance)
                    .stream()
                    .filter(node -> node.getPos().distSqr(playerPosition) < maxEffectDistance * maxEffectDistance)
                    .forEach(node -> node.tickEffects(level));
        }
    }

    public static HitResult overrideStellarFilamentInteract(HitResult defaultResult) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return defaultResult;
        if (player.getMainHandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT) || player.getOffhandItem().is(ItemsAS.BLOCK_STELLAR_FILAMENT)) {
            Entity tracingEntity = Minecraft.getInstance().getCameraEntity();
            if (tracingEntity != null) {
                ClipContext ctx = RayTraceUtil.getEntityViewContext(tracingEntity, player.blockInteractionRange());
                HitResult filteredResult = RayTraceUtil.clipPerPosition(tracingEntity.level(), ctx, isInFocalPointArea());

                if (defaultResult.getType() == HitResult.Type.MISS) {
                    return filteredResult;
                }
                double defaultDist = defaultResult.getLocation().distanceToSqr(ctx.getFrom());
                double filteredDist = filteredResult.getLocation().distanceToSqr(ctx.getFrom()) + 4;
                if (filteredDist <= defaultDist) {
                    return filteredResult;
                } else {
                    return defaultResult;
                }
            }
        }
        return defaultResult;
    }

    public static boolean isInFocalPointArea(BlockPos pos) {
        return isInFocalPointArea().test(pos);
    }

    public static Predicate<BlockPos> isInFocalPointArea() {
        Entity tracingEntity = Minecraft.getInstance().getCameraEntity();
        if (tracingEntity == null) return pos -> false;
        Player player = Minecraft.getInstance().player;
        if (player == null) return pos -> false;

        int boxSize = FocusCrystalFilamentObserver.OBSERVED_AREA_RADIUS * 2 + 1;
        ClipContext ctx = RayTraceUtil.getEntityViewContext(tracingEntity, player.blockInteractionRange());
        List<AABB> clipBoxes = SyncDataManager.getInstance().getClientData(SyncDataTypesAS.FOCAL_POINT)
                .getNearbyFocusedNodes(player.level(), new Vector3(player), player.blockInteractionRange() + 6)
                .stream()
                .map(node -> AABB.ofSize(node.getFocalPosition().get().getCenter(), boxSize, boxSize, boxSize))
                .toList();
        return pos -> {
            if (ctx.getFrom().distanceToSqr(pos.getCenter()) < 10) return false;
            for (AABB box : clipBoxes) {
                if (box.contains(Vec3.atCenterOf(pos))) return true;
            }
            return false;
        };
    }
}
