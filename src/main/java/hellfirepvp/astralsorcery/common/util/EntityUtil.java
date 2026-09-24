/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityUtil {

    private static final Random rand = new Random();

    public static Optional<Player> getPlayer(UUID playerUUID, LogicalSide side) {
        return side.isClient() ? getPlayerClient(playerUUID) : getPlayerServer(playerUUID);
    }

    public static Optional<Player> getPlayerServer(UUID playerUUID) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(server.getPlayerList().getPlayer(playerUUID));
    }

    @OnlyIn(Dist.CLIENT)
    public static Optional<Player> getPlayerClient(UUID playerUUID) {
        ClientLevel clWorld = Minecraft.getInstance().level;
        if (clWorld == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(clWorld.getPlayerByUUID(playerUUID));
    }

    public static <T> Optional<T> selectClosest(Collection<T> elements, Function<T, Double> dstFunction) {
        if (elements.isEmpty()) {
            return Optional.empty();
        }
        T closest = null;
        double closestDst = Double.MAX_VALUE;
        for (T element : elements) {
            double dst = dstFunction.apply(element);
            if (dst < closestDst) {
                closestDst = dst;
                closest = element;
            }
        }
        return Optional.ofNullable(closest);
    }

    @Nullable
    public static <T extends LivingEntity> T spawnLivingEntity(ServerLevel sLevel, EntityType<?> type, Vector3 pos) {
        T entity;
        try {
            entity = (T) type.create(sLevel);
        } catch (Exception exc) {
            return null;
        }
        if (entity == null) return null;

        entity.moveTo(pos.getX(), pos.getY(), pos.getZ(), sLevel.random.nextFloat() * 360F, 0F);
        sLevel.addFreshEntityWithPassengers(entity);
        return entity;
    }

    public static boolean canEntityFit(Level level, EntityType<?> type, BlockPos pos) {
        return level.noBlockCollision(null, type.getSpawnAABB(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
    }

    public static <T extends Entity> T transferEntity(T entity, Vector3 targetPos) {
        return transferEntity(entity, entity.level().dimension(), targetPos);
    }

    public static <T extends Entity> T transferEntity(T entity, ResourceKey<Level> targetLevel, Vector3 targetPos) {
        if (!(entity.level() instanceof ServerLevel srcLevel)) return entity;
        MinecraftServer server = srcLevel.getServer();
        EntityTeleportEvent event = new EntityTeleportEvent(entity, targetPos.getX(), targetPos.getY(), targetPos.getZ());
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return entity;
        double x = event.getTargetX();
        double y = event.getTargetY();
        double z = event.getTargetZ();

        entity.setShiftKeyDown(false);
        ServerLevel dstLevel = server.getLevel(targetLevel);
        if (dstLevel == null) return entity;
        if (!Level.isInSpawnableBounds(targetPos.toBlockPos())) return entity;

        float yaw = Mth.wrapDegrees(entity.getYRot());
        float pitch = Mth.wrapDegrees(entity.getXRot());
        if (entity.teleportTo(dstLevel, x, y, z, Set.of(), yaw, pitch)) {
            if (entity instanceof PathfinderMob mob) {
                mob.getNavigation().stop();
            }
        }
        return entity;
    }
}
