/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wand;

import hellfirepvp.astralsorcery.common.component.BlockStateStorageComponent;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.IntegerModeComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lumen.ILumenHandler;
import hellfirepvp.astralsorcery.common.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArchitectWandItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArchitectWandItem extends ItemCustom {

    public static final float LUMEN_COST_PER_BLOCK = 0.3F;

    // Base ranges (no lumen)
    private static final int BASE_LINE_LENGTH = 8;
    private static final int BASE_PLANE_RADIUS = 2;
    private static final int BASE_SPHERE_RADIUS = 2;
    private static final int BASE_HOLLOW_SPHERE_OUTER = 3;
    private static final int BASE_HOLLOW_SPHERE_INNER = 2;

    // Boosted ranges (with lumen)
    private static final int BOOSTED_LINE_LENGTH = 20;
    private static final int BOOSTED_PLANE_RADIUS = 5;
    private static final int BOOSTED_SPHERE_RADIUS = 5;
    private static final int BOOSTED_HOLLOW_SPHERE_OUTER = 5;
    private static final int BOOSTED_HOLLOW_SPHERE_INNER = 4;

    public ArchitectWandItem() {
        super(new Properties()
                .component(DataComponentsAS.BLOCK_STATE_STORAGE, BlockStateStorageComponent.EMPTY)
                .component(DataComponentsAS.MODE, IntegerModeComponent.ZERO)
                .component(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY
                        .updateProperties(properties -> properties.allowFill().allowAccept(LumenAS.AEVITAS)))
                .stacksTo(1));
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        tabItems.accept(new ItemStack(this));

        ItemStack lumenFilled = new ItemStack(this);
        LumenUtil.Storage.setStoredLumen(lumenFilled, LumenAS.AEVITAS.stack(StoredLumenComponent.DEFAULT_CAPACITY));
        IdentifierComponent.createIdentifierIfNotExists(lumenFilled);
        tabItems.accept(lumenFilled);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(getPlaceMode(stack).getDisplay().copy().withStyle(ChatFormatting.GOLD));
    }

    public static boolean hasLumenBoost(ItemStack stack, int amount) {
        return LumenUtil.drainItem(stack, LumenAS.AEVITAS.stack(amount), ILumenHandler.Action.SIMULATE).getAmount() >= amount;
    }

    private static boolean hasLumenBoost(Player player, int amount) {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof ArchitectWandItem) return hasLumenBoost(main, amount);
        ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof ArchitectWandItem) return hasLumenBoost(off, amount);
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();

        if (player == null || level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel sLevel) || !(player instanceof ServerPlayer sPlayer)) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) {
            WandBlockStorageHelper.tryStoreBlock(stack, level, pos, player);
            return InteractionResult.SUCCESS;
        }

        if (!WandBlockStorageHelper.getStorage(stack).hasStoredStates()) return InteractionResult.SUCCESS;

        PlaceMode mode = getPlaceMode(stack);
        Direction placingAgainst = context.getClickedFace();
        BlockPos placeOrigin = pos.relative(placingAgainst);

        List<BlockPos> positions = mode.generatePositions(level, player, placingAgainst, placeOrigin);
        List<BlockPos> placeablePositions = filterReplaceable(level, positions);
        attemptPlaceBlocks(sLevel, sPlayer, stack, placeablePositions);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            PlaceMode current = getPlaceMode(stack);
            PlaceMode next = current.next();
            setPlaceMode(stack, next);
            player.displayClientMessage(next.getDisplay(), true);
            return InteractionResultHolder.success(stack);
        }

        if (level instanceof ServerLevel sLevel && player instanceof ServerPlayer sPlayer) {
            if (WandBlockStorageHelper.getStorage(stack).hasStoredStates()) {
                PlaceMode mode = getPlaceMode(stack);

                BlockHitResult rtr = level.clip(RayTraceUtil.getEntityViewContext(player, WandBlockStorageHelper.MAX_RAYTRACE_DISTANCE));
                boolean hasHit = rtr.getType() != HitResult.Type.MISS;

                Direction face = hasHit ? rtr.getDirection() : null;
                BlockPos origin = hasHit ? rtr.getBlockPos().relative(rtr.getDirection()) : null;

                List<BlockPos> positions = mode.generatePositions(level, player, face, origin);
                List<BlockPos> placeablePositions = filterReplaceable(level, positions);
                attemptPlaceBlocks(sLevel, sPlayer, stack, placeablePositions);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    private List<BlockPos> filterReplaceable(Level level, List<BlockPos> positions) {
        List<BlockPos> result = new ArrayList<>();
        for (BlockPos pos : positions) {
            ChunkUtil.executeWithChunk(level, pos, () -> {
                if (BlockUtil.isReplaceable(level, pos)) {
                    result.add(pos);
                }
            });
        }
        return result;
    }

    private void attemptPlaceBlocks(ServerLevel level, ServerPlayer player, ItemStack wandStack, List<BlockPos> positions) {
        Map<BlockPos, BlockState> placeMap = WandBlockStorageHelper.buildPlaceableMap(player, wandStack, positions);
        boolean useLumen = hasLumenBoost(wandStack, Mth.ceil(LUMEN_COST_PER_BLOCK));
        int placed = 0;

        for (var entry : placeMap.entrySet()) {
            BlockPos placePos = entry.getKey();
            BlockState stateToPlace = entry.getValue();

            if (!player.mayUseItemAt(placePos, Direction.UP, wandStack)) continue;
            if (EventHooks.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, placePos), Direction.UP)) continue;

            ItemStack blockItem = new ItemStack(stateToPlace.getBlock());
            boolean hasItems = player.isCreative() || WandBlockStorageHelper.consumeBlock(player, blockItem);
            if (!hasItems) continue;

            level.setBlockAndUpdate(placePos, stateToPlace);
            placed++;
        }

        if (useLumen && placed > 0 && !player.isCreative()) {
            LumenUtil.drainItem(wandStack, LumenAS.AEVITAS.stack(Mth.ceil(placed * LUMEN_COST_PER_BLOCK)), ILumenHandler.Action.EXECUTE);
        }
    }

    public static PlaceMode getPlaceMode(ItemStack stack) {
        return stack.getOrDefault(DataComponentsAS.MODE, IntegerModeComponent.ZERO).getMode(PlaceMode.class);
    }

    public static void setPlaceMode(ItemStack stack, PlaceMode mode) {
        stack.set(DataComponentsAS.MODE, new IntegerModeComponent(mode.ordinal()));
    }

    public enum PlaceMode {

        TOWARDS_PLAYER("towards", true) {
            @Override
            public List<BlockPos> generatePositions(Level level, Player player, Direction placedAgainst, BlockPos center) {
                List<BlockPos> blocks = new ArrayList<>();
                if (center == null || placedAgainst == null) return blocks;

                boolean boosted = hasLumenBoost(player, Mth.ceil(LUMEN_COST_PER_BLOCK));
                int maxLength = boosted ? BOOSTED_LINE_LENGTH : BASE_LINE_LENGTH;

                double cmpFrom, cmpTo;
                switch (placedAgainst.getAxis()) {
                    case X -> { cmpFrom = center.getX(); cmpTo = player.getX(); }
                    case Y -> { cmpFrom = center.getY(); cmpTo = player.getY(); }
                    case Z -> { cmpFrom = center.getZ(); cmpTo = player.getZ(); }
                    default -> { return blocks; }
                }
                int length = (int) Math.min(maxLength, Math.abs(cmpFrom + 0.5 - cmpTo));
                for (int i = 0; i < length; i++) {
                    BlockPos at = center.relative(placedAgainst, i);
                    if (!BlockUtil.isReplaceable(level, at)) break;
                    blocks.add(at);
                }
                return blocks;
            }
        },
        FROM_PLAYER("line", false) {
            @Override
            public List<BlockPos> generatePositions(Level level, Player player, Direction placedAgainst, BlockPos center) {
                BlockHitResult hit = level.clip(RayTraceUtil.getEntityViewContext(player, WandBlockStorageHelper.MAX_RAYTRACE_DISTANCE));
                if (hit.getType() == HitResult.Type.MISS) return List.of();

                BlockPos target = hit.getBlockPos();
                List<BlockPos> line = new ArrayList<>();

                ClipContext ctx = new ClipContext(
                        player.position(),
                        Vec3.atCenterOf(target),
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        player);
                RayTraceUtil.clipPerPosition(level, ctx, pos -> {
                    if (BlockUtil.isReplaceable(level, pos)) {
                        line.add(pos.immutable());
                        return false;
                    }
                    return true;
                });
                return line;
            }
        },
        H_PLANE("plane", true) {
            @Override
            public List<BlockPos> generatePositions(Level level, Player player, Direction placedAgainst, BlockPos center) {
                if (center == null) return List.of();
                int radius = hasLumenBoost(player, Mth.ceil(LUMEN_COST_PER_BLOCK)) ? BOOSTED_PLANE_RADIUS : BASE_PLANE_RADIUS;
                return BlockGeometry.getPlane(Direction.UP, radius).stream()
                        .map(offset -> offset.offset(center))
                        .collect(Collectors.toList());
            }
        },
        V_PLANE("wall", true) {
            @Override
            public List<BlockPos> generatePositions(Level level, Player player, Direction placedAgainst, BlockPos center) {
                if (center == null) return List.of();
                int radius = hasLumenBoost(player, Mth.ceil(LUMEN_COST_PER_BLOCK)) ? BOOSTED_PLANE_RADIUS : BASE_PLANE_RADIUS;
                return BlockGeometry.getPlane(player.getDirection(), radius).stream()
                        .map(offset -> offset.offset(center))
                        .collect(Collectors.toList());
            }
        },
        SPHERE("sphere", true) {
            @Override
            public List<BlockPos> generatePositions(Level level, Player player, Direction placedAgainst, BlockPos center) {
                if (center == null) return List.of();
                int radius = hasLumenBoost(player, Mth.ceil(LUMEN_COST_PER_BLOCK)) ? BOOSTED_SPHERE_RADIUS : BASE_SPHERE_RADIUS;
                return BlockGeometry.getSphere(radius).stream()
                        .map(offset -> offset.offset(center))
                        .collect(Collectors.toList());
            }
        },
        SPHERE_HOLLOW("sphere_hollow", true) {
            @Override
            public List<BlockPos> generatePositions(Level level, Player player, Direction placedAgainst, BlockPos center) {
                if (center == null) return List.of();
                boolean boosted = hasLumenBoost(player, Mth.ceil(LUMEN_COST_PER_BLOCK));
                int outer = boosted ? BOOSTED_HOLLOW_SPHERE_OUTER : BASE_HOLLOW_SPHERE_OUTER;
                int inner = boosted ? BOOSTED_HOLLOW_SPHERE_INNER : BASE_HOLLOW_SPHERE_INNER;
                return BlockGeometry.getHollowSphere(outer, inner).stream()
                        .map(offset -> offset.offset(center))
                        .collect(Collectors.toList());
            }
        };

        private final String translationKey;
        private final boolean needsOffset;

        PlaceMode(String translationKey, boolean needsOffset) {
            this.translationKey = translationKey;
            this.needsOffset = needsOffset;
        }

        public boolean needsOffset() {
            return needsOffset;
        }

        public Component getName() {
            return Component.translatable("astralsorcery.misc.architect.mode." + this.translationKey);
        }

        public Component getDisplay() {
            return Component.translatable("astralsorcery.misc.architect.mode", this.getName());
        }

        public PlaceMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtil.getEnumEntry(PlaceMode.class, next);
        }

        public abstract List<BlockPos> generatePositions(Level level, Player player, Direction placedAgainst, BlockPos center);
    }
}
