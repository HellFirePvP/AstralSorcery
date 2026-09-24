/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.property.AttunePlayerProperty;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.network.play.PktSyncModifierSource;
import hellfirepvp.astralsorcery.common.network.play.PktSyncPerkActivity;
import hellfirepvp.astralsorcery.common.network.play.PktSyncPlayerProgress;
import hellfirepvp.astralsorcery.common.perk.PerkApplicationManager;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.research.io.ResearchWriter;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocation;
import hellfirepvp.astralsorcery.common.research.perk.PerkAllocationType;
import hellfirepvp.astralsorcery.common.research.perk.PerkRemovalResult;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchHelper {

    public static boolean memorizeConstellation(ServerPlayer player, BaseConstellation constellation) {
        return withProgress(player, progress -> progress.memorizeConstellation(constellation));
    }

    public static boolean discoverConstellation(ServerPlayer player, BaseConstellation constellation) {
        return withProgress(player, progress -> progress.discoverConstellation(constellation));
    }

    public static boolean discoverConstellations(ServerPlayer player, Collection<BaseConstellation> constellations) {
        return withProgress(player, progress -> {
            boolean discoveredAny = false;
            for (BaseConstellation c : constellations) {
                if (progress.discoverConstellation(c)) discoveredAny = true;
            }
            return discoveredAny;
        });
    }

    public static boolean memorizeFocalPoint(ServerPlayer player, BaseConstellation constellation) {
        return withProgress(player, progress ->
                constellation.is(TagsAS.Constellations.MAY_BE_FOCAL_POINT) && progress.memorizeFocalPoint(constellation));
    }

    public static boolean memorizeFocalPoints(ServerPlayer player, Collection<BaseConstellation> constellations) {
        return withProgress(player, progress -> {
            boolean memorizedAny = false;
            for (BaseConstellation c : constellations) {
                if (c.is(TagsAS.Constellations.MAY_BE_FOCAL_POINT) && progress.memorizeFocalPoint(c)) memorizedAny = true;
            }
            return memorizedAny;
        });
    }

    public static List<Lumen> discoverLumen(ServerPlayer player, Lumen lumen) {
        List<Lumen> discovered = new ArrayList<>();
        withProgress(player, progress -> {
            if (progress.discoverLumen(lumen)) {
                discovered.add(lumen);
                return true;
            }
            return false;
        });
        return discovered;
    }

    public static List<Lumen> discoverLumen(ServerPlayer player, Collection<Lumen> lumen) {
        List<Lumen> discovered = new ArrayList<>();
        withProgress(player, progress -> {
            for (Lumen l : lumen) {
                if (progress.discoverLumen(l)) {
                    discovered.add(l);
                }
            }
            return !discovered.isEmpty();
        });
        return discovered;
    }

    public static boolean setResearchProgress(ServerPlayer player, ResearchTier progression) {
        return withProgress(player, progress -> {
            progress.setProgression(progression);
            return true;
        });
    }

    public static boolean setLearnedToNavigateTome(ServerPlayer player) {
        return withProgress(player, progress -> {
            progress.setLearnedToNavigateTome(true);
            return true;
        });
    }

    public static boolean setTomeReceived(ServerPlayer player) {
        return withProgress(player, progress -> {
            progress.setTomeReceived(true);
            return true;
        });
    }

    public static boolean acceptKnowledgeShare(ServerPlayer player, PlayerProgress otherProgress) {
        return withProgress(player, progress -> {
            progress.mergeKnowledgeShare(otherProgress);
            return true;
        });
    }

    public static boolean attuneConstellation(ServerPlayer player, BaseConstellation constellation) {
        return withProgress(player, progress -> {
            if (!progress.hasDiscoveredConstellation(constellation)) return false;

            removeAllAllocatedPerks(progress, player);

            PlayerPerkData perkData = progress.getPerkData();
            perkData.setExp(0);
            progress.setAttunedConstellation(constellation);

            AttunePlayerProperty.getRootPerk(constellation, LogicalSide.SERVER).ifPresent(root -> {
                doApplyPerk(progress, perkData, player, root, PerkAllocation.unlock());
            });
            return true;
        });
    }

    public static boolean removeAttunedConstellation(ServerPlayer player) {
        return withProgress(player, progress -> {
            removeAllAllocatedPerks(progress, player);
            progress.getPerkData().setExp(0);
            progress.setAttunedConstellation(null);
            return true;
        });
    }

    public static boolean setAttunedFlag(ServerPlayer player, boolean wasAttunedBefore) {
        return withProgress(player, progress -> {
            progress.setWasOnceAttuned(wasAttunedBefore);
            return true;
        });
    }

    public static boolean setKnowledgeFlag(ServerPlayer player, ResearchFlag flag) {
        return withProgress(player, progress -> {
            return progress.setKnownFlag(flag);
        });
    }

    public static boolean maximizeAll(ServerPlayer player) {
        discoverConstellations(player, RegistriesAS.REGISTRY_CONSTELLATIONS.stream().toList());
        memorizeFocalPoints(player, RegistriesAS.REGISTRY_CONSTELLATIONS.stream().toList());
        setResearchProgress(player, ResearchTier.values()[ResearchTier.values().length - 1]);
        setAttunedFlag(player, true);
        Arrays.stream(ResearchFlag.values()).forEach(flag -> setKnowledgeFlag(player, flag));
        discoverLumen(player, RegistriesAS.REGISTRY_LUMEN.stream().toList());
        return true;
    }

    public static boolean wipeProgress(ServerPlayer player) {
        PlayerProgress progress = ResearchManager.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        resetPerks(player);
        ResearchManager.removeProgress(player.getUUID());
        ResearchWriter.wipeFiles(player.getUUID());

        PlayerProgress newProgress = ResearchManager.getProgress(player, LogicalSide.SERVER);

        PacketDistributor.sendToPlayer(player, PktSyncPlayerProgress.newRequest(newProgress));
        ResearchManager.scheduleSave(player, true);
        return true;
    }

    private static boolean withProgress(ServerPlayer player, Predicate<PlayerProgress> fn) {
        PlayerProgress progress = ResearchManager.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        if (!fn.test(progress)) return false;

        PacketDistributor.sendToPlayer(player, PktSyncPlayerProgress.newRequest(progress));
        ResearchManager.scheduleSave(player, false);
        return true;
    }

    // ******************** PERKS ******************** //

    public static boolean setPerkExp(ServerPlayer player, double exp) {
        return withProgress(player, progress -> {
            progress.getPerkData().setExp(exp);
            return true;
        });
    }

    public static boolean addPerkExp(ServerPlayer player, double exp) {
        return withProgress(player, progress -> {
            progress.getPerkData().modifyExp(player, exp);
            return true;
        });
    }

    public static boolean givePerkPointToken(ServerPlayer player, ResourceLocation token) {
        return withProgress(player, progress -> {
            return progress.getPerkData().grantFreeAllocationPoint(token);
        });
    }

    public static boolean removePerkPointToken(ServerPlayer player, ResourceLocation token) {
        return withProgress(player, progress -> {
            return progress.getPerkData().removeAllocationPoint(token);
        });
    }

    public static boolean applyPerk(ServerPlayer player, AbstractPerk<?> perk, PerkAllocation alloc) {
        return withProgress(player, progress -> {
            PlayerPerkData perkData = progress.getPerkData();

            // only check unlocked, granted comes with a token
            if (alloc.getType().isUnlock()) {
                if (!perkData.hasFreeAllocationPoint(player, LogicalSide.SERVER)) return false;
            }

            return doApplyPerk(progress, perkData, player, perk, alloc);
        });
    }

    public static boolean removePerk(ServerPlayer player, AbstractPerk<?> perk, PerkAllocation alloc) {
        return withProgress(player, progress -> {
            return doRemovePerk(progress, player, perk, alloc, true);
        });
    }

    public static boolean applyPerkSeal(ServerPlayer player, AbstractPerk<?> perk) {
        return withProgress(player, progress -> {
            PlayerPerkData perkData = progress.getPerkData();
            if (!perkData.hasPerkAllocation(perk)) return false;
            if (perkData.isPerkSealed(perk)) return false;
            if (!perkData.canSealPerk(perk)) return false;

            PerkApplicationManager.modifySource(player, LogicalSide.SERVER, perk, PerkManager.Action.REMOVE);
            PacketDistributor.sendToPlayer(player, PktSyncModifierSource.remove(perk));

            if (!perkData.sealPerk(perk)) {
                // We probably have bigger problems here
                throw new IllegalStateException("Failed to seal perk after player progress said it was fine? *shrug*");
            }
            return true;
        });
    }

    public static boolean breakPerkSeal(ServerPlayer player, AbstractPerk<?> perk) {
        boolean success = withProgress(player, progress -> {
            PlayerPerkData perkData = progress.getPerkData();
            if (!perkData.hasPerkAllocation(perk)) return false;
            if (!perkData.isPerkSealed(perk)) return false;
            if (!perkData.breakSeal(perk)) return false;

            PerkApplicationManager.modifySource(player, LogicalSide.SERVER, perk, PerkManager.Action.ADD);
            return true;
        });

        // Delayed because we have to wait for the client to receive the info via player-progress-sync
        // that that perk actually is unsealed and has modifiers again.
        // Yea i know it's not great, but is what it is...
        if (success) {
            PacketDistributor.sendToPlayer(player, PktSyncModifierSource.add(perk));
        }
        return success;
    }

    public static <D extends AbstractPerk.Data> boolean updatePerkData(ServerPlayer player, AbstractPerk<D> perk, D oldData, D newData) {
        return withProgress(player, progress -> {
            PlayerPerkData perkData = progress.getPerkData();
            if (!perkData.hasPerkAllocation(perk)) return false;

            PerkApplicationManager.modifySource(player, LogicalSide.SERVER, perk, PerkManager.Action.REMOVE);
            perkData.updateAllocatedPerkData(perk, newData);
            PerkApplicationManager.modifySource(player, LogicalSide.SERVER, perk, PerkManager.Action.ADD);

            PacketDistributor.sendToPlayer(player, PktSyncPerkActivity.changeData(perk, oldData, newData));
            return true;
        });
    }

    public static boolean resetPerks(ServerPlayer player) {
        return withProgress(player, progress -> {
            removeAllAllocatedPerks(progress, player);
            return true;
        });
    }

    private static void removeAllAllocatedPerks(PlayerProgress progress, ServerPlayer player) {
        PlayerPerkData perkData = progress.getPerkData();
        List<AbstractPerk<?>> allocatedPerks = new ArrayList<>(perkData.getAllocatedPerks(PerkAllocationType.UNLOCKED));
        allocatedPerks.addAll(perkData.getAllocatedPerks(PerkAllocationType.UNLOCKED_NON_CONNECT));
        List<AbstractPerk<?>> syncRemovable = new ArrayList<>();
        for (AbstractPerk<?> perk : allocatedPerks) {
            if (doRemovePerk(progress, player, perk, PerkAllocation.unlock(), false)) {
                syncRemovable.add(perk);
            }
        }
        PacketDistributor.sendToPlayer(player, PktSyncPerkActivity.removePerks(syncRemovable));
    }

    private static boolean doRemovePerk(PlayerProgress progress, ServerPlayer player, AbstractPerk<?> perk, PerkAllocation allocation, boolean sync) {
        PlayerPerkData perkData = progress.getPerkData();
        if (perkData.hasPerkAllocation(perk, allocation.getType())) {
            AbstractPerk.Data data = perkData.getPerkData(perk).orElse(null);
            if (data != null) {
                PerkRemovalResult removeResult = perkData.removePerkAllocation(perk, allocation, true);
                if (removeResult.isFailure()) {
                    return false;
                }
                if (removeResult.removesPerk()) {
                    PerkApplicationManager.modifySource(player, LogicalSide.SERVER, perk, PerkManager.Action.REMOVE);
                }
                if (removeResult.removesAllocationType()) {
                    perk.onRemovePerkServer(player, allocation.getType(), progress, MiscUtil.cast(data));
                }
                PerkRemovalResult actualResult = perkData.removePerkAllocation(perk, allocation, false);
                if (actualResult.removesPerk()) {
                    if (sync) {
                        PacketDistributor.sendToPlayer(player, PktSyncModifierSource.remove(perk));
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean doApplyPerk(PlayerProgress progress, PlayerPerkData perkData, ServerPlayer player, AbstractPerk<?> perk, PerkAllocation allocation) {
        if (!perkData.applyPerkAllocation(perk, allocation, true)) {
            return false;
        }
        if (perkData.hasPerkAllocation(perk)) {
            if (!perkData.hasPerkAllocation(perk, allocation.getType())) {
                AbstractPerk.Data data = perkData.getPerkData(perk).orElseThrow();
                perk.onUnlockPerkServer(player, allocation.getType(), progress, MiscUtil.cast(data));
            }
            return perkData.applyPerkAllocation(perk, allocation, false);
        } else {
            AbstractPerk.Data newData = perk.getType().dataType().emptyDataFn().get();
            perk.onUnlockPerkServer(player, allocation.getType(), progress, MiscUtil.cast(newData));
            perkData.applyPerkAllocation(perk, allocation, false);
            perkData.updateAllocatedPerkData(perk, MiscUtil.cast(newData));

            PerkApplicationManager.modifySource(player, LogicalSide.SERVER, perk, PerkManager.Action.ADD);
            PacketDistributor.sendToPlayer(player, PktSyncModifierSource.add(perk));
            return true;
        }
    }
}
