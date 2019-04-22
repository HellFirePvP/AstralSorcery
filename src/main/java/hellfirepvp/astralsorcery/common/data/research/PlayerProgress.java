/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

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
    private boolean tomeReceived = false;

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
        freePointTokens.clear();
        perkExp = 0;
        tomeReceived = false;

        if (compound.hasKey("seenConstellations")) {
            NBTTagList list = compound.getList("seenConstellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                seenConstellations.add(list.getString(i));
            }
        }
        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getList("constellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                String s = list.getString(i);
                knownConstellations.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }

        if (compound.hasKey("attuned")) {
            String cst = compound.getString("attuned");
            IConstellation c = ConstellationRegistry.getConstellationByName(cst);
            if(!(c instanceof IMajorConstellation)) {
                AstralSorcery.log.warn("Failed to load attuned Constellation: " + cst + " - constellation doesn't exist or isn't major.");
            } else {
                attunedConstellation = (IMajorConstellation) c;
            }
        }

        int perkTreeLevel = compound.getInt("perkTreeVersion");
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
                NBTTagList list = compound.getList("perks", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    NBTTagCompound tag = list.getCompound(i);
                    String perkRegName = tag.getString("perkName");
                    NBTTagCompound data = tag.getCompound("perkData");
                    AbstractPerk perk = PerkTree.PERK_TREE.getPerk(new ResourceLocation(perkRegName));
                    if(perk != null) {
                        unlockedPerks.put(perk, data);
                    }
                }
            }
            if(compound.hasKey("sealedPerks")) {
                NBTTagList list = compound.getList("sealedPerks", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    NBTTagCompound tag = list.getCompound(i);
                    String perkRegName = tag.getString("perkName");
                    AbstractPerk perk = PerkTree.PERK_TREE.getPerk(new ResourceLocation(perkRegName));
                    if(perk != null) {
                        sealedPerks.add(perk);
                    }
                }
            }

            if (compound.hasKey("pointTokens")) {
                NBTTagList list = compound.getList("pointTokens", Constants.NBT.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    this.freePointTokens.add(list.getString(i));
                }
            }
        }

        if (compound.hasKey("tierReached")) {
            int tierOrdinal = compound.getInt("tierReached");
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
            NBTTagList list = compound.getList("sextanttargets", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                String strTarget = list.getString(i);
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

        if (!compound.hasKey("bookReceived")) {
            this.tomeReceived = true; //Legacy support for player progress files that do not have the tag yet.
        } else {
            this.tomeReceived = compound.getBoolean("bookReceived");
        }
    }

    //For file saving, persistent saving.
    public void store(NBTTagCompound cmp) {
        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.add(new NBTTagString(s));
        }
        NBTTagList l = new NBTTagList();
        for (String s : seenConstellations) {
            l.add(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);
        cmp.setTag("seenConstellations", l);
        cmp.setInt("tierReached", tierReached.ordinal());
        cmp.setBoolean("wasAttuned", wasOnceAttuned);
        NBTTagList listTokens = new NBTTagList();
        for (String s : freePointTokens) {
            listTokens.add(new NBTTagString(s));
        }
        cmp.setTag("pointTokens", listTokens);
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
            list.add(tag);
        }
        cmp.setTag("perks", list);
        list = new NBTTagList();
        for (AbstractPerk perk : sealedPerks) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("perkName", perk.getRegistryName().toString());
            list.add(tag);
        }
        cmp.setTag("sealedPerks", list);
        cmp.setInt("perkTreeVersion", PerkTree.PERK_TREE_VERSION);

        list = new NBTTagList();
        for (SextantFinder.TargetObject to : usedTargets) {
            list.add(new NBTTagString(to.getRegistryName()));
        }
        cmp.setTag("sextanttargets", list);

        cmp.setDouble("perkExp", perkExp);
        cmp.setBoolean("bookReceived", tomeReceived);
    }

    //For knowledge sharing; some information is not important to be shared.
    public void storeKnowledge(NBTTagCompound cmp) {
        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.add(new NBTTagString(s));
        }
        NBTTagList l = new NBTTagList();
        for (String s : seenConstellations) {
            l.add(new NBTTagString(s));
        }
        NBTTagList listTargets = new NBTTagList();
        for (SextantFinder.TargetObject to : usedTargets) {
            listTargets.add(new NBTTagString(to.getRegistryName()));
        }
        cmp.setTag("constellations", list);
        cmp.setTag("seenConstellations", l);
        cmp.setTag("sextanttargets", listTargets);
        cmp.setInt("tierReached", tierReached.ordinal());
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
        freePointTokens.clear();
        perkExp = 0;
        tomeReceived = false;

        if (compound.hasKey("seenConstellations")) {
            NBTTagList list = compound.getList("seenConstellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                seenConstellations.add(list.getString(i));
            }
        }
        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getList("constellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                knownConstellations.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }

        if (compound.hasKey("tierReached")) {
            int tierOrdinal = compound.getInt("tierReached");
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
            NBTTagList list = compound.getList("sextanttargets", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                String strTarget = list.getString(i);
                SextantFinder.TargetObject to = SextantFinder.getByName(strTarget);
                if (to != null && !this.usedTargets.contains(to)) {
                    this.usedTargets.add(to);
                }
            }
        }
        if (compound.hasKey("pointTokens")) {
            NBTTagList list = compound.getList("pointTokens", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                this.freePointTokens.add(list.getString(i));
            }
        }

        this.wasOnceAttuned = compound.getBoolean("wasAttuned");
    }

    public boolean isValid() {
        return true;
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
    public NBTTagCompound getPerkData(AbstractPerk perk) {
        NBTTagCompound tag = unlockedPerks.get(perk);
        return tag == null ? null : tag.copy();
    }

    public void setPerkData(AbstractPerk perk, NBTTagCompound data) {
        this.unlockedPerks.put(perk, data);
    }

    public boolean hasPerkEffect(Predicate<AbstractPerk> perkMatch) {
        AbstractPerk perk = MiscUtils.iterativeSearch(unlockedPerks.keySet(), perkMatch);
        return perk != null && hasPerkEffect(perk);
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

    public boolean didReceiveTome() {
        return tomeReceived;
    }

    protected void setTomeReceived() {
        this.tomeReceived = true;
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

    public int getAvailablePerkPoints(EntityPlayer player) {
        int allocatedPerks = this.unlockedPerks.size() - 1; //Root perk doesn't count
        int allocationLevels = PerkLevelManager.INSTANCE.getLevel(getPerkExp(), player);
        return (allocationLevels + this.freePointTokens.size()) - allocatedPerks;
    }

    public boolean hasFreeAllocationPoint(EntityPlayer player) {
        return getAvailablePerkPoints(player) > 0;
    }

    public double getPerkExp() {
        return perkExp;
    }

    public int getPerkLevel(EntityPlayer player) {
        return PerkLevelManager.INSTANCE.getLevel(getPerkExp(), player);
    }

    public float getPercentToNextLevel(EntityPlayer player) {
        return PerkLevelManager.INSTANCE.getNextLevelPercent(getPerkExp(), player);
    }

    protected void modifyExp(double exp, EntityPlayer player) {
        int currLevel = PerkLevelManager.INSTANCE.getLevel(getPerkExp(), player);
        if (exp >= 0 && currLevel >= PerkLevelManager.getLevelCapFor(player)) {
            return;
        }
        long expThisLevel = PerkLevelManager.INSTANCE.getExpForLevel(currLevel, player);
        long expNextLevel = PerkLevelManager.INSTANCE.getExpForLevel(currLevel + 1, player);
        long cap = MathHelper.lfloor(((float) (expNextLevel - expThisLevel)) * 0.08F);
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

    protected PlayerProgress copy() {
        PlayerProgress copy = new PlayerProgress();
        NBTTagCompound saveData = new NBTTagCompound();
        this.store(saveData);
        copy.load(saveData);
        return copy;
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
