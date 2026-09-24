/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal;

import hellfirepvp.astralsorcery.common.component.AstrolabeAngleComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.level.LevelSkyHandler;
import hellfirepvp.astralsorcery.common.data.level.FocalPointData;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.data.sync.server.FocalPointSyncData;
import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.focal.node.FocalPointNode;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.ResearchMessageHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.RayTraceUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointManager {

    private static final FocalPointManager INSTANCE = new FocalPointManager();

    private FocalPointManager() {}

    public static FocalPointManager getInstance() {
        return INSTANCE;
    }

    private FocalPointSyncData getData() {
        return SyncDataManager.getInstance().getData(SyncDataTypesAS.FOCAL_POINT);
    }

    public void addNewNode(ServerLevel level, FocalPointNode node, boolean loadNode) {
        DataAS.DOMAIN_AS.getData(level, DataAS.KEY_FOCAL_POINT_DATA).addNode(node);
        if (loadNode) {
            this.getData().loadNode(level, node);
        }
    }

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(this::onLevelTick);
        bus.addListener(this::onPlayerTick);
        bus.addListener(this::onChunkLoad);
        bus.addListener(this::onChunkUnload);
    }

    private void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel sLevel) {
            this.getData().getNodes(sLevel.dimension()).forEach(node -> {
                node.tick(sLevel);
            });
        }
    }

    private void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer && AstrolabeItem.isUsingAstrolabe(sPlayer)) {
            if (sPlayer.serverLevel().getGameTime() % 10 == 0) {
                ItemStack held = sPlayer.getUseItem();
                if (held.isEmpty()) return;
                LevelSkyHandler.getContext(sPlayer.serverLevel()).ifPresent(ctx -> {
                    FocalPointData focalPointData = DataAS.DOMAIN_AS.getData(sPlayer.serverLevel(), DataAS.KEY_FOCAL_POINT_DATA);
                    PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
                    float angle = held.getOrDefault(DataComponentsAS.ASTROLABE_ANGLE, AstrolabeAngleComponent.DEFAULT).angle();
                    RegistriesAS.REGISTRY_CONSTELLATIONS.getTag(TagsAS.Constellations.MAY_BE_FOCAL_POINT).ifPresent(set -> {
                        set.forEach(cstHolder -> {
                            BaseConstellation cst = cstHolder.value();
                            if (!progress.hasSeenFocalPoint(cst) && Math.abs(angle - ctx.getConstellationHandler().getAngle(cst)) < 6F) {
                                if (this.isPlayerLookingAtFocalPoint(sPlayer, focalPointData, cst)) {
                                    if (ResearchHelper.memorizeFocalPoint(sPlayer, cst)) {
                                        ResearchMessageHelper.sendConstellationFocalPointDiscovery(sPlayer, cst, progress.hasDiscoveredConstellation(cst));
                                    }
                                }
                            }
                        });
                    });
                });

            }
        }
    }

    private boolean isPlayerLookingAtFocalPoint(ServerPlayer player, FocalPointData focalPointData, BaseConstellation constellation) {
        Vec3 playerEyePos = player.getEyePosition();
        Vector3 viewDir = Vector3.directionFromYawPitch(player.getYRot(), player.getXRot()).normalize();
        float playerYaw = player.getYRot();
        float playerPitch = player.getXRot();

        List<FocalPointNode> nearbyNodes = focalPointData.getNodesNear(player.blockPosition(), 384);
        for (FocalPointNode node : nearbyNodes) {
            if (!node.getConstellation().equals(constellation)) {
                continue;
            }

            BlockPos groundPos = node.getPos().toBlockPos(0);
            int surfaceY = player.serverLevel().getHeight(Heightmap.Types.WORLD_SURFACE, groundPos.getX(), groundPos.getZ());

            Vector3 targetPos = this.findBestFocalPosition(playerEyePos, viewDir, groundPos, surfaceY, player.serverLevel());

            Vector3 toTarget = targetPos.copy().subtract(playerEyePos);
            double deltaX = toTarget.getX();
            double deltaY = toTarget.getY();
            double deltaZ = toTarget.getZ();
            double horizontalDist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            float targetYaw = (float) Math.toDegrees(Math.atan2(-deltaX, deltaZ));
            float targetPitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDist));

            float yawDiff = Math.abs(Mth.wrapDegrees(targetYaw - playerYaw));
            float pitchDiff = Math.abs(Mth.wrapDegrees(targetPitch - playerPitch));

            if (yawDiff < 3F && pitchDiff < 5F) {
                ClipContext context = new ClipContext(
                        playerEyePos,
                        targetPos.toVector3d(),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player
                );
                HitResult result = RayTraceUtil.clip(player.serverLevel(), context);
                if (result.getType() == HitResult.Type.MISS) {
                    return true;
                }
            }
        }
        return false;
    }

    private Vector3 findBestFocalPosition(Vec3 playerEyePos, Vector3 viewDir, BlockPos groundPos, int surfaceY, ServerLevel level) {
        double centerX = groundPos.getX() + 0.5;
        double centerZ = groundPos.getZ() + 0.5;

        double t = ((centerX - playerEyePos.x) * viewDir.getX() + (centerZ - playerEyePos.z) * viewDir.getZ())
                / (viewDir.getX() * viewDir.getX() + viewDir.getZ() * viewDir.getZ());

        if (t < 0) {
            t = 0;
        }

        double targetY = playerEyePos.y + t * viewDir.getY();
        targetY = Mth.clamp(targetY, surfaceY + 1, Math.min(surfaceY + 384, level.getMaxBuildHeight()));

        return new Vector3(centerX, targetY, centerZ);
    }

    private void onChunkLoad(ChunkEvent.Load event) {
        if (event.getChunk() instanceof LevelChunk && event.getLevel() instanceof ServerLevel sLevel) {
            ChunkPos pos = event.getChunk().getPos();
            FocalPointData.Section sectionData = DataAS.DOMAIN_AS.getData(sLevel, DataAS.KEY_FOCAL_POINT_DATA).getSection(pos.getWorldPosition());
            if (sectionData != null) {

                MiscUtil.schedule(sLevel.getServer(), () -> {
                    this.getData().loadNodes(sLevel, sectionData.getFocalPoints().stream()
                            .filter(node -> new ChunkPos(node.getPos().toBlockPos(0)).equals(pos))
                            .toList());
                });
            }
        }
    }

    private void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getChunk() instanceof LevelChunk && event.getLevel() instanceof ServerLevel sLevel) {
            ChunkPos pos = event.getChunk().getPos();

            this.getData().unloadNodes(sLevel, pos);
        }
    }
}
