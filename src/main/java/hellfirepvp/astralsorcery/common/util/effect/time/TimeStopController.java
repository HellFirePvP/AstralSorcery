/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryPotions;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.dragon.phase.PhaseList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopController
 * Created by HellFirePvP
 * Date: 17.10.2017 / 22:07
 */
public class TimeStopController implements ITickHandler {

    private Map<Integer, List<TimeStopZone>> activeTimeStopZones = new HashMap<>();

    public static TimeStopController INSTANCE = new TimeStopController();

    private TimeStopController() {}

    @Nullable
    public static TimeStopZone tryGetZoneAt(World world, BlockPos pos) {
        if(world.isRemote) return null;
        int dimId = world.provider.getDimension();
        List<TimeStopZone> zones = INSTANCE.activeTimeStopZones.get(dimId);
        if(zones == null || zones.isEmpty()) return null;
        for (TimeStopZone zone : zones) {
            if(zone.offset.equals(pos)) {
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
     * @param reducedParticles set to true to reduce the amount of particles spawned by the effect
     * @return null if the world's provider is null, otherwise a registered and running instance of the timeStopEffect
     */
    @Nullable
    public static TimeStopZone freezeWorldAt(@Nonnull TimeStopZone.EntityTargetController controller, @Nonnull World world, @Nonnull BlockPos offset, boolean reducedParticles, float range, int maxAge) {
        if(world.provider == null) return null;

        TimeStopZone stopZone = new TimeStopZone(controller, range, offset, world, maxAge, reducedParticles);
        int dimId = world.provider.getDimension();
        List<TimeStopZone> zones = INSTANCE.activeTimeStopZones.computeIfAbsent(dimId, (id) -> new LinkedList<>());
        zones.add(stopZone);
        ((DataTimeFreezeEffects) SyncDataHolder.getData(Side.SERVER, SyncDataHolder.DATA_TIME_FREEZE_EFFECTS))
                .server_addNewEffect(dimId, TimeStopEffectHelper.fromZone(stopZone));
        return stopZone;
    }

    @SubscribeEvent
    public void onWorldClear(WorldEvent.Unload event) {
        World w = event.getWorld();
        if(w != null && w.provider != null) {
            int id = w.provider.getDimension();
            List<TimeStopZone> freezeAreas = activeTimeStopZones.get(id);
            if(freezeAreas != null && !freezeAreas.isEmpty()) {
                for (TimeStopZone stop : freezeAreas) {
                    stop.stopEffect();
                }
            }
            ((DataTimeFreezeEffects) SyncDataHolder.getData(Side.SERVER, SyncDataHolder.DATA_TIME_FREEZE_EFFECTS))
                    .server_clearEffects(id);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingTickTest(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase e = event.getEntityLiving();
        if(e.isPotionActive(RegistryPotions.potionTimeFreeze)) {
            boolean shouldFreeze = true;
            if(e.isDead || e.getHealth() <= 0) {
                shouldFreeze = false;
            }
            if(e instanceof EntityDragon && ((EntityDragon) e).getPhaseManager().getCurrentPhase().getType() == PhaseList.DYING) {
                shouldFreeze = false;
            }
            if(shouldFreeze) {
                if(e.world.isRemote && e.world.rand.nextInt(5) == 0) {
                    TimeStopEffectHelper.playEntityParticles(e);
                }
                TimeStopZone.handleImportantEntityTicks(e);
                event.setCanceled(true);
                return;
            }
        }
        World w = e.world;
        if(w != null && w.provider != null) {
            int id = w.provider.getDimension();
            List<TimeStopZone> freezeAreas = activeTimeStopZones.get(id);
            if(freezeAreas != null && !freezeAreas.isEmpty()) {
                for (TimeStopZone stop : freezeAreas) {
                    if(stop.interceptEntityTick(e)) {
                        TimeStopZone.handleImportantEntityTicks(e);
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        for (Map.Entry<Integer, List<TimeStopZone>> zoneMap : activeTimeStopZones.entrySet()) {
            for (Iterator<TimeStopZone> iterator = zoneMap.getValue().iterator(); iterator.hasNext();) {
                TimeStopZone zone = iterator.next();
                if(zone.shouldDespawn()) { //If this was requested outside of the tick logic. Prevents potentially unwanted ticks.
                    zone.stopEffect();
                    ((DataTimeFreezeEffects) SyncDataHolder.getData(Side.SERVER, SyncDataHolder.DATA_TIME_FREEZE_EFFECTS))
                            .server_removeEffect(zoneMap.getKey(), TimeStopEffectHelper.fromZone(zone));
                    iterator.remove();
                    continue;
                }
                zone.onServerTick();
                if(zone.shouldDespawn()) {
                    zone.stopEffect();
                    ((DataTimeFreezeEffects) SyncDataHolder.getData(Side.SERVER, SyncDataHolder.DATA_TIME_FREEZE_EFFECTS))
                            .server_removeEffect(zoneMap.getKey(), TimeStopEffectHelper.fromZone(zone));
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
