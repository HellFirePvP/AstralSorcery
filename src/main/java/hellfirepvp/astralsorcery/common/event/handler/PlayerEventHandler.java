/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.block.tile.AltarBlock;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.config.server.GeneralConfig;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.event.InventoryChangeEvent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.network.play.*;
import hellfirepvp.astralsorcery.common.research.*;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.RecipeUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayerEventHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PlayerEventHandler {

    public static void attachListeners(IEventBus bus) {
        bus.addListener(PlayerEventHandler::onLogin);
        bus.addListener(PlayerEventHandler::onResearchDiscover);
    }

    private static void onResearchDiscover(InventoryChangeEvent event) {
        ItemStack stack = event.getNewStack();
        ServerPlayer sPlayer = event.getPlayer();
        PlayerProgress prog = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
        if (!prog.isValid()) return;

        if (stack.has(DataComponentsAS.LUMEN)) {
            LumenComponent cmp = stack.getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY);
            Lumen lumen = cmp.lumen().value();
            if (!prog.hasDiscoveredLumen(lumen) && lumen != LumenAS.NONE.get()) {
                List<Lumen> newlyDiscovered = ResearchHelper.discoverLumen(sPlayer, RecipeUtil.findAnyLumenMakingUp(sPlayer.serverLevel(), lumen));
                newlyDiscovered.addAll(ResearchHelper.discoverLumen(sPlayer, lumen));
                if (!newlyDiscovered.isEmpty()) {
                    ResearchMessageHelper.sendLumenDiscovery(sPlayer, newlyDiscovered);
                }
            }
        }

        if (stack.has(DataComponentsAS.STORED_LUMEN)) {
            StoredLumenComponent cmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
            Set<Lumen> foundLumen = new HashSet<>();
            cmp.storedLumen().forEach(stored -> foundLumen.add(stored.lumen()));
            foundLumen.addAll(cmp.boundLumen().keySet());

            Set<Lumen> discovered = new HashSet<>();
            for (Lumen lumen : foundLumen) {
                if (!prog.hasDiscoveredLumen(lumen) && lumen != LumenAS.NONE.get()) {
                    discovered.addAll(ResearchHelper.discoverLumen(sPlayer, RecipeUtil.findAnyLumenMakingUp(sPlayer.serverLevel(), lumen)));
                    discovered.addAll(ResearchHelper.discoverLumen(sPlayer, lumen));
                }
            }
            if (!discovered.isEmpty()) {
                ResearchMessageHelper.sendLumenDiscovery(sPlayer, discovered);
            }
        }

        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AltarBlock altarBlock) {
            TileAltar.AltarType altarType = altarBlock.getAltarType();
            ResearchTier targetTier = altarType.getRequiredTier();
            ResearchTier current = prog.getTierReached();
            if (altarType.getRequiredTier().isThisLater(current)) {
                if (ResearchHelper.setResearchProgress(sPlayer, targetTier)) {
                    ResearchMessageHelper.sendResearchTierDiscovery(sPlayer, current, prog.getTierReached());
                }
            }
        }
    }

    private static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) {
            return;
        }

        if (GeneralConfig.CONFIG.giveTomeOnJoin.get()) {
            PlayerProgress progress = ResearchManager.getProgress(sPlayer, LogicalSide.SERVER);
            if (progress.hasReceivedTome()) {
                if (sPlayer.getInventory().add(ItemsAS.TOME.toStack())) {
                    ResearchHelper.setTomeReceived(sPlayer);
                }
            }
        }

        PacketDistributor.sendToPlayer(sPlayer, PktSyncPerkTree.sync());
        PacketDistributor.sendToPlayer(sPlayer, PktSyncResearchNodes.newRequest());
        PacketDistributor.sendToPlayer(sPlayer, PktSyncData.syncAll(SyncDataManager.getInstance()));
        PacketDistributor.sendToPlayer(sPlayer, PktSyncPerkLevels.sync(sPlayer));
        PacketDistributor.sendToPlayer(sPlayer, PktSyncPlayerProgress.newRequest(ResearchManager.getProgress(sPlayer, LogicalSide.SERVER)));
        PacketDistributor.sendToPlayer(sPlayer, PktSyncLumenBindingTypes.newRequest());
    }
}
