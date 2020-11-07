/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.auxiliary.gateway.CelestialGatewayHandler;
import hellfirepvp.astralsorcery.common.constellation.ConstellationGenerator;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.util.PlayerReference;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.object.ObjectReference;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatewayUI
 * Created by HellFirePvP
 * Date: 12.09.2020 / 09:51
 */
public class GatewayUI {

    private final ResourceLocation dimType;
    private final BlockPos pos;
    private final Vector3 renderCenter;
    private final double sphereRadius;
    private final Map<UUID, IConstellation> playerConstellations = new HashMap<>();
    private final List<GatewayEntry> gatewayEntries = new ArrayList<>();

    private int visibleTicks = 20;

    private GatewayUI(ResourceLocation dimType, BlockPos pos, Vector3 renderCenter, double sphereRadius) {
        this.dimType = dimType;
        this.pos = pos;
        this.renderCenter = renderCenter;
        this.sphereRadius = sphereRadius;
        this.initializePlayerConstellations();
    }

    private void initializePlayerConstellations() {
        if (!this.getThisGatewayNode().isLocked() || this.getThisGatewayNode().getAllowedUsers().isEmpty()) {
            return;
        }
        for (PlayerReference playerRef : this.getThisGatewayNode().getAllowedUsers().values()) {
            UUID playerUUID = playerRef.getPlayerUUID();
            long plSeed = playerUUID.getMostSignificantBits() ^ playerUUID.getLeastSignificantBits();
            Random sRand = new Random(plSeed);
            for (int i = 0; i < sRand.nextInt(5); i++) {
                sRand.nextLong();
            }
            this.playerConstellations.put(playerUUID, ConstellationGenerator.generateRandom(sRand.nextLong()));
        }
    }

    public static GatewayUI create(IWorld world, BlockPos tilePos, Vector3 renderPos, double sphereRadius) {
        GatewayCache.GatewayNode gatewayNode = CelestialGatewayHandler.INSTANCE.getGatewayNode(world, LogicalSide.CLIENT, tilePos);
        if (gatewayNode == null) {
            return null;
        }
        ResourceLocation dimType = world.getDimension().getType().getRegistryName();
        GatewayUI ui = new GatewayUI(dimType, tilePos, renderPos, sphereRadius);
        PlayerEntity thisPlayer = Minecraft.getInstance().player;

        for (GatewayCache.GatewayNode node : CelestialGatewayHandler.INSTANCE.getGatewaysForWorld(world, LogicalSide.CLIENT)) {
            if (!node.hasAccess(thisPlayer)) {
                continue;
            }
            appendEntry(ui, node, dimType, true, sphereRadius);
        }

        CelestialGatewayHandler.INSTANCE.getGatewayCache(LogicalSide.CLIENT).forEach((dimTypeKey, gatewayNodes) -> {
            if (dimTypeKey.equals(dimType)) {
                return;
            }
            for (GatewayCache.GatewayNode node : gatewayNodes) {
                if (!node.hasAccess(thisPlayer)) {
                    continue;
                }
                appendEntry(ui, node, dimTypeKey, false, sphereRadius);
            }
        });
        return ui;
    }

