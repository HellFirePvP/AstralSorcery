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
import hellfirepvp.astralsorcery.common.util.BlockFinder;
import hellfirepvp.astralsorcery.common.util.LumenUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.RayTraceUtil;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ExchangeWandItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ExchangeWandItem extends ItemCustom {

    public static final float LUMEN_COST_PER_BLOCK = 0.15F;

    public static final float MAX_EXCHANGE_HARDNESS = 50F;

    public ExchangeWandItem() {
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
        tooltipComponents.add(getSizeMode(stack).getDisplay().copy().withStyle(ChatFormatting.GOLD));
    }

    public static boolean hasLumenBoost(ItemStack stack, int amount) {
        return LumenUtil.drainItem(stack, LumenAS.AEVITAS.stack(amount), ILumenHandler.Action.SIMULATE).getAmount() >= amount;
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

        tryExchangeAt(sLevel, sPlayer, stack, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            SizeMode current = getSizeMode(stack);
            SizeMode next = current.next();
            setSizeMode(stack, next);
            player.displayClientMessage(next.getDisplay(), true);
            return InteractionResultHolder.success(stack);
        }

        if (level instanceof ServerLevel sLevel && player instanceof ServerPlayer sPlayer) {
            BlockHitResult rtr = level.clip(RayTraceUtil.getEntityViewContext(player, WandBlockStorageHelper.MAX_RAYTRACE_DISTANCE));
            if (rtr.getType() != HitResult.Type.MISS) {
                tryExchangeAt(sLevel, sPlayer, stack, rtr.getBlockPos());
            }
        }
        return InteractionResultHolder.success(stack);
    }

    private void tryExchangeAt(ServerLevel level, ServerPlayer player, ItemStack stack, BlockPos pos) {
        BlockStateStorageComponent storage = WandBlockStorageHelper.getStorage(stack);
        if (!storage.hasStoredStates()) return;

        BlockState targetState = level.getBlockState(pos);

        List<BlockState> storedStates = storage.storedStates();
        boolean targetIsStored = storedStates.stream().anyMatch(s -> s.getBlock() == targetState.getBlock());
        if (targetIsStored && storedStates.size() <= 1) return;

        float hardness = targetState.getDestroySpeed(level, pos);
        if (hardness < 0 || hardness > MAX_EXCHANGE_HARDNESS) return;

        SizeMode mode = getSizeMode(stack);
        boolean lumenBoosted = hasLumenBoost(stack, Mth.ceil(LUMEN_COST_PER_BLOCK));
        int totalAvailable = WandBlockStorageHelper.countTotalAvailable(player, stack);
        int maxBlocks = Math.min(totalAvailable, mode.getMaxBlocks(lumenBoosted));
        List<BlockPos> discovered = BlockFinder.findConnectedBlocksWithSameState(
                level, pos, true, -1, maxBlocks, false);

        attemptExchangeBlocks(level, player, stack, discovered);
    }

    private void attemptExchangeBlocks(ServerLevel level, ServerPlayer player, ItemStack wandStack, List<BlockPos> positions) {
        Map<BlockPos, BlockState> placeMap = WandBlockStorageHelper.buildPlaceableMap(player, wandStack, positions);
        boolean useLumen = hasLumenBoost(wandStack, Mth.ceil(LUMEN_COST_PER_BLOCK));
        int exchanged = 0;

        for (var entry : placeMap.entrySet()) {
            BlockPos exchangePos = entry.getKey();
            BlockState replacementState = entry.getValue();

            BlockState existingState = level.getBlockState(exchangePos);
            if (existingState.isAir()) continue;
            if (!player.mayUseItemAt(exchangePos, Direction.UP, wandStack)) continue;

            ItemStack blockItem = new ItemStack(replacementState.getBlock());
            boolean hasItems = player.isCreative() || WandBlockStorageHelper.consumeBlock(player, blockItem);
            if (!hasItems) continue;

            if (!player.gameMode.destroyBlock(exchangePos)) continue;

            if (EventHooks.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, exchangePos), Direction.UP)) continue;

            level.setBlockAndUpdate(exchangePos, replacementState);
            exchanged++;
        }

        if (useLumen && exchanged > 0 && !player.isCreative()) {
            LumenUtil.drainItem(wandStack, LumenAS.AEVITAS.stack(Mth.ceil(exchanged * LUMEN_COST_PER_BLOCK)), ILumenHandler.Action.EXECUTE);
        }
    }

    public static SizeMode getSizeMode(ItemStack stack) {
        return stack.getOrDefault(DataComponentsAS.MODE, IntegerModeComponent.ZERO).getMode(SizeMode.class);
    }

    public static void setSizeMode(ItemStack stack, SizeMode mode) {
        stack.set(DataComponentsAS.MODE, new IntegerModeComponent(mode.ordinal()));
    }

    public enum SizeMode {

        RANGE_2(2, 16, 64),
        RANGE_3(3, 32, 128),
        RANGE_4(4, 64, 256),
        RANGE_5(5, 128, 512);

        private final int searchRadius;
        private final int baseMaxBlocks;
        private final int boostedMaxBlocks;

        SizeMode(int searchRadius, int baseMaxBlocks, int boostedMaxBlocks) {
            this.searchRadius = searchRadius;
            this.baseMaxBlocks = baseMaxBlocks;
            this.boostedMaxBlocks = boostedMaxBlocks;
        }

        public int getSearchRadius() {
            return searchRadius;
        }

        public int getMaxBlocks(boolean lumenBoosted) {
            return lumenBoosted ? boostedMaxBlocks : baseMaxBlocks;
        }

        public Component getName() {
            return Component.translatable("astralsorcery.misc.exchange.size." + this.searchRadius);
        }

        public Component getDisplay() {
            return Component.translatable("astralsorcery.misc.exchange.size", this.getName());
        }

        public SizeMode next() {
            int next = (this.ordinal() + 1) % values().length;
            return MiscUtil.getEnumEntry(SizeMode.class, next);
        }
    }
}
