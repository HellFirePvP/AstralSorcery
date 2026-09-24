/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import hellfirepvp.astralsorcery.common.data.level.CelestialGatewayData;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.data.sync.client.CelestialGatewayClientData;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GatewayUserInterface
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GatewayUserInterface {

    private final ResourceKey<Level> levelKey;
    private final BlockPos pos;
    private final Vector3 renderPos;
    private final float sphereRadius;

    //TODO prevent addition of targets around directly 'up'
    private final List<GatewayTarget> gatewayTargets = new ArrayList<>();

    private int visibleTicks = 20;

    private GatewayUserInterface(ResourceKey<Level> levelKey, BlockPos pos, Vector3 renderPos, float sphereRadius) {
        this.levelKey = levelKey;
        this.pos = pos;
        this.renderPos = renderPos;
        this.sphereRadius = sphereRadius;
    }

    @Nullable
    public static GatewayUserInterface create(Level level, BlockPos tilePos, Vector3 renderPos, float sphereRadius) {
        ResourceKey<Level> levelKey = level.dimension();
        CelestialGatewayClientData data = SyncDataManager.getInstance().getClientData(SyncDataTypesAS.CELESTIAL_GATEWAY);
        return data.getEntry(levelKey, tilePos).map(entry -> {
            GatewayUserInterface ui = new GatewayUserInterface(levelKey, tilePos, renderPos, sphereRadius);
            data.getKnownLevels().forEach(otherLevelKey -> {
                data.getEntries(otherLevelKey).forEach((otherPos, otherEntry) -> {
                    if (otherLevelKey.equals(levelKey)) {
                        appendEntrySameLevel(ui, otherLevelKey, otherEntry);
                    } else {
                        appendEntryOtherLevel(ui, otherLevelKey, otherEntry);
                    }
                });
            });
            return ui;
        }).orElse(null);
    }

    private static void appendEntrySameLevel(GatewayUserInterface ui,
                                             ResourceKey<Level> entryLevelKey,
                                             CelestialGatewayData.GatewayEntry entry) {
        Vector3 thisRenderPos = ui.getRenderPos();
        Vector3 otherNodePos = Vector3.atCenter(entry.getPos());

        if (thisRenderPos.distanceSquared(otherNodePos) < 256) { // 16 * 16
            return;
        }

        Vector3 dir = otherNodePos.copy().subtract(thisRenderPos);
        dir.setY(Math.max(dir.getY(), 0));
        Vector3 sphereDir = dir.copy().normalize().multiply(ui.getSphereRadius());
        GatewayTarget newTarget = new GatewayTarget(entryLevelKey, entry, sphereDir);

        Iterator<GatewayTarget> iterator = ui.gatewayTargets.iterator();
        while (iterator.hasNext()) {
            GatewayTarget otherTarget = iterator.next();
            if (Math.abs(otherTarget.pitch - newTarget.pitch) < 7 &&
                    (Math.abs(otherTarget.yaw - newTarget.yaw) <= 7 || Math.abs(otherTarget.yaw - newTarget.yaw - 360F) <= 7)) {

                if (!otherTarget.entryLevelKey.equals(newTarget.entryLevelKey)) {
                    iterator.remove();
                    continue;
                }
                if (ui.pos.distSqr(newTarget.entry.getPos()) < ui.pos.distSqr(otherTarget.entry.getPos())) {
                    iterator.remove();
                    continue;
                }

                // Another entry in the same world + closer than new one, skip adding the new one.
                return;
            }
        }
        ui.gatewayTargets.add(newTarget);
    }

    private static void appendEntryOtherLevel(GatewayUserInterface ui,
                                              ResourceKey<Level> entryLevelKey,
                                              CelestialGatewayData.GatewayEntry entry) {
        long seed = MiscUtil.getBlockPosSeed(entry.getPos());
        int keyHash = entryLevelKey.toString().hashCode();
        seed = seed ^ ((long) keyHash * 0x9E3779B97F4A7C15L);
        RandomSource rand = RandomSource.create(seed);

        GatewayTarget newTarget;
        int attempts = 50;
        while (attempts > 0) {
            Vector3 sphereDir = Vector3.positiveYRandom(rand).normalize().multiply(ui.getSphereRadius());
            newTarget = new GatewayTarget(entryLevelKey, entry, sphereDir);

            boolean mayAdd = true;
            for (GatewayTarget otherTarget : ui.gatewayTargets) {
                if (Math.abs(otherTarget.pitch - newTarget.pitch) < 15 &&
                        (Math.abs(otherTarget.yaw - newTarget.yaw) <= 15 || Math.abs(otherTarget.yaw - newTarget.yaw - 360F) <= 15)) {
                    mayAdd = false;
                    break;
                }
            }
            if (mayAdd) {
                ui.gatewayTargets.add(newTarget);
                return;
            }
            attempts--;
        }
    }

    public ResourceKey<Level> getLevelKey() {
        return this.levelKey;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Vector3 getRenderPos() {
        return this.renderPos.copy();
    }

    public double getSphereRadius() {
        return this.sphereRadius;
    }

    public List<GatewayTarget> getGatewayTargets() {
        return Collections.unmodifiableList(this.gatewayTargets);
    }

    public void refreshView() {
        this.visibleTicks = 20;
    }

    public boolean isViewVisible() {
        return this.visibleTicks > 0;
    }

    public void tick() {
        this.visibleTicks--;
    }

    public static class GatewayTarget {

        private final ResourceKey<Level> entryLevelKey;
        private final CelestialGatewayData.GatewayEntry entry;

        private final Vector3 relativePos;

        private final float yaw, pitch;

        private GatewayTarget(ResourceKey<Level> entryLevelKey,
                              CelestialGatewayData.GatewayEntry entry,
                              Vector3 relativePos) {
            this.entryLevelKey = entryLevelKey;
            this.entry = entry;
            this.relativePos = relativePos.copy();
            if (this.relativePos.getY() < 0) {
                this.relativePos.setY(0);
            }

            Vector3 angles = this.relativePos.copyToPolar();
            this.yaw = 180F - (float) angles.getZ();
            this.pitch = -90F + (float) angles.getY();
        }

        public CelestialGatewayData.GatewayEntry getEntry() {
            return this.entry;
        }

        public ResourceKey<Level> getEntryLevelKey() {
            return this.entryLevelKey;
        }

        public Vector3 getRelativePos() {
            return this.relativePos.copy();
        }

        public float getYaw() {
            return this.yaw;
        }

        public float getPitch() {
            return this.pitch;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            GatewayTarget that = (GatewayTarget) o;
            return Objects.equals(entryLevelKey, that.entryLevelKey) && Objects.equals(entry, that.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entryLevelKey, entry);
        }
    }
}
