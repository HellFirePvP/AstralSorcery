/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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

    public static void unsafeForceGiveResearch(EntityPlayerMP player, ResearchProgression prog) {
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
        PacketChannel.CHANNEL.sendTo(pkt, player);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
    }

    public static void giveResearchIgnoreFail(EntityPlayer player, ResearchProgression prog) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return;

        ProgressionTier tier = prog.getRequiredProgress();
        if(!progress.getTierReached().isThisLaterOrEqual(tier)) return;
        for (ResearchProgression other : prog.getPreConditions()) {
            if(!progress.getResearchProgression().contains(other)) return;
        }

        if(progress.forceGainResearch(prog)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(prog);
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
    }

    public static void giveProgressionIgnoreFail(EntityPlayer player, ProgressionTier tier) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return;

        ProgressionTier t = progress.getTierReached();
        if(!t.hasNextTier()) return; //No higher tier available anyway.
        ProgressionTier next = t.next();
        if(!next.equals(tier)) return; //Given one is not the next step.

        progress.setTierReached(next);
        PktProgressionUpdate pkt = new PktProgressionUpdate(next);
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
    }

    public static boolean useSextantTarget(SextantFinder.TargetObject to, EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.useTarget(to);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean discoverConstellations(Collection<IConstellation> csts, EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        for (IConstellation c : csts) {
            progress.discoverConstellation(c.getUnlocalizedName());
            AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((EntityPlayerMP) player, c);
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean discoverConstellation(IConstellation c, EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.discoverConstellation(c.getUnlocalizedName());

        AdvancementTriggers.DISCOVER_CONSTELLATION.trigger((EntityPlayerMP) player, c);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean memorizeConstellation(IConstellation c, EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.memorizeConstellation(c.getUnlocalizedName());

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean maximizeTier(EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.setTierReached(ProgressionTier.values()[ProgressionTier.values().length - 1]);

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setAttunedBefore(EntityPlayer player, boolean wasAttunedBefore) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        progress.setAttunedBefore(wasAttunedBefore);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setAttunedConstellation(EntityPlayer player, @Nullable IMajorConstellation constellation) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if(!progress.isValid()) return false;

        if (constellation != null && !progress.getKnownConstellations().contains(constellation.getUnlocalizedName())) {
            return false;
        }

        Map<AbstractPerk, NBTTagCompound> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, NBTTagCompound> perkEntry : perkCopy.entrySet()) {
            perkEntry.getKey().onRemovePerkServer(player, progress, perkEntry.getValue());
            progress.removePerk(perkEntry.getKey());
            PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perkEntry.getKey(), true);
        }

        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL), (EntityPlayerMP) player);

        progress.setExp(0);
        progress.setAttunedConstellation(constellation);
        AbstractPerk root;
        if (constellation != null && (root = PerkTree.PERK_TREE.getRootPerk(constellation)) != null) {
            NBTTagCompound data = new NBTTagCompound();
            root.onUnlockPerkServer(player, progress, data);
            progress.setPerkData(root, data);
            PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, root, false);
            PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(root, true), (EntityPlayerMP) player);
        }

        AdvancementTriggers.ATTUNE_SELF.trigger((EntityPlayerMP) player, constellation);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setPerkData(EntityPlayer player, @Nonnull AbstractPerk perk, NBTTagCompound prevoiusData, NBTTagCompound newData) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkEffect(perk)) return false;

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, true);
        progress.setPerkData(perk, newData);
        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);

        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, prevoiusData, newData), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean applyPerk(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasFreeAllocationPoint(player)) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        NBTTagCompound data = new NBTTagCompound();
        perk.onUnlockPerkServer(player, progress, data);
        progress.setPerkData(perk, data);

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);
        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, true), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean applyPerkSeal(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (progress.isPerkSealed(perk)) return false;

        if (!progress.sealPerk(perk)) {
            return false;
        }

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, true);
        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, false), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean breakPerkSeal(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (!progress.hasPerkUnlocked(perk)) return false;
        if (!progress.isPerkSealed(perk)) return false;

        if (!progress.breakSeal(perk)) {
            return false;
        }

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);

        //Send way after research sync...
        AstralSorcery.proxy.scheduleDelayed(() -> {
            PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, true), (EntityPlayerMP) player);
        });
        return true;
    }

    public static boolean grantFreePerkPoint(EntityPlayer player, String token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        if (!progress.grantFreeAllocationPoint(token)) {
            return false;
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean revokeFreePoint(EntityPlayer player, String token) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        if (!progress.tryRevokeAllocationPoint(token)) {
            return false;
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean forceApplyPerk(EntityPlayer player, @Nonnull AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        if (progress.hasPerkUnlocked(perk)) return false;

        NBTTagCompound data = new NBTTagCompound();
        perk.onUnlockPerkServer(player, progress, data);
        progress.setPerkData(perk, data);

        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, false);
        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, true), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean removePerk(EntityPlayer player, AbstractPerk perk) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        NBTTagCompound data = progress.getPerkData(perk);
        if (data == null) {
            return false;
        }
        perk.onRemovePerkServer(player, progress, data);
        progress.removePerk(perk);
        PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perk, true);

        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(perk, false), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean resetPerks(EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        Map<AbstractPerk, NBTTagCompound> perkCopy = new HashMap<>(progress.getUnlockedPerkData());
        for (Map.Entry<AbstractPerk, NBTTagCompound> perkEntry : perkCopy.entrySet()) {
            perkEntry.getKey().onRemovePerkServer(player, progress, perkEntry.getValue());
            progress.removePerk(perkEntry.getKey());
            PerkEffectHelper.EVENT_INSTANCE.notifyPerkChange(player, Side.SERVER, perkEntry.getKey(), true);
        }

        PacketChannel.CHANNEL.sendTo(new PktSyncPerkActivity(PktSyncPerkActivity.Type.CLEARALL), (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setTomeReceived(EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        progress.setTomeReceived();

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean setExp(EntityPlayer player, long exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        progress.setExp(exp);

        AdvancementTriggers.PERK_LEVEL.trigger((EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static boolean modifyExp(EntityPlayer player, double exp) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;

        progress.modifyExp(exp, player);

        AdvancementTriggers.PERK_LEVEL.trigger((EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static void forceMaximizeAll(EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return;
        ProgressionTier before = progress.getTierReached();

        ResearchManager.discoverConstellations(ConstellationRegistry.getAllConstellations(), player);
        ResearchManager.maximizeTier(player);
        ResearchManager.forceMaximizeResearch(player);
        ResearchManager.setAttunedBefore(player, true);
        for (SextantFinder.TargetObject to : SextantFinder.getSelectableTargets()) {
            progress.useTarget(to);
        }

        if(progress.getTierReached().isThisLater(before)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(progress.getTierReached());
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
    }

    public static boolean forceMaximizeResearch(EntityPlayer player) {
        PlayerProgress progress = ResearchHelper.getProgress(player, Dist.DEDICATED_SERVER);
        if (!progress.isValid()) return false;
        for (ResearchProgression progression : ResearchProgression.values()) {
            progress.forceGainResearch(progression);
        }

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe((EntityPlayerMP) player);
        savePlayerKnowledge((EntityPlayerMP) player);
        return true;
    }

    public static void informCraftingGridCompletion(EntityPlayer player, ItemStack out) {
        Item iOut = out.getItem();
        informCraft(player, out, iOut, Block.getBlockFromItem(iOut));
    }

    public static void informCraftingInfusionCompletion(TileStarlightInfuser infuser, ActiveInfusionTask recipe) {
        EntityPlayer crafter = recipe.tryGetCraftingPlayerServer();
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
        EntityPlayer crafter = recipeToCraft.tryGetCraftingPlayerServer();
        if(!(crafter instanceof EntityPlayerMP)) {
            AstralSorcery.log.warn("Crafting finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + altar.getPos() + " in dim " + altar.getWorld().provider.getDimension());
            return;
        }

        ItemStack out = recipeToCraft.getRecipeToCraft().getOutputForMatching();
        Item iOut = out.getItem();

        informCraft(crafter, out, iOut, Block.getBlockFromItem(iOut));

        AdvancementTriggers.ALTAR_CRAFT.trigger((EntityPlayerMP) crafter, recipeToCraft.getRecipeToCraft());
    }

    private static void informCraft(EntityPlayer crafter, ItemStack crafted, Item itemCrafted, @Nullable Block iBlock) {
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

}
