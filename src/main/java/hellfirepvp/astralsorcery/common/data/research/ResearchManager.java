package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.infusion.ActiveInfusionTask;
import hellfirepvp.astralsorcery.common.item.ItemHandTelescope;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktProgressionUpdate;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import hellfirepvp.astralsorcery.common.registry.RegistryAchievements;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchManager
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:33
 */
public class ResearchManager {

    public static PlayerProgress clientProgress = new PlayerProgress();

    private static Map<UUID, PlayerProgress> playerProgressServer = new HashMap<>();

    @Nullable
    public static PlayerProgress getProgress(EntityPlayer player, Side side) {
        if(side == Side.CLIENT) {
            return clientProgress;
        } else {
            return getProgress(player);
        }
    }

    @Nullable
    public static PlayerProgress getProgress(EntityPlayer player) {
        return getProgress(player.getUniqueID());
    }

    @Nullable
    public static PlayerProgress getProgress(UUID uuid) {
        PlayerProgress progress = playerProgressServer.get(uuid);
        if (progress == null) {
            loadPlayerKnowledge(uuid);
            progress = playerProgressServer.get(uuid);
        }
        if (progress == null) {
            AstralSorcery.log.warn("Failed to load AstralSocery Progress data!");
            AstralSorcery.log.warn("Erroneous file: " + uuid.toString() + ".astral");
        }
        return progress;
    }

