/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import hellfirepvp.astralsorcery.common.component.BlockStateStorageComponent;
import hellfirepvp.astralsorcery.common.component.IntegerModeComponent;
import hellfirepvp.astralsorcery.common.item.wand.ArchitectWandItem;
import hellfirepvp.astralsorcery.common.item.wand.ExchangeWandItem;
import hellfirepvp.astralsorcery.common.item.wand.WandBlockStorageHelper;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.util.BlockFinder;
import hellfirepvp.astralsorcery.common.util.BlockUtil;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.RayTraceUtil;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: WandPreviewRenderHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class WandPreviewRenderHelper {

    // Shared with wand items
    private static final int RAYTRACE_DISTANCE = WandBlockStorageHelper.MAX_RAYTRACE_DISTANCE;

    private static List<BlockPos> cachedPositions = Collections.emptyList();
    @Nullable
    private static BlockPos cachedTarget = null;
    @Nullable
    private static Direction cachedFace = null;
    @Nullable
    private static BlockPos cachedPlayerPos = null;
    private static int cachedModeOrdinal = -1;

    private static final ResourceLocation TEX_ITEM_FRAME = AstralSorcery.key("textures/screen/overlay/item_frame.png");
    private static final ResourceLocation TEX_ITEM_FRAME_EXT = AstralSorcery.key("textures/screen/overlay/item_frame_extension.png");
    private static final int OVERLAY_X = 30;
    private static final int OVERLAY_Y = 15;
    private static final int FRAME_WIDTH = 26;
    private static final int FRAME_HALF_HEIGHT = 13;
    private static final int FRAME_FULL_HEIGHT = 26;

    public static void attachEventListeners(IEventBus eventBus) {
        eventBus.addListener(WandPreviewRenderHelper::onRenderLevelStage);
        eventBus.addListener(WandPreviewRenderHelper::onRenderGuiLayer);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack held = findWandInHand(player);
        if (held.isEmpty()) {
            clearCache();
            return;
        }

        BlockStateStorageComponent storage = WandBlockStorageHelper.getStorage(held);
        if (!storage.hasStoredStates()) {
            clearCache();
            return;
        }
        int modeOrdinal = held.getOrDefault(DataComponentsAS.MODE, IntegerModeComponent.ZERO).mode();

        Level level = player.level();
        Vec3 cameraPos = event.getCamera().getPosition();

        BlockHitResult hitResult = level.clip(RayTraceUtil.getEntityViewContext(player, RAYTRACE_DISTANCE));
        boolean hasHit = hitResult.getType() != HitResult.Type.MISS;

        List<BlockPos> positions = computePositions(held, player, level, hitResult, hasHit, storage, modeOrdinal);

        boolean isExchange = held.getItem() instanceof ExchangeWandItem;
        if (isExchange) {
            // For exchange wand, use scaled overlay to avoid z-fighting with existing blocks
            Map<BlockPos, BlockState> placeMap = WandBlockStorageHelper.buildPreviewMap(player, held, positions, level);
            for (var entry : placeMap.entrySet()) {
                TranslucentBlockRenderHelper.submitBlockOverlay(DyeColor.WHITE, entry.getValue(), entry.getKey(), cameraPos);
            }
        } else {
            // For architect wand, filter to replaceable then show the placement map
            List<BlockPos> replaceable = new ArrayList<>();
            for (BlockPos pos : positions) {
                ChunkUtil.executeWithChunk(level, pos, () -> {
                    if (BlockUtil.isReplaceable(level, pos)) {
                        replaceable.add(pos);
                    }
                });
            }
            Map<BlockPos, BlockState> placeMap = WandBlockStorageHelper.buildPreviewMap(player, held, replaceable, level);
            for (var entry : placeMap.entrySet()) {
                TranslucentBlockRenderHelper.submitBlockState(DyeColor.WHITE, entry.getValue(), entry.getKey(), cameraPos);
            }
        }
    }

    private static List<BlockPos> computePositions(ItemStack held, Player player, Level level,
                                                    BlockHitResult hitResult, boolean hasHit,
                                                    BlockStateStorageComponent storage, int modeOrdinal) {
        BlockPos targetPos = hasHit ? hitResult.getBlockPos() : null;
        Direction targetFace = hasHit ? hitResult.getDirection() : null;
        BlockPos playerPos = player.blockPosition();

        boolean cacheValid = modeOrdinal == cachedModeOrdinal
                && playerPos.equals(cachedPlayerPos)
                && targetFace == cachedFace
                && (targetPos == null ? cachedTarget == null : targetPos.equals(cachedTarget));

        if (cacheValid) {
            return cachedPositions;
        }

        cachedTarget = targetPos;
        cachedFace = targetFace;
        cachedPlayerPos = playerPos;
        cachedModeOrdinal = modeOrdinal;

        List<BlockPos> positions;
        if (held.getItem() instanceof ArchitectWandItem) {
            positions = computeArchitectPositions(held, player, level, hitResult, hasHit, storage);
        } else if (held.getItem() instanceof ExchangeWandItem) {
            positions = computeExchangePositions(held, player, level, hitResult, hasHit, storage);
        } else {
            positions = Collections.emptyList();
        }

        cachedPositions = positions;
        return positions;
    }

    private static List<BlockPos> computeArchitectPositions(ItemStack held, Player player, Level level,
                                                             BlockHitResult hitResult, boolean hasHit,
                                                             BlockStateStorageComponent storage) {
        ArchitectWandItem.PlaceMode mode = ArchitectWandItem.getPlaceMode(held);
        if (mode.needsOffset() && !hasHit) return Collections.emptyList();

        BlockPos placeOrigin = hasHit ? hitResult.getBlockPos().relative(hitResult.getDirection()) : null;
        Direction face = hasHit ? hitResult.getDirection() : null;

        List<BlockPos> positions = mode.generatePositions(level, player, face, placeOrigin);

        int totalAvailable = WandBlockStorageHelper.countTotalAvailable(player, held);
        if (positions.size() > totalAvailable) {
            positions = positions.subList(0, totalAvailable);
        }

        return positions;
    }

    private static List<BlockPos> computeExchangePositions(ItemStack held, Player player, Level level,
                                                            BlockHitResult hitResult, boolean hasHit,
                                                            BlockStateStorageComponent storage) {
        if (!hasHit) return Collections.emptyList();

        BlockPos origin = hitResult.getBlockPos();
        BlockState targetState = level.getBlockState(origin);

        Map<BlockState, WandBlockStorageHelper.InventoryEntry> inventoryMap = WandBlockStorageHelper.getInventoryMatching(player, held);
        boolean targetIsStored = inventoryMap.keySet().stream().anyMatch(s -> s.getBlock() == targetState.getBlock());
        if (targetIsStored && inventoryMap.size() <= 1) return Collections.emptyList();

        float hardness = targetState.getDestroySpeed(level, origin);
        if (hardness < 0 || hardness > ExchangeWandItem.MAX_EXCHANGE_HARDNESS) return Collections.emptyList();

        ExchangeWandItem.SizeMode mode = ExchangeWandItem.getSizeMode(held);
        boolean lumenBoosted = ExchangeWandItem.hasLumenBoost(held, 1);
        int totalAvailable = WandBlockStorageHelper.countTotalAvailable(player, held);

        int maxBlocks = Math.min(totalAvailable, mode.getMaxBlocks(lumenBoosted));
        return BlockFinder.findConnectedBlocksWithSameState(
                level, origin, true, -1, maxBlocks, false);
    }

    private static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack held = findWandInHand(player);
        if (held.isEmpty()) return;

        BlockStateStorageComponent storage = WandBlockStorageHelper.getStorage(held);
        if (!storage.hasStoredStates()) return;

        Map<BlockState, WandBlockStorageHelper.InventoryEntry> inventoryMap = WandBlockStorageHelper.getInventoryMatching(player, held);
        if (inventoryMap.isEmpty()) return;

        GuiGraphics graphics = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;
        List<WandBlockStorageHelper.InventoryEntry> entries = new ArrayList<>(inventoryMap.values());

        graphics.enableScissor(0, 0, graphics.guiWidth(), graphics.guiHeight());

        // Draw frame backgrounds
        int y = OVERLAY_Y;
        for (int i = 0; i < entries.size(); i++) {
            boolean first = i == 0;
            boolean last = i == entries.size() - 1;

            if (first) {
                // Upper cap (top half of frame texture)
                graphics.blit(TEX_ITEM_FRAME, OVERLAY_X, y, 0, 0, FRAME_WIDTH, FRAME_HALF_HEIGHT, FRAME_WIDTH, FRAME_FULL_HEIGHT);
                y += FRAME_HALF_HEIGHT;
            } else {
                // Middle extension (connects previous slot bottom to this slot top)
                graphics.blit(TEX_ITEM_FRAME_EXT, OVERLAY_X, y, 0, 0, FRAME_WIDTH, FRAME_FULL_HEIGHT, FRAME_WIDTH, FRAME_FULL_HEIGHT);
                y += FRAME_FULL_HEIGHT;
            }

            if (last) {
                // Lower cap (bottom half of frame texture)
                graphics.blit(TEX_ITEM_FRAME, OVERLAY_X, y, 0, FRAME_HALF_HEIGHT, FRAME_WIDTH, FRAME_HALF_HEIGHT, FRAME_WIDTH, FRAME_FULL_HEIGHT);
                y += FRAME_HALF_HEIGHT;
            }
        }

        graphics.disableScissor();

        // Draw item icons and counts
        int itemY = OVERLAY_Y;
        for (int i = 0; i < entries.size(); i++) {
            WandBlockStorageHelper.InventoryEntry entry = entries.get(i);
            ItemStack displayStack = entry.itemStack();
            int count = entry.count();

            int iconY = itemY + 5;

            graphics.renderItem(displayStack, OVERLAY_X + 5, iconY);

            String countStr = count >= Integer.MAX_VALUE ? "\u221E" : String.valueOf(count);
            int textX = OVERLAY_X + 14;
            int textY = iconY + 12;

            graphics.pose().pushPose();
            graphics.pose().translate(textX, textY, 500);
            graphics.pose().scale(0.7F, 0.7F, 1F);
            graphics.drawString(font, countStr, 0, 0, 0xDDDDDD, true);
            graphics.pose().popPose();

            itemY += FRAME_FULL_HEIGHT;
        }
    }

    private static ItemStack findWandInHand(Player player) {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof ArchitectWandItem || main.getItem() instanceof ExchangeWandItem) {
            return main;
        }
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof ArchitectWandItem || off.getItem() instanceof ExchangeWandItem) {
            return off;
        }
        return ItemStack.EMPTY;
    }

    private static void clearCache() {
        cachedPositions = Collections.emptyList();
        cachedTarget = null;
        cachedFace = null;
        cachedPlayerPos = null;
        cachedModeOrdinal = -1;
    }
}
