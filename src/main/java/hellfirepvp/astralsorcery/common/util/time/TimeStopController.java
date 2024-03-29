/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientTimeFreezeEntities;
import hellfirepvp.astralsorcery.common.data.sync.server.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.data.sync.server.DataTimeFreezeEntities;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopController
 * Created by HellFirePvP
 * Date: 31.08.2019 / 13:53
 */
public class TimeStopController implements ITickHandler {

    private static final Map<RegistryKey<World>, List<TimeStopZone>> activeTimeStopZones = new HashMap<>();

    public static final TimeStopController INSTANCE = new TimeStopController();

    private TimeStopController() {}

    @Nullable
    public static TimeStopZone tryGetZoneAt(World world, BlockPos pos) {
        if (world.isRemote) {
            return null;
        }
        List<TimeStopZone> zones = activeTimeStopZones.getOrDefault(world.getDimensionKey(), Collections.emptyList());
        for (TimeStopZone zone : zones) {
            if (zone.offset.equals(pos)) {
                return zone;
            }
        }
        return null;
    }

    /**
     * Stops Tile- & Entityticks in a specific region of a world
     *
     * @param controller a target controller to determine how to handle/filter entity freezing
     * @param world to spawn the timeFreeze in
     * @param offset the center position
     * @param range the range of the timeFreeze effect
     * @param maxAge the duration in ticks
     * @return null if the world's provider is null, otherwise a registered and running instance of the timeStopEffect
     */
    @Nonnull
    public static TimeStopZone freezeWorldAt(@Nonnull TimeStopZone.EntityTargetController controller, @Nonnull World world, @Nonnull BlockPos offset, float range, int maxAge) {
        TimeStopZone stopZone = new TimeStopZone(controller, range, offset, world, maxAge);
        List<TimeStopZone> zones = activeTimeStopZones.computeIfAbsent(world.getDimensionKey(), (id) -> new LinkedList<>());
        zones.add(stopZone);

        SyncDataHolder.executeServer(SyncDataHolder.DATA_TIME_FREEZE_EFFECTS, DataTimeFreezeEffects.class, data -> {
            data.addNewEffect(world.getDimensionKey(), TimeStopEffectHelper.fromZone(stopZone));
        });
        return stopZone;
    }

    public static void onWorldUnload(World world) {
        if (world.isRemote()) {
            return;
        }

        RegistryKey<World> dimKey = world.getDimensionKey();
        for (TimeStopZone stop : activeTimeStopZones.getOrDefault(dimKey, Collections.emptyList())) {
            stop.stopEffect();
        }
        activeTimeStopZones.remove(dimKey);
    }

    public static boolean isFrozenDirectly(Entity e) {
        if (e.getEntityWorld().isRemote()) {
            return SyncDataHolder.computeClient(SyncDataHolder.DATA_TIME_FREEZE_ENTITIES, ClientTimeFreezeEntities.class, data -> data.isFrozen(e)).orElse(false);
        } else {
            return SyncDataHolder.computeServer(SyncDataHolder.DATA_TIME_FREEZE_ENTITIES, DataTimeFreezeEntities.class, data -> data.isFrozen(e)).orElse(false);
        }
    }

    public static boolean skipLivingTick(LivingEntity e) {
        if (isFrozenDirectly(e)) {
            boolean shouldFreeze = true;
            if (!e.isAlive() || e.getHealth() <= 0) {
                shouldFreeze = false;
            }
            if (e instanceof EnderDragonEntity && ((EnderDragonEntity) e).getPhaseManager().getCurrentPhase().getType() == PhaseType.DYING) {
                shouldFreeze = false;
            }
            if (shouldFreeze) {
                if (e.world.isRemote()) {
                    int amt = (int) MathHelper.sqrt(e.getWidth() * e.getHeight());
                    for (int i = 0; i < amt; i++) {
                        if (e.world.rand.nextInt(5) == 0) {
                            TimeStopEffectHelper.playEntityParticles(e);
                        }
                    }
                }
                if (!e.getEntityWorld().isRemote()) {
                    TimeStopZone.handleImportantEntityTicks(e);
                    return true;
                }
            }
        }
        List<TimeStopZone> freezeAreas = activeTimeStopZones.get(e.getEntityWorld().getDimensionKey());
        if (freezeAreas != null && !freezeAreas.isEmpty()) {
            for (TimeStopZone stop : freezeAreas) {
                if (stop.interceptEntityTick(e)) {
                    if (!e.getEntityWorld().isRemote()) {
                        TimeStopZone.handleImportantEntityTicks(e);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        for (Map.Entry<RegistryKey<World>, List<TimeStopZone>> zoneMap : activeTimeStopZones.entrySet()) {
            Iterator<TimeStopZone> iterator = zoneMap.getValue().iterator();
            while (iterator.hasNext()) {
                TimeStopZone zone = iterator.next();
                if (zone.shouldDespawn()) { //If this was requested outside of the tick logic. Prevents potentially unwanted ticks.
                    zone.stopEffect();
                    SyncDataHolder.executeServer(SyncDataHolder.DATA_TIME_FREEZE_EFFECTS, DataTimeFreezeEffects.class, data -> {
                        data.removeEffect(zoneMap.getKey(), TimeStopEffectHelper.fromZone(zone));
                    });
                    iterator.remove();
                    continue;
                }
                zone.onServerTick();
                if (zone.shouldDespawn()) {
                    zone.stopEffect();
                    SyncDataHolder.executeServer(SyncDataHolder.DATA_TIME_FREEZE_EFFECTS, DataTimeFreezeEffects.class, data -> {
                        data.removeEffect(zoneMap.getKey(), TimeStopEffectHelper.fromZone(zone));
                    });
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase.equals(TickEvent.Phase.START);
    }

    @Override
    public String getName() {
        return "TimeStop Controller";
    }
}
