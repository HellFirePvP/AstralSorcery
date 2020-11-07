/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.item.base.OverrideInteractItem;
import hellfirepvp.astralsorcery.common.tile.base.TileOwned;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerInteract
 * Created by HellFirePvP
 * Date: 24.08.2019 / 15:58
 */
public class EventHandlerInteract {

    private EventHandlerInteract() {}

    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(EventHandlerInteract::onBlockInteract);
        eventBus.addListener(EventHandlerInteract::onEntityInteract);
        eventBus.addListener(EventHandlerInteract::onSinglePlace);
        eventBus.addListener(EventHandlerInteract::onMultiPlace);
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack held = event.getItemStack();
        if (held.getItem() instanceof OverrideInteractItem) {
            OverrideInteractItem item = (OverrideInteractItem) held.getItem();
            if (item.shouldInterceptEntityInteract(event.getSide(), event.getPlayer(), event.getHand(), event.getTarget()) &&
                    item.doEntityInteract(event.getSide(), event.getPlayer(), event.getHand(), event.getTarget())) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    private static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.getItem() instanceof OverrideInteractItem) {
            OverrideInteractItem item = (OverrideInteractItem) held.getItem();
            if (item.shouldInterceptBlockInteract(event.getSide(), event.getPlayer(), event.getHand(), event.getPos(), event.getFace()) &&
                    item.doBlockInteract(event.getSide(), event.getPlayer(), event.getHand(), event.getPos(), event.getFace())) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    private static void onSinglePlace(BlockEvent.EntityPlaceEvent event) {
        if (event instanceof BlockEvent.EntityMultiPlaceEvent) {
            return; //Handled 1 method below.
        }
        IWorld world = event.getWorld();
        if (world.isRemote() || !(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        handleOwnerPlacement(world, event.getPos(), (PlayerEntity) event.getEntity());
    }

    private static void onMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        IWorld world = event.getWorld();
        if (world.isRemote() || !(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity placer = (PlayerEntity) event.getEntity();
        for (BlockSnapshot snapshot : event.getReplacedBlockSnapshots()) {
            handleOwnerPlacement(world, snapshot.getPos(), placer);
        }
    }

    private static void handleOwnerPlacement(IWorld world, BlockPos pos, PlayerEntity placer) {
        TileOwned owned = MiscUtils.getTileAt(world, pos, TileOwned.class, true);
        if (owned != null) {
            owned.setOwner(placer);
        }
    }
}
