/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncPerkActivity;
import hellfirepvp.astralsorcery.common.util.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.sextant.TargetObject;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;

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
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return;

        ProgressionTier reqTier = prog.getRequiredProgress();
        if(!progress.getTierReached().isThisLaterOrEqual(reqTier)) {
            progress.setTierReached(reqTier);
        }

        LinkedList<ResearchProgression> progToGive = new LinkedList<>();
        progToGive.add(prog);
        while (!progToGive.isEmpty()) {
            ResearchProgression give = progToGive.pop();
            if(!progress.getResearchProgression().contains(give)) {
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
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return;

        ProgressionTier tier = prog.getRequiredProgress();
        if(!progress.getTierReached().isThisLaterOrEqual(tier)) return;
        for (ResearchProgression other : prog.getPreConditions()) {
            if(!progress.getResearchProgression().contains(other)) return;
        }

        if(progress.forceGainResearch(prog)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(prog);
            PacketChannel.CHANNEL.sendToPlayer(player, pkt);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
    }

    public static void giveProgressionIgnoreFail(PlayerEntity player, ProgressionTier tier) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return;

        ProgressionTier t = progress.getTierReached();
        if(!t.hasNextTier()) return; //No higher tier available anyway.
        ProgressionTier next = t.next();
        if(!next.equals(tier)) return; //Given one is not the next step.

        progress.setTierReached(next);
        PktProgressionUpdate pkt = new PktProgressionUpdate(next);
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);


        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
    }

    public static boolean useSextantTarget(TargetObject to, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.useTarget(to);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean discoverConstellations(Collection<IConstellation> csts, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        for (IConstellation c : csts) {
            progress.discoverConstellation(c.getUnlocalizedName());
            //TODO advancements
            //AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((ServerPlayerEntity) player, c);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean discoverConstellation(IConstellation c, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.discoverConstellation(c.getUnlocalizedName());

        //TODO advancements
        //AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((ServerPlayerEntity) player, c);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean memorizeConstellation(IConstellation c, PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.memorizeConstellation(c.getUnlocalizedName());

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean maximizeTier(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.setTierReached(ProgressionTier.values()[ProgressionTier.values().length - 1]);

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendToPlayer(player, pkt);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setAttunedBefore(PlayerEntity player, boolean wasAttunedBefore) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.setAttunedBefore(wasAttunedBefore);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setAttunedConstellation(PlayerEntity player, @Nullable IMajorConstellation constellation) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        if (constellation != null && !progress.getKnownConstellations().contains(constellation.getUnlocalizedName())) {
            return false;
        }

        Map<AbstractPerk, CompoundNBT> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, CompoundNBT> perkEntry : perkCopy.entrySet()) {
            dropPerk(progress, player, Dist.DEDICATED_SERVER, perkEntry.getKey(), perkEntry.getValue());
        }

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL));

        progress.setExp(0);
        progress.setAttunedConstellation(constellation);
        AbstractPerk root;
        if (constellation != null && (root = PerkTree.PERK_TREE.getRootPerk(constellation)) != null) {
            CompoundNBT data = new CompoundNBT();
            root.onUnlockPerkServer(player, progress, data);
            progress.applyPerk(root, data);
            //TODO perks
            //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Dist.DEDICATED_SERVER, root, false);
            PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(root, true));
        }

        //TODO advancements
        //AdvancementTriggers.ATTUNE_SELF.trigger((ServerPlayerEntity) player, constellation);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setPerkData(PlayerEntity player, @Nonnull AbstractPerk perk, CompoundNBT prevoiusData, CompoundNBT newData) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkEffect(perk)) return false;

        //TODO perks
        //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Dist.DEDICATED_SERVER, perk, true);
        progress.applyPerk(perk, newData);
        //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Dist.DEDICATED_SERVER, perk, false);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, prevoiusData, newData));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean applyPerk(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasFreeAllocationPoint(player)) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        CompoundNBT data = new CompoundNBT();
        perk.onUnlockPerkServer(player, progress, data);
        progress.applyPerk(perk, data);

        //TODO perks
        //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Dist.DEDICATED_SERVER, perk, false);
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, true));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean applyPerkSeal(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (progress.isPerkSealed(perk)) return false;

        if (!progress.sealPerk(perk)) {
            return false;
        }

        //TODO perks
        //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Dist.DEDICATED_SERVER, perk, true);
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, false));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean breakPerkSeal(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (!progress.isPerkSealed(perk)) return false;

        if (!progress.breakSeal(perk)) {
            return false;
        }

        //TODO perks
        //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Dist.DEDICATED_SERVER, perk, false);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);

        //Send way after research sync...
        AstralSorcery.getProxy().scheduleDelayed(() -> {
            PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, true));
        });
        return true;
    }

    public static boolean grantFreePerkPoint(PlayerEntity player, String token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        if (!progress.grantFreeAllocationPoint(token)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean revokeFreePoint(PlayerEntity player, String token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        if (!progress.tryRevokeAllocationPoint(token)) {
            return false;
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean forceApplyPerk(PlayerEntity player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        CompoundNBT data = new CompoundNBT();
        perk.onUnlockPerkServer(player, progress, data);
        progress.applyPerk(perk, data);

        //TODO perks
        //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Dist.DEDICATED_SERVER, perk, false);
        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, true));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean removePerk(PlayerEntity player, AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        CompoundNBT data = progress.getPerkData(perk);
        if (data == null) {
            return false;
        }
        dropPerk(progress, player, Dist.DEDICATED_SERVER, perk, data);

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(perk, false));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean resetPerks(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        Map<AbstractPerk, CompoundNBT> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, CompoundNBT> perkEntry : perkCopy.entrySet()) {
            dropPerk(progress, player, Dist.DEDICATED_SERVER, perkEntry.getKey(), perkEntry.getValue());
        }

        PacketChannel.CHANNEL.sendToPlayer(player, new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL));

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    private static void dropPerk(PlayerProgress progress, PlayerEntity player, Dist side, AbstractPerk perk, CompoundNBT data) {
        progress.removePerk(perk);
        //TODO perks
        //PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, side, perk, true);
        perk.onRemovePerkServer(player, progress, data);
        progress.removePerkData(perk);
    }

    public static boolean setTomeReceived(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        progress.setTomeReceived();

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean setExp(PlayerEntity player, long exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        progress.setExp(exp);

        //TODO advancements
        //AdvancementTriggers.PERK_LEVEL.trigger((ServerPlayerEntity) player);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static boolean modifyExp(PlayerEntity player, double exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        progress.modifyExp(exp, player);

        //TODO advancements
        //AdvancementTriggers.PERK_LEVEL.trigger((ServerPlayerEntity) player);

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
        return true;
    }

    public static void forceMaximizeAll(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return;
        ProgressionTier before = progress.getTierReached();

        ResearchManager.discoverConstellations(ConstellationRegistry.getAllConstellations(), player);
        ResearchManager.maximizeTier(player);
        ResearchManager.forceMaximizeResearch(player);
        ResearchManager.setAttunedBefore(player, true);
        for (TargetObject to : SextantFinder.getSelectableTargets()) {
            progress.useTarget(to);
        }

        if(progress.getTierReached().isThisLater(before)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(progress.getTierReached());
            PacketChannel.CHANNEL.sendToPlayer(player, pkt);
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        ResearchHelper.savePlayerKnowledge(player);
    }

    public static boolean forceMaximizeResearch(PlayerEntity player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
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

    //TODO crafting???
    /*
    public static void informCraftingGridCompletion(PlayerEntity player, ItemStack out) {
        Item iOut = out.getItem();
        informCraft(player, out, iOut, Block.getBlockFromItem(iOut));
    }

    public static void informCraftingInfusionCompletion(TileStarlightInfuser infuser, ActiveInfusionTask recipe) {
        PlayerEntity crafter = recipe.tryGetCraftingPlayerServer();
        if(crafter == null) {
            AstralSorcery.log.warn("Infusion finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + infuser.getPos() + " in dim " + infuser.getWorld().provider.getDimension());
            return;
        }

        ItemStack out = recipe.getRecipeToCraft().getOutput(infuser);
        Item iOut = out.getItem();

        informCraft(crafter, out, iOut, Block.getBlockFromItem(iOut));
    }

    public static void informCraftingAltarCompletion(TileAltar altar, ActiveCraftingTask recipeToCraft) {
        PlayerEntity crafter = recipeToCraft.tryGetCraftingPlayerServer();
        if(!(crafter instanceof ServerPlayerEntity)) {
            AstralSorcery.log.warn("Crafting finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + altar.getPos() + " in dim " + altar.getWorld().provider.getDimension());
            return;
        }

        ItemStack out = recipeToCraft.getRecipeToCraft().getOutputForMatching();
        Item iOut = out.getItem();

        informCraft(crafter, out, iOut, Block.getBlockFromItem(iOut));

        AdvancementTriggers.ALTAR_CRAFT.trigger((ServerPlayerEntity) crafter, recipeToCraft.getRecipeToCraft());
    }

    private static void informCraft(PlayerEntity crafter, ItemStack crafted, Item itemCrafted, @Nullable Block iBlock) {
        if(iBlock != null) {
            if(iBlock instanceof BlockAltar) {
                giveProgressionIgnoreFail(crafter, ProgressionTier.BASIC_CRAFT);
                giveResearchIgnoreFail(crafter, ResearchProgression.BASIC_CRAFT);

                TileAltar.AltarLevel to = TileAltar.AltarLevel.values()[crafted.getItemDamage()];
                switch (to) {
                    case ATTUNEMENT:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.ATTUNEMENT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.ATTUNEMENT);
                        break;
                    case CONSTELLATION_CRAFT:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.CONSTELLATION_CRAFT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.CONSTELLATION);
                        break;
                    case TRAIT_CRAFT:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.TRAIT_CRAFT);
                        giveResearchIgnoreFail(crafter, ResearchProgression.RADIANCE);
                        break;
                    case BRILLIANCE:
                        giveProgressionIgnoreFail(crafter, ProgressionTier.BRILLIANCE);
                        giveResearchIgnoreFail(crafter, ResearchProgression.BRILLIANCE);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    */

}