    private static void appendEntry(GatewayUI ui, GatewayCache.GatewayNode node, ResourceLocation nodeDimType, boolean sameWorld, double sphereRadius) {
        Vector3 renderPos = ui.getRenderCenter();

        Vector3 nodePos = new Vector3(node.getPos());
        if (sameWorld) {
            if (renderPos.distance(nodePos) < 16) {
                return;
            }

            Vector3 dir = nodePos.subtract(renderPos);
            dir.setY(Math.max(dir.getY(), 0));
            Vector3 sphereDirection = dir.normalize().multiply(sphereRadius);
            GatewayEntry entry = new GatewayEntry(node, nodeDimType, sphereDirection);
            ObjectReference<GatewayEntry> overlapping = new ObjectReference<>(null);
            ui.gatewayEntries.removeIf(otherEntry -> {
                if (Math.abs(otherEntry.pitch - entry.pitch) < 7 &&
                        (Math.abs(otherEntry.yaw - entry.yaw) <= 7 || Math.abs(otherEntry.yaw - entry.yaw - 360F) <= 7)) {

                    if (renderPos.distanceSquared(entry.getRelativePos()) < renderPos.distanceSquared(otherEntry.getRelativePos())) {
                        return true;
                    } else {
                        overlapping.set(otherEntry);
                        return false;
                    }
                }
                return false;
            });
            if (overlapping.get() == null) {
                ui.gatewayEntries.add(entry);
            }
        } else {
            long seed = 0xA7401CE1466A1095L;
            seed |= ((long) node.getPos().getX()) << 48;
            seed |= ((long) node.getPos().getY()) << 24;
            seed |= ((long) node.getPos().getZ());
            Random rand = new Random(seed);
            Vector3 direction = Vector3.positiveYRandom(rand).normalize().multiply(sphereRadius);
            GatewayEntry entry = new GatewayEntry(node, nodeDimType, direction);
            int tries = 50;
            boolean foundSpace = false;
            while (!foundSpace && tries > 0) {

                boolean mayAdd = true;
                for (GatewayEntry otherEntry : ui.gatewayEntries) {
                    if (Math.abs(otherEntry.pitch - entry.pitch) < 15 &&
                            (Math.abs(otherEntry.yaw - entry.yaw) <= 15 || Math.abs(otherEntry.yaw - entry.yaw - 360F) <= 15)) {
                        mayAdd = false;
                        break;
                    }
                }
                if(mayAdd) {
                    foundSpace = true;
                } else {
                    direction = Vector3.positiveYRandom(rand).normalize().multiply(sphereRadius);
                    entry = new GatewayEntry(node, nodeDimType, direction);
                }
                tries--;
            }
            if(foundSpace) {
                ui.gatewayEntries.add(entry);
            }
        }
    }

    public ResourceLocation getDimType() {
        return dimType;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Vector3 getRenderCenter() {
        return renderCenter;
    }

    public double getSphereRadius() {
        return sphereRadius;
    }

    @Nullable
    public GatewayCache.GatewayNode getThisGatewayNode() {
        return CelestialGatewayHandler.INSTANCE.getGatewayNode(Minecraft.getInstance().world, LogicalSide.CLIENT, this.getPos());
    }

    @Nullable
    public IConstellation getGeneratedConstellation(UUID playerUUID) {
        return this.playerConstellations.get(playerUUID);
    }

    public List<GatewayEntry> getGatewayEntries() {
        return Collections.unmodifiableList(gatewayEntries);
    }

    public void refreshView() {
        this.visibleTicks = 20;
    }

    public int getVisibleTicks() {
        return visibleTicks;
    }

    public void decrementVisibleTicks() {
        this.visibleTicks--;
    }

    public static class GatewayEntry {

        private final GatewayCache.GatewayNode node;
        private final ResourceLocation nodeDimension;

        private final Vector3 relativePos;

        private final float yaw, pitch;

        private GatewayEntry(GatewayCache.GatewayNode node, ResourceLocation nodeDimension, Vector3 relativePos) {
            this.node = node;
            this.nodeDimension = nodeDimension;
            this.relativePos = relativePos.clone();
            if(this.relativePos.getY() < 0) {
                this.relativePos.setY(0);
            }

            Vector3 angles = relativePos.copyToPolar();
            this.yaw = (float) (180F - angles.getZ());
            this.pitch = Math.min(0F, (float) (-90F + angles.getY()));
        }

        public GatewayCache.GatewayNode getNode() {
            return node;
        }

        public ResourceLocation getNodeDimension() {
            return nodeDimension;
        }

        public Vector3 getRelativePos() {
            return relativePos;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GatewayEntry that = (GatewayEntry) o;
            return Objects.equals(node, that.node) &&
                    Objects.equals(nodeDimension, that.nodeDimension);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, nodeDimension);
        }
    }
}
