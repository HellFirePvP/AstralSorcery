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
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

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
    private Map<AbstractPerk, Integer> unlockedPerks = new HashMap<>();
    private double perkExp = 0;

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        researchProgression.clear();
        usedTargets.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        wasOnceAttuned = false;
        unlockedPerks.clear();
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
        if(compound.hasKey("perks")) {
            NBTTagList list = compound.getTagList("perks", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                String perkRegName = tag.getString("perkName");
                AbstractPerk perk = PerkTree.INSTANCE.getPerk(new ResourceLocation(perkRegName));
                Integer unlockLevel = tag.getInteger("perkLevel");
                if(perk != null) {
                    unlockedPerks.put(perk, unlockLevel);
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
        for (AbstractPerk perk : unlockedPerks.keySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("perkName", perk.getRegistryName().toString());
            tag.setInteger("perkLevel", unlockedPerks.get(perk));
            list.appendTag(tag);
        }
        cmp.setTag("perks", list);

        list = new NBTTagList();
        for (SextantFinder.TargetObject to : usedTargets) {
            list.appendTag(new NBTTagString(to.getRegistryName()));
        }
        cmp.setTag("sextanttargets", list);

        cmp.setDouble("perkExp", perkExp);
    }

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

    public void loadKnowledge(NBTTagCompound compound) {
        knownConstellations.clear();
        researchProgression.clear();
        usedTargets.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        wasOnceAttuned = false;
        unlockedPerks.clear();
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

    public Map<AbstractPerk, Integer> getAppliedPerks() {
        return unlockedPerks == null ? Maps.newHashMap() : Collections.unmodifiableMap(unlockedPerks);
    }

    public boolean hasPerkUnlocked(AbstractPerk perk) {
        return unlockedPerks.containsKey(perk);
    }

    public void addPerk(AbstractPerk perk, Integer alignmentLevelUnlocked) {
        this.unlockedPerks.put(perk, alignmentLevelUnlocked);
    }

    public void clearPerks() {
        this.unlockedPerks.clear();
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

    // -1 -> no free level
    public int getNextFreeLevel() {
        int level = ConstellationPerkLevelManager.INSTANCE.getLevel(MathHelper.floor(getPerkExp()));
        for (int i = 1; i <= level; i++) {
            if(!unlockedPerks.values().contains(i)) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasFreeAlignmentLevel() {
        return getNextFreeLevel() > -1;
    }

    public double getPerkExp() {
        return perkExp;
    }

    protected void modifyExp(double exp) {
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
        this.unlockedPerks = message.usedPerks;
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
