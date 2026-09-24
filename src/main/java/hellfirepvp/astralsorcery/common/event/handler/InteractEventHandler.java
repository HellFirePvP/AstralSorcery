/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import hellfirepvp.astralsorcery.common.container.provider.ContainerTomePapersProvider;
import hellfirepvp.astralsorcery.common.item.TomeItem;
import hellfirepvp.astralsorcery.common.item.base.InterceptInteractItem;
import hellfirepvp.astralsorcery.common.item.wand.ArchitectWandItem;
import hellfirepvp.astralsorcery.common.item.wand.ExchangeWandItem;
import hellfirepvp.astralsorcery.common.item.wand.WandBlockStorageHelper;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.play.PktOpenClientScreen;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InteractEventHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InteractEventHandler {

    public static void attachListeners(IEventBus bus) {
        bus.addListener(InteractEventHandler::onBlockInteract);
        bus.addListener(InteractEventHandler::onEntityInteract);
        bus.addListener(EventPriority.LOW, InteractEventHandler::onOpenLectern);
        bus.addListener(InteractEventHandler::onLeftClickBlock);
        bus.addListener(InteractEventHandler::onLeftClickEmpty);
    }

    private static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.getItem() instanceof InterceptInteractItem.Block blockInteractItem) {
            if (blockInteractItem.shouldInterceptBlockInteract(event.getSide(), event.getEntity(), event.getHand(), event.getPos(), event.getHitVec(), event.getFace()) &&
                    blockInteractItem.doBlockInteract(event.getSide(), event.getEntity(), event.getHand(), event.getPos(), event.getHitVec(), event.getFace())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
        MiscUtil.getTileAt(event.getLevel(), event.getPos(), LecternBlockEntity.class, false).ifPresent(tile -> {
            if (tile.getBook().is(ItemsAS.TOME)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);

                if (event.getEntity() instanceof ServerPlayer sPlayer) {
                    PacketDistributor.sendToPlayer(sPlayer, PktOpenClientScreen.openScreen(PktOpenClientScreen.ScreenType.TOME));
                }
            }
        });
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack held = event.getItemStack();
        if (held.getItem() instanceof InterceptInteractItem.Entity<?> entityInteractItem && entityInteractItem.getEntityFilterClass().isInstance(event.getTarget())) {
            if (entityInteract(entityInteractItem, MiscUtil.cast(event.getTarget()), event.getSide(), event.getEntity(), event.getHand())) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    private static <T extends Entity> boolean entityInteract(InterceptInteractItem.Entity<T> interactItem, T entity, LogicalSide side, Player interacter, InteractionHand hand) {
        return interactItem.shouldInterceptEntityInteract(side, interacter, hand, entity) &&
                interactItem.doEntityInteract(side, interacter, hand, entity);
    }

    private static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getEntity().isShiftKeyDown()) return;
        tryClearWandStorage(event.getEntity(), event.getItemStack());
    }

    private static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (!event.getEntity().isShiftKeyDown()) return;
        tryClearWandStorage(event.getEntity(), event.getItemStack());
    }

    private static void tryClearWandStorage(Player player, ItemStack held) {
        if (held.isEmpty()) return;
        if (!(held.getItem() instanceof ArchitectWandItem) && !(held.getItem() instanceof ExchangeWandItem)) return;
        if (!WandBlockStorageHelper.getStorage(held).hasStoredStates()) return;

        WandBlockStorageHelper.clearStorage(held);
        player.displayClientMessage(Component.translatable("message.astralsorcery.wand.cleared"), true);
    }

    private static void onOpenLectern(PlayerInteractEvent.RightClickBlock event) {
        MiscUtil.getTileAt(event.getLevel(), event.getPos(), LecternBlockEntity.class, false).ifPresent(lectern -> {
            ItemStack contained = lectern.getBook();
            if (contained.is(ItemsAS.TOME)) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                if (event.getLevel().isClientSide()) {
                    openTomeScreen();
                }
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void openTomeScreen() {
        Minecraft.getInstance().setScreen(TomeResearchScreen.getOpenTome());
    }
}
