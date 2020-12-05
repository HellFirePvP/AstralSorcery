/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.infusion.ActiveLiquidInfusionRecipe;
import hellfirepvp.astralsorcery.common.lib.AdvancementsAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncModifierSource;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncPerkActivity;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchManager
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:33
 */
public class ResearchManager {

    public static void unsafeForceGiveResearch(ServerPlayerEntity player, ResearchProgression prog) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return;

        ProgressionTier reqTier = prog.getRequiredProgress();
        if (!progress.getTierReached().isThisLaterOrEqual(reqTier)) {
            progress.setTierReached(reqTier);
        }

        LinkedList<ResearchProgression> progToGive = new LinkedList<>();
        progToGive.add(prog);
        while (!progToGive.isEmpty()) {
            ResearchProgression give = progToGive.pop();
            if (!progress.hasResearch(give)) {
                progress.forceGainResearch(give);
            }
            progToGive.addAll(give.getPreConditions());
        }

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
    }

    public static boolean grantResearch(PlayerEntity player, ResearchProgression prog) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        ProgressionTier tier = prog.getRequiredProgress();
        if (!progress.getTierReached().isThisLaterOrEqual(tier)) return false;
        for (ResearchProgression other : prog.getPreConditions()) {
            if (!progress.hasResearch(other)) return false;
        }

        if (progress.forceGainResearch(prog)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(prog);
            PacketChannel.CHANNEL.sendToPlayer(player, pkt);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean grantProgress(PlayerEntity player, ProgressionTier tier) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        ProgressionTier t = progress.getTierReached();
        if (!t.hasNextTier()) return false; //No higher tier available anyway.
        ProgressionTier next = t.next();
        if (!next.equals(tier)) return false; //Given one is not the next step.

        progress.setTierReached(next);
        PktProgressionUpdate pkt = new PktProgressionUpdate(next);
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean discoverConstellations(Collection<IConstellation> constellations, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        for (IConstellation cst : constellations) {
            progress.discoverConstellation(cst.getRegistryName());
            AdvancementsAS.DISCOVER_CONSTELLATION.trigger((ServerPlayerEntity) player, cst);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean discoverConstellation(IConstellation constellation, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.discoverConstellation(constellation.getRegistryName());

        AdvancementsAS.DISCOVER_CONSTELLATION.trigger((ServerPlayerEntity) player, constellation);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean memorizeConstellation(IConstellation c, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.memorizeConstellation(c.getRegistryName());

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean updateConstellationPapers(List<IConstellation> papers, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.setStoredConstellationPapers(papers.stream().map(IForgeRegistryEntry::getRegistryName).collect(Collectors.toList()));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean maximizeTier(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.setTierReached(ProgressionTier.values()[ProgressionTier.values().length - 1]);

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setAttunedBefore(PlayerEntity player, boolean wasAttunedBefore) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.setAttunedBefore(wasAttunedBefore);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setAttunedConstellation(PlayerEntity player, @Nullable IMajorConstellation constellation) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        if (constellation != null && !progress.hasConstellationDiscovered(constellation)) {
            return false;
        }
        PlayerPerkData perkData = progress.getPerkData();
        removeAllAllocatedPerks(progress, player);

        perkData.setExp(0);
        progress.setAttunedConstellation(constellation);
        AbstractPerk root;
        if (constellation != null && (root = PerkTree.PERK_TREE.getRootPerk(LogicalSide.SERVER, constellation)) != null) {
            doApplyPerk(progress, perkData, player, root, PlayerPerkAllocation.unlock());
        }

        AdvancementsAS.ATTUNE_SELF.trigger((ServerPlayerEntity) player, constellation);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setPerkData(PlayerEntity player, @Nonnull AbstractPerk perk, CompoundNBT prevoiusData, CompoundNBT newData) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        PlayerPerkData perkData = progress.getPerkData();
        if (!perkData.hasPerkAllocation(perk)) return false;

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.REMOVE);
        progress.getPerkData().updatePerkData(perk, newData);
        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.ADD);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, prevoiusData, newData));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean applyPerk(PlayerEntity player, @Nonnull AbstractPerk perk, PlayerPerkAllocation allocation) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        PlayerPerkData perkData = progress.getPerkData();

        if (allocation.getType() == PerkAllocationType.UNLOCKED) {
            if (!perkData.hasFreeAllocationPoint(player, LogicalSide.SERVER)) return false;
        }

        if (!doApplyPerk(progress, perkData, player, perk, allocation)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean applyPerkSeal(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        PlayerPerkData perkData = progress.getPerkData();
        if (!perkData.hasPerkAllocation(perk)) return false;
        if (perkData.isPerkSealed(perk)) return false;

        if (!perkData.canSealPerk(perk)) {
            return false;
        }

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.REMOVE);
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncModifierSource(perk, PerkEffectHelper.Action.REMOVE));

        if (!perkData.sealPerk(perk)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean breakPerkSeal(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        PlayerPerkData perkData = progress.getPerkData();
        if (!perkData.isPerkSealed(perk)) return false;

        if (!perkData.breakSeal(perk)) {
            return false;
        }

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.ADD);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncModifierSource(perk, PerkEffectHelper.Action.ADD));
        return true;
    }

    public static boolean grantFreePerkPoint(PlayerEntity player, ResourceLocation token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        if (!progress.getPerkData().grantFreeAllocationPoint(token)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean revokeFreePoint(PlayerEntity player, ResourceLocation token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        if (!progress.getPerkData().tryRevokeAllocationPoint(token)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean forceApplyPerk(PlayerEntity player, @Nonnull AbstractPerk perk, PlayerPerkAllocation allocation) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        PlayerPerkData perkData = progress.getPerkData();

        if (!doApplyPerk(progress, perkData, player, perk, allocation)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean removePerk(PlayerEntity player, AbstractPerk perk, PlayerPerkAllocation allocation) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        PlayerPerkData perkData = progress.getPerkData();
        if (!perkData.hasPerkAllocation(perk, allocation.getType())) return false;

        if (!doRemovePerk(progress, player, LogicalSide.SERVER, perk, allocation, true)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean resetPerks(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        removeAllAllocatedPerks(progress, player);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    private static void removeAllAllocatedPerks(PlayerProgress progress, PlayerEntity player) {
        PlayerPerkData perkData = progress.getPerkData();
        List<AbstractPerk> allocatedPerks = new ArrayList<>(perkData.getAllocatedPerks(PerkAllocationType.UNLOCKED));
        List<AbstractPerk> syncRemovable = new ArrayList<>();
        for (AbstractPerk perk : allocatedPerks) {
            if (doRemovePerk(progress, player, LogicalSide.SERVER, perk, PlayerPerkAllocation.unlock(), false)) {
                syncRemovable.add(perk);
            }
        }
        List<ResourceLocation> removals = syncRemovable.stream().map(AbstractPerk::getRegistryName).collect(Collectors.toList());
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(removals));
    }

    private static boolean doRemovePerk(PlayerProgress progress, PlayerEntity player, LogicalSide side, AbstractPerk perk, PlayerPerkAllocation allocation, boolean sync) {
        PlayerPerkData perkData = progress.getPerkData();
        if (perkData.hasPerkAllocation(perk, allocation.getType())) {
            CompoundNBT data = perkData.getData(perk);
            if (data != null) {
                PerkRemovalResult removeResult = perkData.removePerkAllocation(perk, allocation, true);
                if (removeResult.isFailure()) {
                    return false;
                }
                if (removeResult.removesPerk()) {
                    PerkEffectHelper.modifySource(player, side, perk, PerkEffectHelper.Action.REMOVE);
                }
                if (removeResult.removesAllocationType()) {
                    perk.onRemovePerkServer(player, allocation.getType(), progress, data);
                }
                PerkRemovalResult actualResult = perkData.removePerkAllocation(perk, allocation, false);
                if (actualResult.removesPerk()) {
                    if (sync) {
                        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncModifierSource(perk, PerkEffectHelper.Action.REMOVE));
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean doApplyPerk(PlayerProgress progress, PlayerPerkData perkData, PlayerEntity player, AbstractPerk perk, PlayerPerkAllocation allocation) {
        if (!perkData.applyPerkAllocation(perk, allocation, true)) {
            return false;
        }
        if (perkData.hasPerkAllocation(perk)) {
            if (!perkData.hasPerkAllocation(perk, allocation.getType())) {
                CompoundNBT data = perkData.getData(perk);
                perk.onUnlockPerkServer(player, allocation.getType(), progress, data);
            }
            return perkData.applyPerkAllocation(perk, allocation, false);
        } else {
            CompoundNBT data = new CompoundNBT();
            perk.onUnlockPerkServer(player, allocation.getType(), progress, data);
            perkData.applyPerkAllocation(perk, allocation, false);
            perkData.updatePerkData(perk, data);

            PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.ADD);
            PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncModifierSource(perk, PerkEffectHelper.Action.ADD));
            return true;
        }
    }

    public static boolean setTomeReceived(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.setTomeReceived();

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean togglePerkAbilities(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.setUsePerkAbilities(!progress.doPerkAbilities());

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setExp(PlayerEntity player, long exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.getPerkData().setExp(exp);

        AdvancementsAS.PERK_LEVEL.trigger((ServerPlayerEntity) player);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean modifyExp(PlayerEntity player, double exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.getPerkData().modifyExp(exp, player);

        AdvancementsAS.PERK_LEVEL.trigger((ServerPlayerEntity) player);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean forceMaximizeAll(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        ProgressionTier before = progress.getTierReached();

        ResearchManager.discoverConstellations(ConstellationRegistry.getAllConstellations(), player);
        ResearchManager.maximizeTier(player);
        ResearchManager.forceMaximizeResearch(player);
        ResearchManager.setAttunedBefore(player, true);

        if (progress.getTierReached().isThisLater(before)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(progress.getTierReached());
            PacketChannel.CHANNEL.sendToPlayer(player, pkt);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean forceMaximizeResearch(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        for (ResearchProgression progression : ResearchProgression.values()) {
            progress.forceGainResearch(progression);
        }

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static void informCraftedInfuser(@Nonnull TileInfuser infuser, @Nonnull ActiveLiquidInfusionRecipe recipe, @Nonnull ItemStack crafted) {
        PlayerEntity crafter = recipe.tryGetCraftingPlayerServer();
        if (!(crafter instanceof ServerPlayerEntity)) {
            AstralSorcery.log.warn("Infusion finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + infuser.getPos() + " in dim " + infuser.getWorld().getDimensionKey().getLocation());
            return;
        }

        informCrafted(crafter, crafted);
    }

    public static void informCraftedAltar(@Nonnull TileAltar altar, @Nonnull ActiveSimpleAltarRecipe recipe, @Nonnull ItemStack crafted) {
        PlayerEntity crafter = recipe.tryGetCraftingPlayerServer();
        if (!(crafter instanceof ServerPlayerEntity)) {
            AstralSorcery.log.warn("Crafting finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + altar.getPos() + " in dim " + altar.getWorld().getDimensionKey().getLocation());
            return;
        }

        informCrafted(crafter, crafted);

        AdvancementsAS.ALTAR_CRAFT.trigger((ServerPlayerEntity) crafter, recipe.getRecipeToCraft(), crafted);
    }

    public static void informCrafted(@Nonnull PlayerEntity player, @Nonnull ItemStack out) {
        if (!out.isEmpty()) {
            informCraftCompletion(player, out, out.getItem(), Block.getBlockFromItem(out.getItem()));
        }
    }

    private static void informCraftCompletion(@Nonnull PlayerEntity crafter, @Nonnull ItemStack crafted, @Nonnull Item itemCrafted, @Nonnull Block blockCrafted) {
        if (blockCrafted instanceof BlockAltar) {
            grantProgress(crafter, ProgressionTier.BASIC_CRAFT);
            grantResearch(crafter, ResearchProgression.BASIC_CRAFT);

            //Fallthrough switch to lower tiers
            switch (((BlockAltar) blockCrafted).getAltarType()) {
                case RADIANCE:
                    grantProgress(crafter, ProgressionTier.TRAIT_CRAFT);
                    grantResearch(crafter, ResearchProgression.RADIANCE);
                case CONSTELLATION:
                    grantProgress(crafter, ProgressionTier.CONSTELLATION_CRAFT);
                    grantResearch(crafter, ResearchProgression.CONSTELLATION);
                case ATTUNEMENT:
                    grantProgress(crafter, ProgressionTier.ATTUNEMENT);
                    grantResearch(crafter, ResearchProgression.ATTUNEMENT);
                default:
                    break;
            }
        }
    }

}
