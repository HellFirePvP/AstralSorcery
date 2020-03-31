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
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncPerkActivity;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import hellfirepvp.astralsorcery.common.util.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.sextant.TargetObject;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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
            if (!progress.getResearchProgression().contains(give)) {
                progress.forceGainResearch(give);
            }
            progToGive.addAll(give.getPreConditions());
        }

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
    }

    public static void giveResearchIgnoreFail(PlayerEntity player, ResearchProgression prog) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return;

        ProgressionTier tier = prog.getRequiredProgress();
        if (!progress.getTierReached().isThisLaterOrEqual(tier)) return;
        for (ResearchProgression other : prog.getPreConditions()) {
            if (!progress.getResearchProgression().contains(other)) return;
        }

        if (progress.forceGainResearch(prog)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(prog);
            PacketChannel.CHANNEL.sendToPlayer(player, pkt);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
    }

    public static void giveProgressionIgnoreFail(PlayerEntity player, ProgressionTier tier) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return;

        ProgressionTier t = progress.getTierReached();
        if (!t.hasNextTier()) return; //No higher tier available anyway.
        ProgressionTier next = t.next();
        if (!next.equals(tier)) return; //Given one is not the next step.

        progress.setTierReached(next);
        PktProgressionUpdate pkt = new PktProgressionUpdate(next);
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);


        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
    }

    public static boolean useSextantTarget(TargetObject to, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.useTarget(to);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean discoverConstellations(Collection<IConstellation> csts, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        for (IConstellation c : csts) {
            progress.discoverConstellation(c.getRegistryName());
            //TODO advancements
            //AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((ServerPlayerEntity) player, c);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean discoverConstellation(IConstellation c, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.discoverConstellation(c.getRegistryName());

        //TODO advancements
        //AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((ServerPlayerEntity) player, c);

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

        if (constellation != null && !progress.getKnownConstellations().contains(constellation.getRegistryName())) {
            return false;
        }

        Map<AbstractPerk, CompoundNBT> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, CompoundNBT> perkEntry : perkCopy.entrySet()) {
            dropPerk(progress, player, LogicalSide.SERVER, perkEntry.getKey(), perkEntry.getValue());
        }

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL));

        progress.setExp(0);
        progress.setAttunedConstellation(constellation);
        AbstractPerk root;
        if (constellation != null && (root = PerkTree.PERK_TREE.getRootPerk(constellation)) != null) {
            CompoundNBT data = new CompoundNBT();
            root.onUnlockPerkServer(player, progress, data);
            progress.applyPerk(root, data);

            PerkEffectHelper.modifySource(player, LogicalSide.SERVER, root, PerkEffectHelper.Action.ADD);
            PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(root, PerkEffectHelper.Action.ADD));
        }

        //TODO advancements
        //AdvancementTriggers.ATTUNE_SELF.trigger((ServerPlayerEntity) player, constellation);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setPerkData(PlayerEntity player, @Nonnull AbstractPerk perk, CompoundNBT prevoiusData, CompoundNBT newData) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkEffect(perk)) return false;

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.REMOVE);
        progress.applyPerk(perk, newData);
        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.ADD);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, prevoiusData, newData));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean applyPerk(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasFreeAllocationPoint(player)) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        CompoundNBT data = new CompoundNBT();
        perk.onUnlockPerkServer(player, progress, data);
        progress.applyPerk(perk, data);

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.ADD);
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, PerkEffectHelper.Action.ADD));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean applyPerkSeal(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (progress.isPerkSealed(perk)) return false;

        if (!progress.sealPerk(perk)) {
            return false;
        }

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.REMOVE);
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, PerkEffectHelper.Action.REMOVE));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean breakPerkSeal(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (!progress.isPerkSealed(perk)) return false;

        if (!progress.breakSeal(perk)) {
            return false;
        }

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.ADD);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, PerkEffectHelper.Action.ADD));
        return true;
    }

    public static boolean grantFreePerkPoint(PlayerEntity player, String token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        if (!progress.grantFreeAllocationPoint(token)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean revokeFreePoint(PlayerEntity player, String token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        if (!progress.tryRevokeAllocationPoint(token)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean forceApplyPerk(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        CompoundNBT data = new CompoundNBT();
        perk.onUnlockPerkServer(player, progress, data);
        progress.applyPerk(perk, data);

        PerkEffectHelper.modifySource(player, LogicalSide.SERVER, perk, PerkEffectHelper.Action.ADD);
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, PerkEffectHelper.Action.ADD));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean removePerk(PlayerEntity player, AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        CompoundNBT data = progress.getPerkData(perk);
        if (data == null) {
            return false;
        }
        dropPerk(progress, player, LogicalSide.SERVER, perk, data);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, PerkEffectHelper.Action.REMOVE));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean resetPerks(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        Map<AbstractPerk, CompoundNBT> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, CompoundNBT> perkEntry : perkCopy.entrySet()) {
            dropPerk(progress, player, LogicalSide.SERVER, perkEntry.getKey(), perkEntry.getValue());
        }

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    private static void dropPerk(PlayerProgress progress, PlayerEntity player, LogicalSide side, AbstractPerk perk, CompoundNBT data) {
        progress.removePerk(perk);
        PerkEffectHelper.modifySource(player, side, perk, PerkEffectHelper.Action.REMOVE);
        perk.onRemovePerkServer(player, progress, data);
        progress.removePerkData(perk);
    }

    public static boolean setTomeReceived(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.setTomeReceived();

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setExp(PlayerEntity player, long exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.setExp(exp);

        //TODO advancements
        //AdvancementTriggers.PERK_LEVEL.trigger((ServerPlayerEntity) player);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean modifyExp(PlayerEntity player, double exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (!progress.isValid()) return false;

        progress.modifyExp(exp, player);

        //TODO advancements
        //AdvancementTriggers.PERK_LEVEL.trigger((ServerPlayerEntity) player);

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
        for (TargetObject to : SextantFinder.getSelectableTargets()) {
            progress.useTarget(to);
        }

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

    public static void informCraftedInfuser(TileInfuser infuser, ActiveLiquidInfusionRecipe recipe, ItemStack crafted) {
        PlayerEntity crafter = recipe.tryGetCraftingPlayerServer();
        if (!(crafter instanceof ServerPlayerEntity)) {
            AstralSorcery.log.warn("Infusion finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + infuser.getPos() + " in dim " + infuser.getWorld().getDimension().getType().getRegistryName());
            return;
        }

        informCrafted(crafter, crafted);
    }

    public static void informCraftedAltar(TileAltar altar, ActiveSimpleAltarRecipe recipe, ItemStack crafted) {
        PlayerEntity crafter = recipe.tryGetCraftingPlayerServer();
        if (!(crafter instanceof ServerPlayerEntity)) {
            AstralSorcery.log.warn("Crafting finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + altar.getPos() + " in dim " + altar.getWorld().getDimension().getType().getRegistryName());
            return;
        }

        informCrafted(crafter, crafted);

        //TODO advancement trigger altar craft
        //AdvancementTriggers.ALTAR_CRAFT.trigger((ServerPlayerEntity) crafter, recipeToCraft.getRecipeToCraft());
    }

    public static void informCrafted(PlayerEntity player, ItemStack out) {
        if (!out.isEmpty()) {
            informCraftCompletion(player, out, out.getItem(), Block.getBlockFromItem(out.getItem()));
        }
    }

    private static void informCraftCompletion(PlayerEntity crafter, ItemStack crafted, Item itemCrafted, @Nullable Block blockCrafted) {
        if (blockCrafted != null) {
            if (blockCrafted instanceof BlockAltar) {
                giveProgressionIgnoreFail(crafter, ProgressionTier.BASIC_CRAFT);
                giveResearchIgnoreFail(crafter, ResearchProgression.BASIC_CRAFT);

                //Fallthrough switch to lower tiers
                switch (((BlockAltar) blockCrafted).getAltarType()) {
                    case RADIANCE:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.TRAIT_CRAFT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.RADIANCE);
                    case CONSTELLATION:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.CONSTELLATION_CRAFT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.CONSTELLATION);
                    case ATTUNEMENT:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.ATTUNEMENT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.ATTUNEMENT);
                    default:
                        break;
                }
            }
        }
    }

}