    public static void wipeKnowledge(EntityPlayer p) {
        wipeFile(p);
        playerProgressServer.remove(p.getUniqueID());
        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, (net.minecraft.entity.player.EntityPlayerMP) p);
        PktSyncKnowledge pk = new PktSyncKnowledge(PktSyncKnowledge.STATE_WIPE);
        PacketChannel.CHANNEL.sendTo(pk, (net.minecraft.entity.player.EntityPlayerMP) p);
    }

    public static void sendInitClientKnowledge(EntityPlayer p) {
        UUID uuid = p.getUniqueID();
        if (playerProgressServer.get(uuid) == null) {
            loadPlayerKnowledge(uuid);
        }
        if (playerProgressServer.get(uuid) == null) {
            AstralSorcery.log.warn("Failed to load AstralSocery Progress data for " + p.getName());
            AstralSorcery.log.warn("Erroneous file: " + uuid.toString() + ".astral");
            return;
        }
        pushProgressToClientUnsafe(p);
    }

    public static void unsafeForceGiveResearch(EntityPlayer player, ResearchProgression prog) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return;

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
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
    }

    public static void giveResearchIgnoreFail(EntityPlayer player, ResearchProgression prog) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return;

        ProgressionTier tier = prog.getRequiredProgress();
        if(!progress.getTierReached().isThisLaterOrEqual(tier)) return;
        for (ResearchProgression other : prog.getPreConditions()) {
            if(!progress.getResearchProgression().contains(other)) return;
        }

        if(progress.forceGainResearch(prog)) {
            PktProgressionUpdate pkt = new PktProgressionUpdate(prog);
            PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);
        }

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
    }

    public static void giveProgressionIgnoreFail(EntityPlayer player, ProgressionTier tier) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return;

        ProgressionTier t = progress.getTierReached();
        if(!t.hasNextTier()) return; //No higher tier available anyway.
        ProgressionTier next = t.next();
        if(!next.equals(tier)) return; //Given one is not the next step.

        progress.setTierReached(next);
        PktProgressionUpdate pkt = new PktProgressionUpdate(next);
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
    }

    public static boolean discoverConstellations(Collection<IConstellation> csts, EntityPlayer player) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;

        for (IConstellation c : csts) {
            progress.discoverConstellation(c.getUnlocalizedName());
        }

        player.addStat(RegistryAchievements.achvDiscoverConstellation);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    public static boolean discoverConstellation(IConstellation c, EntityPlayer player) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;

        progress.discoverConstellation(c.getUnlocalizedName());

        player.addStat(RegistryAchievements.achvDiscoverConstellation);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    public static boolean maximizeTier(EntityPlayer player) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;
        progress.setTierReached(ProgressionTier.values()[ProgressionTier.values().length - 1]);

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    public static boolean setAttunedConstellation(EntityPlayer player, @Nullable IMajorConstellation constellation) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;

        progress.clearPerks();
        progress.forceCharge(0);
        progress.setAttunedConstellation(constellation);

        player.addStat(RegistryAchievements.achvPlayerAttunement);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    public static boolean applyPerk(EntityPlayer player, @Nonnull ConstellationPerks perk) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;
        if(!progress.hasFreeAlignmentLevel()) return false;
        if(progress.hasPerkUnlocked(perk)) return false;

        int free = progress.getNextFreeLevel();
        if(free == -1) return false;
        progress.addPerk(perk.getSingleInstance(), free);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    public static boolean resetPerks(EntityPlayer player) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;

        progress.clearPerks();

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    public static boolean forceCharge(EntityPlayer player, int charge) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;

        progress.forceCharge(charge);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    public static boolean modifyAlignmentCharge(EntityPlayer player, double charge) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;

        progress.modifyCharge(MathHelper.floor(charge));

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    /**
     * Returns Optional ProgressionTier:
     *
     * Non-present: player is at max.
     * null: no playerdata found.
     * some progression: New progression reached.
     */
    /*public static Optional<ProgressionTier> stepTier(EntityPlayer player) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return Optional.of(null);
        if(!progress.stepTier()) {
            return Optional.empty();
        }

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return Optional.of(progress.getTierReached());
    }*/

    /*protected static boolean forceUnsafeResearchStep(EntityPlayer player, ResearchProgression progression) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;
        progress.forceGainResearch(progression);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }*/

    public static boolean forceMaximizeResearch(EntityPlayer player) {
        PlayerProgress progress = getProgress(player);
        if(progress == null) return false;
        for (ResearchProgression progression : ResearchProgression.values()) {
            progress.forceGainResearch(progression);
        }

        PktProgressionUpdate pkt = new PktProgressionUpdate();
        PacketChannel.CHANNEL.sendTo(pkt, (EntityPlayerMP) player);

        pushProgressToClientUnsafe(player);
        savePlayerKnowledge(player);
        return true;
    }

    private static void pushProgressToClientUnsafe(EntityPlayer p) {
        PlayerProgress progress = playerProgressServer.get(p.getUniqueID());
        PktSyncKnowledge pkt = new PktSyncKnowledge(PktSyncKnowledge.STATE_ADD);
        pkt.load(progress);
        PacketChannel.CHANNEL.sendTo(pkt, (net.minecraft.entity.player.EntityPlayerMP) p);
    }

    private static void wipeFile(EntityPlayer player) {
        new File(getPlayerDirectory(), player.getUniqueID().toString() + ".astral").delete();
    }

    public static void savePlayerKnowledge(EntityPlayer p) {
        savePlayerKnowledge(p.getUniqueID());
    }

    public static void savePlayerKnowledge(UUID pUUID) {
        if (playerProgressServer.get(pUUID) == null) return;
        String uuidStr = pUUID.toString();
        File dir = getPlayerDirectory();
        File playerFile = new File(dir, uuidStr + ".astral");
        try {
            NBTTagCompound cmp = new NBTTagCompound();
            playerProgressServer.get(pUUID).store(cmp);
            CompressedStreamTools.write(cmp, playerFile);
        } catch (IOException e) {}
    }

    public static void loadPlayerKnowledge(EntityPlayer p) {
        loadPlayerKnowledge(p.getUniqueID());
    }

    public static void loadPlayerKnowledge(UUID pUUID) {
        String uuidStr = pUUID.toString();
        File dir = getPlayerDirectory();
        File playerFile = new File(dir, uuidStr + ".astral");
        try {
            NBTTagCompound compound = CompressedStreamTools.read(playerFile);
            PlayerProgress progress = new PlayerProgress();
            if (compound != null) {
                progress.load(compound);
            }
            progress.forceGainResearch(ResearchProgression.DISCOVERY);

            playerProgressServer.put(pUUID, progress);
        } catch (IOException e) {}
    }

    private static File getPlayerDirectory() {
        File wDir = DimensionManager.getWorld(0).getSaveHandler().getWorldDirectory();
        File pDir = new File(wDir, "playerdata");
        if (!pDir.exists()) {
            pDir.mkdirs();
        }
        return pDir;
    }

    public static void saveAndClearServerCache() {
        playerProgressServer.keySet().forEach(hellfirepvp.astralsorcery.common.data.research.ResearchManager::savePlayerKnowledge);
        playerProgressServer.clear();
    }

    /*public static void logoutResetClient(EntityPlayer player) {
        PktSyncKnowledge pkt = new PktSyncKnowledge(PktSyncKnowledge.STATE_WIPE);
        PacketChannel.CHANNEL.sendTo(pkt, (net.minecraft.entity.player.EntityPlayerMP) player);
    }*/

    public static void recieveProgressFromServer(PktSyncKnowledge message) {
        clientProgress = new PlayerProgress();
        clientProgress.receive(message);
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
        if(crafter == null) {
            AstralSorcery.log.warn("Crafting finished, player that initialized crafting could not be found!");
            AstralSorcery.log.warn("Affected tile: " + altar.getPos() + " in dim " + altar.getWorld().provider.getDimension());
            return;
        }

        ItemStack out = recipeToCraft.getRecipeToCraft().getOutputForRender();
        Item iOut = out.getItem();

        informCraft(crafter, out, iOut, Block.getBlockFromItem(iOut));
    }

    private static void informCraft(EntityPlayer crafter, ItemStack crafted, Item itemCrafted, @Nullable Block iBlock) {

        if(itemCrafted instanceof ItemHandTelescope) {
            crafter.addStat(RegistryAchievements.achvBuildHandTelescope);
        }
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
                        break;
                    case ENDGAME:
                        break;
                }
            }
        } else {

        }
    }

}
