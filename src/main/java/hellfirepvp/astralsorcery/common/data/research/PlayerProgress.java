/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerProgress
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:34
 */
public class PlayerProgress {

    private List<String> knownConstellations = new ArrayList<>();
    private List<String> seenConstellations = new ArrayList<>();
    private IMajorConstellation attunedConstellation = null;
    private boolean wasOnceAttuned = false;
    private List<ResearchProgression> researchProgression = new LinkedList<>();
    private List<SextantFinder.TargetObject> usedTargets = new LinkedList<>();
    private ProgressionTier tierReached = ProgressionTier.DISCOVERY;
    private List<String> freePointTokens = Lists.newArrayList();
    private Map<AbstractPerk, NBTTagCompound> unlockedPerks = new HashMap<>();
    private List<AbstractPerk> sealedPerks = new ArrayList<>();
    private double perkExp = 0;

    //Loading from flat-file, persistent data
    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        seenConstellations.clear();
        researchProgression.clear();
        usedTargets.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        wasOnceAttuned = false;
        unlockedPerks.clear();
        sealedPerks.clear();
        perkExp = 0;

        if (compound.hasKey("seenConstellations")) {
            NBTTagList list = compound.getTagList("seenConstellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                seenConstellations.add(list.getStringTagAt(i));
            }
        }
        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                String s = list.getStringTagAt(i);
                knownConstellations.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }

        if (compound.hasKey("attuned")) {
            String cst = compound.getString("attuned");
            IConstellation c = ConstellationRegistry.getConstellationByName(cst);
            if(c == null || !(c instanceof IMajorConstellation)) {
                AstralSorcery.log.warn("[AstralSorcery] Failed to load attuned Constellation: " + cst + " - constellation doesn't exist or isn't major.");
            } else {
                attunedConstellation = (IMajorConstellation) c;
            }
        }

        int perkTreeLevel = compound.getInteger("perkTreeVersion");
        if (perkTreeLevel < PerkTree.PERK_TREE_VERSION) { //If your perk tree version is outdated, clear it.
            AstralSorcery.log.info("Clearing perk-tree because the player's skill-tree version was outdated!");
            if (attunedConstellation != null) {
                AbstractPerk root = PerkTree.PERK_TREE.getRootPerk(attunedConstellation);
                if (root != null) {
                    NBTTagCompound data = new NBTTagCompound();
                    root.onUnlockPerkServer(null, this, data);
                    unlockedPerks.put(root, data);
                }
            }
        } else {
            if(compound.hasKey("perks")) {
                NBTTagList list = compound.getTagList("perks", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    String perkRegName = tag.getString("perkName");
                    NBTTagCompound data = tag.getCompoundTag("perkData");
                    AbstractPerk perk = PerkTree.PERK_TREE.getPerk(new ResourceLocation(perkRegName));
                    if(perk != null) {
                        unlockedPerks.put(perk, data);
                    }
                }
            }
            if(compound.hasKey("sealedPerks")) {
                NBTTagList list = compound.getTagList("perks", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    String perkRegName = tag.getString("perkName");
                    AbstractPerk perk = PerkTree.PERK_TREE.getPerk(new ResourceLocation(perkRegName));
                    if(perk != null) {
                        sealedPerks.add(perk);
                    }
                }
            }
        }

        if (compound.hasKey("tierReached")) {
            int tierOrdinal = compound.getInteger("tierReached");
            tierReached = ProgressionTier.values()[MathHelper.clamp(tierOrdinal, 0, ProgressionTier.values().length - 1)];
        }

        if (compound.hasKey("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                ResearchProgression prog = ResearchProgression.getById(resOrdinal);
                if (prog != null) {
                    researchProgression.add(prog);
                }
            }
        }

        if (compound.hasKey("sextanttargets")) {
            NBTTagList list = compound.getTagList("sextanttargets", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.tagCount(); i++) {
                String strTarget = list.getStringTagAt(i);
                SextantFinder.TargetObject to = SextantFinder.getByName(strTarget);
                if (to != null && !this.usedTargets.contains(to)) {
                    this.usedTargets.add(to);
                }
            }
        }

        this.wasOnceAttuned = compound.getBoolean("wasAttuned");

        if (compound.hasKey("perkExp")) {
            this.perkExp = compound.getDouble("perkExp");
        }
    }

    //For file saving, persistent saving.
    public void store(NBTTagCompound cmp) {
        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        NBTTagList l = new NBTTagList();
        for (String s : seenConstellations) {
            l.appendTag(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);
        cmp.setTag("seenConstellations", l);
        cmp.setInteger("tierReached", tierReached.ordinal());
        cmp.setBoolean("wasAttuned", wasOnceAttuned);
        int[] researchArray = new int[researchProgression.size()];
        for (int i = 0; i < researchProgression.size(); i++) {
            ResearchProgression progression = researchProgression.get(i);
            researchArray[i] = progression.getProgressId();
        }
        cmp.setIntArray("research", researchArray);
        if(attunedConstellation != null) {
            cmp.setString("attuned", attunedConstellation.getUnlocalizedName());
        }
        list = new NBTTagList();
        for (Map.Entry<AbstractPerk, NBTTagCompound> entry : unlockedPerks.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("perkName", entry.getKey().getRegistryName().toString());
            tag.setTag("perkData", entry.getValue());
            list.appendTag(tag);
        }
        cmp.setTag("perks", list);
        list = new NBTTagList();
        for (AbstractPerk perk : sealedPerks) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("perkName", perk.getRegistryName().toString());
            list.appendTag(tag);
        }
        cmp.setTag("sealedPerks", list);
        cmp.setInteger("perkTreeVersion", PerkTree.PERK_TREE_VERSION);

        list = new NBTTagList();
        for (SextantFinder.TargetObject to : usedTargets) {
            list.appendTag(new NBTTagString(to.getRegistryName()));
        }
        cmp.setTag("sextanttargets", list);

        cmp.setDouble("perkExp", perkExp);
    }

    //For knowledge sharing; some information is not important to be shared.
    public void storeKnowledge(NBTTagCompound cmp) {
        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        NBTTagList l = new NBTTagList();
        for (String s : seenConstellations) {
            l.appendTag(new NBTTagString(s));
        }
        NBTTagList listTargets = new NBTTagList();
        for (SextantFinder.TargetObject to : usedTargets) {
            listTargets.appendTag(new NBTTagString(to.getRegistryName()));
        }
        cmp.setTag("constellations", list);
        cmp.setTag("seenConstellations", l);
        cmp.setTag("sextanttargets", listTargets);
        cmp.setInteger("tierReached", tierReached.ordinal());
        cmp.setBoolean("wasAttuned", wasOnceAttuned);
        int[] researchArray = new int[researchProgression.size()];
        for (int i = 0; i < researchProgression.size(); i++) {
            ResearchProgression progression = researchProgression.get(i);
            researchArray[i] = progression.getProgressId();
        }
        cmp.setIntArray("research", researchArray);
    }

    //For knowledge sharing; some information is not important to be shared.
    public void loadKnowledge(NBTTagCompound compound) {
        knownConstellations.clear();
        researchProgression.clear();
        usedTargets.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        wasOnceAttuned = false;
        unlockedPerks.clear();
        sealedPerks.clear();
        perkExp = 0;

        if (compound.hasKey("seenConstellations")) {
            NBTTagList list = compound.getTagList("seenConstellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                seenConstellations.add(list.getStringTagAt(i));
            }
        }
        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                String s = list.getStringTagAt(i);
                knownConstellations.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }

        if (compound.hasKey("tierReached")) {
            int tierOrdinal = compound.getInteger("tierReached");
            tierReached = ProgressionTier.values()[MathHelper.clamp(tierOrdinal, 0, ProgressionTier.values().length - 1)];
        }

        if (compound.hasKey("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                ResearchProgression prog = ResearchProgression.getById(resOrdinal);
                if (prog != null) {
                    researchProgression.add(prog);
                }
            }
        }

        if (compound.hasKey("sextanttargets")) {
            NBTTagList list = compound.getTagList("sextanttargets", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.tagCount(); i++) {
                String strTarget = list.getStringTagAt(i);
                SextantFinder.TargetObject to = SextantFinder.getByName(strTarget);
                if (to != null && !this.usedTargets.contains(to)) {
                    this.usedTargets.add(to);
                }
            }
        }

        this.wasOnceAttuned = compound.getBoolean("wasAttuned");
    }

    protected boolean forceGainResearch(ResearchProgression progression) {
        if(!researchProgression.contains(progression)) {
            researchProgression.add(progression);
            return true;
        }
        return false;
    }

    protected void setAttunedConstellation(IMajorConstellation constellation) {
        this.attunedConstellation = constellation;
        this.wasOnceAttuned = true;
    }

    public Collection<AbstractPerk> getAppliedPerks() {
        return unlockedPerks == null ? Lists.newArrayList() : Collections.unmodifiableCollection(unlockedPerks.keySet());
    }

    public List<AbstractPerk> getSealedPerks() {
        return sealedPerks == null ? Lists.newArrayList() : Collections.unmodifiableList(sealedPerks);
    }

    public Map<AbstractPerk, NBTTagCompound> getUnlockedPerkData() {
        return unlockedPerks == null ? Maps.newHashMap() : Collections.unmodifiableMap(unlockedPerks);
    }

    @Nullable
    protected NBTTagCompound getPerkData(AbstractPerk perk) {
        return unlockedPerks.get(perk);
    }

    public boolean hasPerkEffect(AbstractPerk perk) {
        return hasPerkUnlocked(perk) && !isPerkSealed(perk);
    }

    public boolean hasPerkUnlocked(AbstractPerk perk) {
        return unlockedPerks.containsKey(perk);
    }

    public boolean isPerkSealed(AbstractPerk perk) {
        return sealedPerks.contains(perk);
    }

    public void putPerk(AbstractPerk perk, NBTTagCompound data) {
        this.unlockedPerks.put(perk, data);
    }

    protected boolean removePerk(AbstractPerk perk) {
        return unlockedPerks.remove(perk) != null &&
                (!sealedPerks.contains(perk) || sealedPerks.remove(perk));
    }

    protected boolean sealPerk(AbstractPerk perk) {
        if (sealedPerks.contains(perk) || !hasPerkUnlocked(perk)) {
            return false;
        }
        return sealedPerks.add(perk);
    }

    protected boolean breakSeal(AbstractPerk perk) {
        if (!sealedPerks.contains(perk) || !hasPerkUnlocked(perk)) {
            return false;
        }
        return sealedPerks.remove(perk);
    }

    public List<ResearchProgression> getResearchProgression() {
        researchProgression.removeIf(Objects::isNull);
        return Lists.newLinkedList(researchProgression);
    }

    public List<SextantFinder.TargetObject> getUsedTargets() {
        return usedTargets;
    }

    public void useTarget(SextantFinder.TargetObject target) {
        if (!this.usedTargets.contains(target)) {
            this.usedTargets.add(target);
        }
    }

    public ProgressionTier getTierReached() {
        return tierReached;
    }

    public IMajorConstellation getAttunedConstellation() {
        return attunedConstellation;
    }

    public boolean wasOnceAttuned() {
        return wasOnceAttuned;
    }

    protected void setAttunedBefore(boolean attuned) {
        this.wasOnceAttuned = attuned;
    }

    protected boolean grantFreeAllocationPoint(String freePointToken) {
        if (this.freePointTokens.contains(freePointToken)) {
            return false;
        }
        this.freePointTokens.add(freePointToken);
        return true;
    }

    protected boolean tryRevokeAllocationPoint(String token) {
        return this.freePointTokens.remove(token);
    }

    public List<String> getFreePointTokens() {
        return Collections.unmodifiableList(freePointTokens);
    }

    public int getAvailablePerkPoints() {
        int allocatedPerks = this.unlockedPerks.size() - 1; //Root perk doesn't count
        int allocationLevels = PerkLevelManager.INSTANCE.getLevel(MathHelper.floor(getPerkExp()));
        return (allocationLevels + this.freePointTokens.size()) - allocatedPerks;
    }

    public boolean hasFreeAllocationPoint() {
        return getAvailablePerkPoints() > 0;
    }

    public double getPerkExp() {
        return perkExp;
    }

    protected void modifyExp(double exp) {
        int currLevel = PerkLevelManager.INSTANCE.getLevel(MathHelper.floor(getPerkExp()));
        if (exp >= 0 && currLevel >= PerkLevelManager.INSTANCE.getLevelCap()) {
            return;
        }
        int expThisLevel = PerkLevelManager.INSTANCE.getExpForLevel(currLevel);
        int expNextLevel = PerkLevelManager.INSTANCE.getExpForLevel(currLevel + 1);
        int cap = MathHelper.floor(((float) (expNextLevel - expThisLevel)) * 0.08F);
        if (exp > cap) {
            exp = cap;
        }

        this.perkExp = Math.max(this.perkExp + exp, 0);
    }

    protected void setExp(double exp) {
        this.perkExp = Math.max(exp, 0);
    }

    protected boolean stepTier() {
        if(getTierReached().hasNextTier()) {
            setTierReached(ProgressionTier.values()[getTierReached().ordinal() + 1]);
            return true;
        }
        return false;
    }

    protected void setTierReached(ProgressionTier tier) {
        this.tierReached = tier;
    }

    public List<String> getKnownConstellations() {
        return knownConstellations;
    }

    public List<String> getSeenConstellations() {
        return seenConstellations;
    }

    public boolean hasConstellationDiscovered(IConstellation constellation) {
        return hasConstellationDiscovered(constellation.getUnlocalizedName());
    }

    public boolean hasConstellationDiscovered(String constellation) {
        return knownConstellations.contains(constellation);
    }

    protected void discoverConstellation(String name) {
        memorizeConstellation(name);
        if (!knownConstellations.contains(name)) knownConstellations.add(name);
    }

    protected void memorizeConstellation(String name) {
        if (!seenConstellations.contains(name)) seenConstellations.add(name);
    }

    protected void receive(PktSyncKnowledge message) {
        this.knownConstellations = message.knownConstellations;
        this.seenConstellations = message.seenConstellations;
        this.researchProgression = message.researchProgression;
        this.tierReached = ProgressionTier.values()[MathHelper.clamp(message.progressTier, 0, ProgressionTier.values().length - 1)];
        this.attunedConstellation = message.attunedConstellation;
        this.wasOnceAttuned = message.wasOnceAttuned;
        this.usedTargets = message.usedTargets;
        this.freePointTokens = message.freePointTokens;
        this.unlockedPerks = message.usedPerks;
        this.sealedPerks = message.sealedPerks;
        this.perkExp = message.perkExp;
    }

    public void acceptMergeFrom(PlayerProgress toMergeFrom) {
        for (String seen : toMergeFrom.seenConstellations) {
            memorizeConstellation(seen);
        }
        for (String known : toMergeFrom.knownConstellations) {
            discoverConstellation(known);
        }
        if(toMergeFrom.wasOnceAttuned) {
            this.wasOnceAttuned = true;
        }
        if(toMergeFrom.tierReached.isThisLaterOrEqual(this.tierReached)) {
            this.tierReached = toMergeFrom.tierReached;
        }
        for (ResearchProgression prog : toMergeFrom.researchProgression) {
            this.forceGainResearch(prog);
        }
        for (SextantFinder.TargetObject target : toMergeFrom.usedTargets) {
            this.useTarget(target);
        }
    }

}
