/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.network.play.PktSyncPerkActivity;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.tick.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.PlayerPerkData;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import hellfirepvp.astralsorcery.common.util.data.SidedReference;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkManager {

    private static final PerkManager INSTANCE = new PerkManager();

    private final SidedReference<Map<UUID, PerkAttributeMap>> perkMap = SidedReference.create(HashMap::new);

    private PerkManager() {}

    public static PerkManager getInstance() {
        return INSTANCE;
    }

    public static PerkAttributeMap getOrCreateAttributes(Player player) {
        LogicalSide side = SidedHelper.getSide(player);
        return getInstance().perkMap.getData(side)
                .map(dataMap -> dataMap.computeIfAbsent(player.getUUID(), id -> new PerkAttributeMap(side)))
                .orElseThrow();
    }

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(this::onPlayerConnect);
        bus.addListener(this::onPlayerDisconnect);
        bus.addListener(this::onPlayerRecreate);
    }

    private void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        modifyAllPerks(event.getEntity(), LogicalSide.SERVER, Action.ADD);

        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            PacketDistributor.sendToPlayer(sPlayer, PktSyncPerkActivity.applyAll());
        }
    }

    private void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        modifyAllPerks(event.getEntity(), LogicalSide.SERVER, Action.REMOVE);
    }

    private void onPlayerRecreate(PlayerEvent.Clone event) {
        modifyAllPerks(event.getOriginal(), LogicalSide.SERVER, Action.REMOVE);
        modifyAllPerks(event.getEntity(), LogicalSide.SERVER, Action.ADD);

        PerkCooldownHelper.removeAllCooldowns(event.getOriginal(), LogicalSide.SERVER);
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            PacketDistributor.sendToPlayer(sPlayer, PktSyncPerkActivity.applyAll());
        }
    }

    public void clearServer() {
        this.perkMap.getData(LogicalSide.SERVER).ifPresent(Map::clear);
    }

    public void clearClient() {
        this.perkMap.getData(LogicalSide.CLIENT).ifPresent(Map::clear);
    }

    @OnlyIn(Dist.CLIENT)
    public static <D extends AbstractPerk.Data> void clientChangePerkData(AbstractPerk<D> perk, D oldData, D newData) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        PlayerProgress progress = ResearchManager.getProgress(player, LogicalSide.CLIENT);
        PlayerPerkData perkData = progress.getPerkData();

        if (!perkData.hasPerkAllocation(perk)) {
            return;
        }
        perkData.updateAllocatedPerkData(perk, oldData);
        PerkApplicationManager.modifySource(player, LogicalSide.CLIENT, perk, Action.REMOVE);
        perkData.updateAllocatedPerkData(perk, newData);
        PerkApplicationManager.modifySource(player, LogicalSide.CLIENT, perk, Action.ADD);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientClearAllPerks() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        PlayerProgress progress = ResearchManager.getProgress(player, LogicalSide.CLIENT);
        if (!progress.isValid()) {
            return;
        }

        PerkAttributeMap attr = getOrCreateAttributes(player);
        for (ModifierSource source : ModifierManager.getAppliedModifiers(player, LogicalSide.CLIENT)) {
            if (source instanceof AbstractPerk) {
                PerkApplicationManager.removeSource(attr, player, LogicalSide.CLIENT, source);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientRefreshAllPerks() {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        modifyAllPerks(player, LogicalSide.CLIENT, Action.ADD);
        PerkCooldownHelper.removeAllCooldowns(player, LogicalSide.CLIENT);
    }

    private static void modifyAllPerks(Player player, LogicalSide side, Action action) {
        ResearchManager.getProgress(player, side).getPerkData().getEffectGrantingPerks()
                .forEach(perk -> PerkApplicationManager.modifySource(player, side, perk, action));
    }

    public enum Action {

        ADD,
        REMOVE;

        public boolean isRemove() {
            return this == REMOVE;
        }

    }
}
