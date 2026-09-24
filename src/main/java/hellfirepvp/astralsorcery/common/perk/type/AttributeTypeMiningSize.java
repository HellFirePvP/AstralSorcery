/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.network.play.PktSyncCustomDestroyProgress;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.BlockUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypeMiningSize
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypeMiningSize extends PerkAttributeType {

    public AttributeTypeMiningSize() {
        super(false);
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        super.attachListeners(eventBus);
        eventBus.addListener(this::onBlockBreak);
    }

    private void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;
        if (!(event.getPlayer() instanceof ServerPlayer sPlayer)) return;

        forAllValidBreakablePositions(sLevel, sPlayer, event.getPos(), sPlayer.gameMode::destroyBlock);
    }

    public static void sendBlockBreakProgressSync(ServerLevel sLevel, ServerPlayer breaker, BlockPos pos, int progressStage) {
        if (progressStage == -1) {
            sendToAllNearby(sLevel, pos, PktSyncCustomDestroyProgress.resetProgress(breaker.getId(), pos));
            return;
        }

        forAllValidBreakablePositions(sLevel, breaker, pos, offsetPos -> {
            sendToAllNearby(sLevel, pos, PktSyncCustomDestroyProgress.destroyProgress(breaker.getId(), pos, offsetPos, progressStage));
        });
    }

    private static void forAllValidBreakablePositions(ServerLevel sLevel, ServerPlayer sPlayer, BlockPos center, Consumer<BlockPos> posFn) {
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
        if (!progress.isValid() || MiscUtil.isPlayerFake(sPlayer)) return;
        if (!PerksAS.AttributeTypes.MINING_SIZE.get().hasTypeApplied(sPlayer, LogicalSide.SERVER)) return;

        float size = PerkManager.getOrCreateAttributes(sPlayer)
                .modifyValue(sPlayer, progress, PerksAS.AttributeTypes.MINING_SIZE, 0);
        size = AttributeEvent.postProcessModded(sPlayer, PerksAS.AttributeTypes.MINING_SIZE, size);
        int miningSize = Mth.floor(size);
        if (miningSize <= 0) return;

        HitResult hitTrace = sPlayer.pick(sPlayer.blockInteractionRange(), 0F, false);
        if (!(hitTrace instanceof BlockHitResult blockHit)) return;
        if (!blockHit.getBlockPos().equals(center)) return; //We uh... hit something else? huh. wtf did the client send?

        BlockState hitState = sLevel.getBlockState(blockHit.getBlockPos());
        Direction hitDirection = blockHit.getDirection();
        float mainDestroySpeed = hitState.getDestroyProgress(sPlayer, sLevel, blockHit.getBlockPos());

        List<BlockPos> adjacentBreakProgresses = new ArrayList<>();
        if (hitDirection.getAxis().isVertical()) {
            adjacentBreakProgresses.addAll(collectPositionsHorizontal(blockHit.getBlockPos(), hitDirection, miningSize));
        } else {
            adjacentBreakProgresses.addAll(collectPositionsVertical(blockHit.getBlockPos(), hitDirection, miningSize));
        }

        WorldBorder worldBorder = sLevel.getWorldBorder();
        ItemStack heldItem = sPlayer.getMainHandItem();
        GameType gameMode = sPlayer.gameMode.getGameModeForPlayer();
        adjacentBreakProgresses.forEach(offsetPos -> {
            BlockState offsetState = sLevel.getBlockState(offsetPos);
            if (BlockUtil.isLiquidBlock(offsetState)) return;
            if (!sLevel.isInWorldBounds(offsetPos) || !worldBorder.isWithinBounds(offsetPos)) return;
            if (offsetState.getDestroySpeed(sLevel, offsetPos) < 0) return; //unbreakable anyway

            float otherDestroySpeed = offsetState.getDestroyProgress(sPlayer, sLevel, offsetPos);
            if (otherDestroySpeed * 1.15F < mainDestroySpeed) {
                return; //If it takes significantly longer to break, it's probably harder. skip
            }

            if (!heldItem.isEmpty() && !heldItem.getItem().canAttackBlock(offsetState, sLevel, offsetPos, sPlayer)) return;
            if (sPlayer.blockActionRestricted(sLevel, offsetPos, gameMode)) return;
            if (offsetState.getBlock() instanceof GameMasterBlock && !sPlayer.canUseGameMasterBlocks()) return;

            posFn.accept(offsetPos);
        });
    }

    private static List<BlockPos> collectPositionsVertical(BlockPos center, Direction dir, int size) {
        if (size <= 0) return Collections.emptyList();

        List<BlockPos> positions = new ArrayList<>();
        for (int xx = -size; xx <= size; xx++) {
            if (dir.getNormal().getX() != 0 && xx != 0) continue;
            for (int yy = -1; yy <= (size * 2 - 1); yy++) {
                if (dir.getNormal().getY() != 0 && yy != 0) continue;
                for (int zz = -size; zz <= size; zz++) {
                    if (dir.getNormal().getZ() != 0 && zz != 0) continue;
                    if (xx == 0 && yy == 0 && zz == 0) continue;

                    positions.add(center.offset(xx, yy, zz));
                }
            }
        }
        return positions;
    }

    private static List<BlockPos> collectPositionsHorizontal(BlockPos center, Direction dir, int size) {
        if (size <= 0) return Collections.emptyList();

        List<BlockPos> positions = new ArrayList<>();
        for (int xx = -size; xx <= size; xx++) {
            if (dir.getNormal().getX() != 0 && xx != 0) continue;
            for (int zz = -size; zz <= size; zz++) {
                if (dir.getNormal().getZ() != 0 && zz != 0) continue;
                if (xx == 0 && zz == 0) continue;

                positions.add(center.offset(xx, 0, zz));
            }
        }
        return positions;
    }

    private static void sendToAllNearby(ServerLevel sLevel, BlockPos pos, PktSyncCustomDestroyProgress.Request pkt) {
        PacketDistributor.sendToPlayersNear(sLevel, null, pos.getX(), pos.getY(), pos.getZ(), 32, pkt);
    }
}
