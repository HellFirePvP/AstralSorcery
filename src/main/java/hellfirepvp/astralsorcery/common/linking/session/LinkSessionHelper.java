/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking.session;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.linking.Linkable;
import hellfirepvp.astralsorcery.common.network.play.PktUpdateLinkSession;
import hellfirepvp.astralsorcery.common.starlight.StarlightNetworkLevelHelper;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.function.BiFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinkSessionHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LinkSessionHelper {

    private static final Map<UUID, ActiveLinkSession> ACTIVE_SESSIONS = new HashMap<>();

    public static void attachEventListeners(IEventBus bus) {
        bus.addListener(LinkSessionHelper::onPlayerLogout);
        bus.addListener(LinkSessionHelper::onPlayerTick);
    }

    private static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            stopSession(sPlayer, false);
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            ActiveLinkSession session = ACTIVE_SESSIONS.get(sPlayer.getUUID());
            if (session != null &&
                    !sPlayer.getItemInHand(InteractionHand.MAIN_HAND).is(ItemsAS.LINKING_TOOL) &&
                    !sPlayer.getItemInHand(InteractionHand.OFF_HAND).is(ItemsAS.LINKING_TOOL)) {
                stopSession(sPlayer);
            }
        }
    }

    public static <T extends Block & Linkable> void startBlockSession(ServerPlayer sPlayer, BlockPos pos, T block) {
        if (block.startLinking(sPlayer)) {
            startSession(sPlayer, ActiveLinkSession.ResolvableLink.pos(pos), block, (session, lvl) -> Optional.of(block));
        }
    }

    public static <T extends BlockEntity & Linkable> void startBlockEntitySession(ServerPlayer sPlayer, T tile) {
        if (tile.startLinking(sPlayer)) {
            BlockPos pos = tile.getBlockPos();
            startSession(sPlayer, ActiveLinkSession.ResolvableLink.pos(pos), tile, (session, lvl) -> MiscUtil.getTileAt(lvl, pos, Linkable.class, true));
        }
    }

    public static <T extends TransmissionNode & Linkable> void startNetworkNodeSession(ServerPlayer sPlayer, T node) {
        if (node.startLinking(sPlayer)) {
            BlockPos pos = node.getNodePos();
            startSession(sPlayer, ActiveLinkSession.ResolvableLink.pos(pos), node, (session, lvl) -> StarlightNetworkLevelHelper.get(lvl).getNode(pos).map(MiscUtil::cast));
        }
    }

    public static <T extends Entity & Linkable> void startEntitySession(ServerPlayer sPlayer, T entity) {
        if (entity.startLinking(sPlayer)) {
            UUID entityUid = entity.getUUID();
            startSession(sPlayer, ActiveLinkSession.ResolvableLink.entity(entity.getId()), entity, (session, lvl) -> Optional.ofNullable(lvl.getEntity(entityUid)).map(MiscUtil::cast));
        }
    }

    private static void startSession(ServerPlayer player, ActiveLinkSession.ResolvableLink reference, Linkable linkable, BiFunction<ActiveLinkSession, ServerLevel, Optional<Linkable>> referenceResolver) {
        stopSession(player, false);

        ActiveLinkSession session = new ActiveLinkSession(player.getUUID(), reference, listLinks(player.serverLevel(), linkable), referenceResolver);
        ACTIVE_SESSIONS.put(player.getUUID(), session);
        PacketDistributor.sendToPlayer(player, PktUpdateLinkSession.updateSession(session));
        player.sendSystemMessage(Component.translatable("message.astralsorcery.linking.start", linkable.getDisplayName(player.serverLevel())).withStyle(ChatFormatting.GREEN));
    }

    private static List<ActiveLinkSession.ResolvableLink> listLinks(ServerLevel level, Linkable linkable) {
        List<ActiveLinkSession.ResolvableLink> linked = new ArrayList<>();
        linkable.getLinkedPositions().forEach(connection -> {
            linked.add(ActiveLinkSession.ResolvableLink.pos(connection.getTo()));
        });
        linkable.getLinkedEntities().forEach(connection -> {
            connection.resolveEntity(level).ifPresent(entity -> {
                linked.add(ActiveLinkSession.ResolvableLink.entity(entity.getId()));
            });
        });

        return linked;
    }

    public static void updateLinkSession(ServerPlayer sPlayer, Linkable linkable, ActiveLinkSession session) {
        if (ACTIVE_SESSIONS.get(sPlayer.getUUID()) == session) { //Only copy & update explicitly this session
            ActiveLinkSession updatedSession = session.updateLinks(listLinks(sPlayer.serverLevel(), linkable));
            ACTIVE_SESSIONS.put(sPlayer.getUUID(), updatedSession);
            PacketDistributor.sendToPlayer(sPlayer, PktUpdateLinkSession.updateSession(updatedSession));
        }
    }

    public static void stopSession(ServerPlayer player) {
        stopSession(player, true);
    }

    private static void stopSession(ServerPlayer player, boolean sync) {
        ActiveLinkSession existingSession = ACTIVE_SESSIONS.remove(player.getUUID());
        if (sync && existingSession != null) {
            PacketDistributor.sendToPlayer(player, PktUpdateLinkSession.stopSession());
            player.sendSystemMessage(Component.translatable("message.astralsorcery.linking.stop").withStyle(ChatFormatting.RED));
        }
    }

    public static Optional<ActiveLinkSession> getActiveSession(Player player) {
        return Optional.ofNullable(ACTIVE_SESSIONS.get(player.getUUID()));
    }

    public static void clearServerCache() {
        ACTIVE_SESSIONS.clear();
    }
}
