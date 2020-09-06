/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncKnowledge;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkLevelManager;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerProgress
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:34
 */
public class PlayerProgress {

    private List<ResourceLocation> knownConstellations = new ArrayList<>();
    private List<ResourceLocation> seenConstellations = new ArrayList<>();
    private List<ResourceLocation> storedConstellationPapers = new ArrayList<>();
    private IMajorConstellation attunedConstellation = null;
    private boolean wasOnceAttuned = false;
    private Set<ResearchProgression> researchProgression = new HashSet<>();
    private ProgressionTier tierReached = ProgressionTier.DISCOVERY;
    private List<String> freePointTokens = Lists.newArrayList();
    private Set<AbstractPerk> appliedPerks = new HashSet<>();
    private Map<AbstractPerk, CompoundNBT> appliedPerkData = new HashMap<>();
    private List<AbstractPerk> sealedPerks = new ArrayList<>();
    private double perkExp = 0;
    private boolean tomeReceived = false;
    private boolean usePerkAbilities = true; //Move this out of this class at some point.. this doesn't actually belong here

    //Loading from flat-file, persistent data
    public void load(CompoundNBT compound) {
        knownConstellations.clear();
        seenConstellations.clear();
        researchProgression.clear();
        storedConstellationPapers.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        wasOnceAttuned = false;
        appliedPerks.clear();
        appliedPerkData.clear();
        sealedPerks.clear();
        freePointTokens.clear();
        perkExp = 0;
        tomeReceived = false;
        usePerkAbilities = true;

        if (compound.contains("seenConstellations")) {
            ListNBT list = compound.getList("seenConstellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                seenConstellations.add(new ResourceLocation(list.getString(i)));
            }
        }
        if (compound.contains("constellations")) {
            ListNBT list = compound.getList("constellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation s = new ResourceLocation(list.getString(i));
                knownConstellations.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }
        if (compound.contains("storedConstellationPapers")) {
            ListNBT list = compound.getList("storedConstellationPapers", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation s = new ResourceLocation(list.getString(i));
                storedConstellationPapers.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }

        if (compound.contains("attuned")) {
            String cst = compound.getString("attuned");
            IConstellation c = ConstellationRegistry.getConstellation(new ResourceLocation(cst));
            if (!(c instanceof IMajorConstellation)) {
                AstralSorcery.log.warn("Failed to load attuned Constellation: " + cst + " - constellation doesn't exist or isn't major.");
            } else {
                attunedConstellation = (IMajorConstellation) c;
            }
        }

        long perkTreeLevel = compound.getLong("perkTreeVersion");
        if (PerkTree.PERK_TREE.getVersion(LogicalSide.SERVER).map(v -> !v.equals(perkTreeLevel)).orElse(true)) { //If your perk tree is different, clear it.
            AstralSorcery.log.info("Clearing perk-tree because the player's skill-tree version was outdated!");
            if (attunedConstellation != null) {
                AbstractPerk root = PerkTree.PERK_TREE.getRootPerk(LogicalSide.SERVER, attunedConstellation);
                if (root != null) {
                    CompoundNBT data = new CompoundNBT();
                    root.onUnlockPerkServer(null, this, data);
                    appliedPerks.add(root);
                    appliedPerkData.put(root, data);
                }
            }
        } else {
            if (compound.contains("perks")) {
                ListNBT list = compound.getList("perks", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT tag = list.getCompound(i);
                    String perkRegName = tag.getString("perkName");
                    CompoundNBT data = tag.getCompound("perkData");
                    PerkTree.PERK_TREE.getPerk(LogicalSide.SERVER, new ResourceLocation(perkRegName)).ifPresent(perk -> {
                        appliedPerks.add(perk);
                        appliedPerkData.put(perk, data);
                    });
                }
            }
            if (compound.contains("sealedPerks")) {
                ListNBT list = compound.getList("sealedPerks", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    CompoundNBT tag = list.getCompound(i);
                    String perkRegName = tag.getString("perkName");
                    PerkTree.PERK_TREE.getPerk(LogicalSide.SERVER, new ResourceLocation(perkRegName)).ifPresent(perk -> {
                        sealedPerks.add(perk);
                    });
                }
            }

            if (compound.contains("pointTokens")) {
                ListNBT list = compound.getList("pointTokens", Constants.NBT.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    this.freePointTokens.add(list.getString(i));
                }
            }
        }

        if (compound.contains("tierReached")) {
            int tierOrdinal = compound.getInt("tierReached");
            tierReached = MiscUtils.getEnumEntry(ProgressionTier.class, tierOrdinal);
        }

        if (compound.contains("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                researchProgression.add(MiscUtils.getEnumEntry(ResearchProgression.class, resOrdinal));
            }
        }

        this.wasOnceAttuned = compound.getBoolean("wasAttuned");

        if (compound.contains("perkExp")) {
            this.perkExp = compound.getDouble("perkExp");
        }

        if (!compound.contains("bookReceived")) {
            this.tomeReceived = true; //Legacy support for player progress files that do not have the tag yet.
        } else {
            this.tomeReceived = compound.getBoolean("bookReceived");
        }

        if (compound.contains("usePerkAbilities")) {
            this.usePerkAbilities = compound.getBoolean("usePerkAbilities");
        }
    }

    //For file saving, persistent saving.
    public void store(CompoundNBT cmp) {
        ListNBT known = new ListNBT();
        for (ResourceLocation s : knownConstellations) {
            known.add(StringNBT.valueOf(s.toString()));
        }
        ListNBT seen = new ListNBT();
        for (ResourceLocation s : seenConstellations) {
            seen.add(StringNBT.valueOf(s.toString()));
        }
        ListNBT storedPapers = new ListNBT();
        for (ResourceLocation s : storedConstellationPapers) {
            storedPapers.add(StringNBT.valueOf(s.toString()));
        }
        cmp.put("constellations", known);
        cmp.put("seenConstellations", seen);
        cmp.put("storedConstellationPapers", storedPapers);
        cmp.putInt("tierReached", tierReached.ordinal());
        cmp.putBoolean("wasAttuned", wasOnceAttuned);
        ListNBT listTokens = new ListNBT();
        for (String s : freePointTokens) {
            listTokens.add(StringNBT.valueOf(s));
        }
        cmp.put("pointTokens", listTokens);
        int[] researchArray = researchProgression.stream()
                .mapToInt(ResearchProgression::ordinal)
                .toArray();
        cmp.putIntArray("research", researchArray);
        if (attunedConstellation != null) {
            cmp.putString("attuned", attunedConstellation.getRegistryName().toString());
        }
        known = new ListNBT();
        for (Map.Entry<AbstractPerk, CompoundNBT> entry : appliedPerkData.entrySet()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("perkName", entry.getKey().getRegistryName().toString());
            tag.put("perkData", entry.getValue());
            known.add(tag);
        }
        cmp.put("perks", known);
        known = new ListNBT();
        for (AbstractPerk perk : sealedPerks) {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("perkName", perk.getRegistryName().toString());
            known.add(tag);
        }
        cmp.put("sealedPerks", known);
        PerkTree.PERK_TREE.getVersion(LogicalSide.SERVER).ifPresent(version -> cmp.putLong("perkTreeVersion", version));

        cmp.putDouble("perkExp", perkExp);
        cmp.putBoolean("bookReceived", tomeReceived);
        cmp.putBoolean("usePerkAbilities", usePerkAbilities);
    }

    //For knowledge sharing; some information is not important to be shared.
    public void storeKnowledge(CompoundNBT cmp) {
        ListNBT list = new ListNBT();
        for (ResourceLocation s : knownConstellations) {
            list.add(StringNBT.valueOf(s.toString()));
        }
        ListNBT l = new ListNBT();
        for (ResourceLocation s : seenConstellations) {
            l.add(StringNBT.valueOf(s.toString()));
        }
        cmp.put("constellations", list);
        cmp.put("seenConstellations", l);
        cmp.putInt("tierReached", tierReached.ordinal());
        cmp.putBoolean("wasAttuned", wasOnceAttuned);
        int[] researchArray = researchProgression.stream()
                .mapToInt(ResearchProgression::ordinal)
                .toArray();
        cmp.putIntArray("research", researchArray);
    }

    //For knowledge sharing; some information is not important to be shared.
    public void loadKnowledge(CompoundNBT compound) {
        knownConstellations.clear();
        researchProgression.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        wasOnceAttuned = false;
        appliedPerks.clear();
        appliedPerkData.clear();
        sealedPerks.clear();
        freePointTokens.clear();
        perkExp = 0;
        tomeReceived = false;

        if (compound.contains("seenConstellations")) {
            ListNBT list = compound.getList("seenConstellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                seenConstellations.add(new ResourceLocation(list.getString(i)));
            }
        }
        if (compound.contains("constellations")) {
            ListNBT list = compound.getList("constellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation s = new ResourceLocation(list.getString(i));

                knownConstellations.add(s);
                if (!seenConstellations.contains(s)) {
                    seenConstellations.add(s);
                }
            }
        }

        if (compound.contains("tierReached")) {
            int tierOrdinal = compound.getInt("tierReached");
            tierReached = MiscUtils.getEnumEntry(ProgressionTier.class, tierOrdinal);
        }

        if (compound.contains("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                researchProgression.add(MiscUtils.getEnumEntry(ResearchProgression.class, resOrdinal));
            }
        }

        if (compound.contains("pointTokens")) {
            ListNBT list = compound.getList("pointTokens", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                this.freePointTokens.add(list.getString(i));
            }
        }

        this.wasOnceAttuned = compound.getBoolean("wasAttuned");
    }

    public boolean isValid() {
        return true;
    }


    protected void setAttunedConstellation(IMajorConstellation constellation) {
        this.attunedConstellation = constellation;
        this.wasOnceAttuned = true;
    }

    public Collection<AbstractPerk> getAppliedPerks() {
        return appliedPerks == null ? Lists.newArrayList() : Collections.unmodifiableCollection(appliedPerks);
    }

    public List<AbstractPerk> getSealedPerks() {
        return sealedPerks == null ? Lists.newArrayList() : Collections.unmodifiableList(sealedPerks);
    }

    public Map<AbstractPerk, CompoundNBT> getUnlockedPerkData() {
        return appliedPerkData == null ? Maps.newHashMap() : Collections.unmodifiableMap(appliedPerkData);
    }

    @Nullable
    public CompoundNBT getPerkData(AbstractPerk perk) {
        CompoundNBT tag = appliedPerkData.get(perk);
        return tag == null ? null : tag.copy();
    }

    public boolean hasPerkEffect(Predicate<AbstractPerk> perkMatch) {
        AbstractPerk perk = MiscUtils.iterativeSearch(appliedPerks, perkMatch);
        return perk != null && hasPerkEffect(perk);
    }

    public boolean hasPerkEffect(AbstractPerk perk) {
        return hasPerkUnlocked(perk) && !isPerkSealed(perk);
    }

    public boolean hasPerkUnlocked(AbstractPerk perk) {
        return appliedPerks.contains(perk);
    }

    public boolean isPerkSealed(AbstractPerk perk) {
        return sealedPerks.contains(perk);
    }

    public void applyPerk(AbstractPerk perk, CompoundNBT data) {
        this.appliedPerks.add(perk);
        this.appliedPerkData.put(perk, data);
    }

    boolean removePerk(AbstractPerk perk) {
        return appliedPerks.remove(perk) && (!sealedPerks.contains(perk) || sealedPerks.remove(perk));
    }

    boolean removePerkData(AbstractPerk perk) {
        return appliedPerkData.remove(perk) != null;
    }

    protected boolean canSealPerk(AbstractPerk perk) {
        return !sealedPerks.contains(perk) && hasPerkUnlocked(perk);
    }

    protected boolean sealPerk(AbstractPerk perk) {
        if (!canSealPerk(perk)) {
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

    public Collection<ResearchProgression> getResearchProgression() {
        return Collections.unmodifiableCollection(researchProgression);
    }

    public boolean hasResearch(ResearchProgression progression) {
        return getResearchProgression().contains(progression);
    }

    protected boolean forceGainResearch(ResearchProgression progression) {
        return researchProgression.add(progression);
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

    public boolean doPerkAbilities() {
        return this.usePerkAbilities;
    }

    protected void setUsePerkAbilities(boolean usePerkAbilities) {
        this.usePerkAbilities = usePerkAbilities;
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

    public int getAvailablePerkPoints(PlayerEntity player, LogicalSide side) {
        int allocatedPerks = this.appliedPerks.size() - 1; //Root perk doesn't count
        int allocationLevels = PerkLevelManager.getLevel(getPerkExp(), player, side);
        return (allocationLevels + this.freePointTokens.size()) - allocatedPerks;
    }

    public boolean hasFreeAllocationPoint(PlayerEntity player, LogicalSide side) {
        return getAvailablePerkPoints(player, side) > 0;
    }

    public double getPerkExp() {
        return perkExp;
    }

    public int getPerkLevel(PlayerEntity player, LogicalSide side) {
        return PerkLevelManager.getLevel(getPerkExp(), player, side);
    }

    public float getPercentToNextLevel(PlayerEntity player, LogicalSide side) {
        return PerkLevelManager.getNextLevelPercent(getPerkExp(), player, side);
    }

    protected void modifyExp(double exp, PlayerEntity player, LogicalSide side) {
        int currLevel = PerkLevelManager.getLevel(getPerkExp(), player, side);
        if (exp >= 0 && currLevel >= PerkLevelManager.getLevelCap(side, player)) {
            return;
        }
        long expThisLevel = PerkLevelManager.getExpForLevel(currLevel, player, side);
        long expNextLevel = PerkLevelManager.getExpForLevel(currLevel + 1, player, side);
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
        if (getTierReached().hasNextTier()) {
            setTierReached(ProgressionTier.values()[getTierReached().ordinal() + 1]);
            return true;
        }
        return false;
    }

    protected void setTierReached(ProgressionTier tier) {
        this.tierReached = tier;
    }

    public List<ResourceLocation> getKnownConstellations() {
        return knownConstellations;
    }

    public List<ResourceLocation> getSeenConstellations() {
        return seenConstellations;
    }

    public List<ResourceLocation> getStoredConstellationPapers() {
        return storedConstellationPapers;
    }

    public boolean hasSeenConstellation(IConstellation constellation) {
        return hasSeenConstellation(constellation.getRegistryName());
    }

    public boolean hasSeenConstellation(ResourceLocation constellation) {
        return seenConstellations.contains(constellation);
    }

    public boolean hasConstellationDiscovered(IConstellation constellation) {
        return hasConstellationDiscovered(constellation.getRegistryName());
    }

    public boolean hasConstellationDiscovered(ResourceLocation constellation) {
        return knownConstellations.contains(constellation);
    }

    protected void discoverConstellation(ResourceLocation name) {
        memorizeConstellation(name);
        if (!knownConstellations.contains(name)) knownConstellations.add(name);
    }

    protected void memorizeConstellation(ResourceLocation name) {
        if (!seenConstellations.contains(name)) seenConstellations.add(name);
    }

    protected void setStoredConstellationPapers(List<ResourceLocation> names) {
        this.storedConstellationPapers.clear();
        this.storedConstellationPapers.addAll(names);
    }

    @OnlyIn(Dist.CLIENT)
    protected void receive(PktSyncKnowledge message) {
        this.knownConstellations = message.knownConstellations;
        this.seenConstellations = message.seenConstellations;
        this.storedConstellationPapers = message.storedConstellationPapers;
        this.researchProgression = new HashSet<>(message.researchProgression);
        this.tierReached = MiscUtils.getEnumEntry(ProgressionTier.class, message.progressTier);
        this.attunedConstellation = message.attunedConstellation;
        this.wasOnceAttuned = message.wasOnceAttuned;
        this.freePointTokens = message.freePointTokens;
        this.appliedPerks = message.usedPerks.keySet().stream()
                .map(perkKey -> PerkTree.PERK_TREE.getPerk(LogicalSide.CLIENT, perkKey))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        this.appliedPerkData = MapStream.of(message.usedPerks)
                .mapKey(perkKey -> PerkTree.PERK_TREE.getPerk(LogicalSide.CLIENT, perkKey))
                .filterKey(Optional::isPresent)
                .mapKey(Optional::get).toMap();
        this.sealedPerks = message.sealedPerks.stream()
                .map(perkKey -> PerkTree.PERK_TREE.getPerk(LogicalSide.CLIENT, perkKey))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        this.perkExp = message.perkExp;
    }

    protected PlayerProgress copy() {
        PlayerProgress copy = new PlayerProgress();
        CompoundNBT saveData = new CompoundNBT();
        this.store(saveData);
        copy.load(saveData);
        return copy;
    }

    public void acceptMergeFrom(PlayerProgress toMergeFrom) {
        for (ResourceLocation seen : toMergeFrom.seenConstellations) {
            memorizeConstellation(seen);
        }
        for (ResourceLocation known : toMergeFrom.knownConstellations) {
            discoverConstellation(known);
        }
        if (toMergeFrom.wasOnceAttuned) {
            this.wasOnceAttuned = true;
        }
        if (toMergeFrom.tierReached.isThisLaterOrEqual(this.tierReached)) {
            this.tierReached = toMergeFrom.tierReached;
        }
        for (ResearchProgression prog : toMergeFrom.researchProgression) {
            this.forceGainResearch(prog);
        }
    }

}
