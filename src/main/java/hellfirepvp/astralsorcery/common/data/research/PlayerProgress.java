/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncKnowledge;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;

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
    private ProgressionTier tierReached = ProgressionTier.DISCOVERY;
    private Set<ResearchProgression> researchProgression = new HashSet<>();

    private IMajorConstellation attunedConstellation = null;
    private boolean wasOnceAttuned = false;

    private final PlayerPerkData perkData = new PlayerPerkData();

    private List<ResourceLocation> storedConstellationPapers = new ArrayList<>();
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

        this.perkData.load(this, compound);

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
        int[] researchArray = researchProgression.stream()
                .mapToInt(ResearchProgression::ordinal)
                .toArray();
        cmp.putIntArray("research", researchArray);
        if (attunedConstellation != null) {
            cmp.putString("attuned", attunedConstellation.getRegistryName().toString());
        }
        PerkTree.PERK_TREE.getVersion(LogicalSide.SERVER).ifPresent(version -> cmp.putLong("perkTreeVersion", version));

        cmp.putBoolean("bookReceived", tomeReceived);
        cmp.putBoolean("usePerkAbilities", usePerkAbilities);

        this.perkData.save(cmp);
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
        if (compound.contains("seenConstellations")) {
            ListNBT list = compound.getList("seenConstellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation cstName = new ResourceLocation(list.getString(i));
                if (!seenConstellations.contains(cstName)) {
                    seenConstellations.add(cstName);
                }
            }
        }
        if (compound.contains("constellations")) {
            ListNBT list = compound.getList("constellations", Constants.NBT.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                ResourceLocation cstName = new ResourceLocation(list.getString(i));

                if (!knownConstellations.contains(cstName)) {
                    knownConstellations.add(cstName);
                }
                if (!seenConstellations.contains(cstName)) {
                    seenConstellations.add(cstName);
                }
            }
        }

        if (compound.contains("tierReached")) {
            int tierOrdinal = compound.getInt("tierReached");
            ProgressionTier otherTier = MiscUtils.getEnumEntry(ProgressionTier.class, tierOrdinal);
            if (otherTier.isThisLater(this.tierReached)) {
                this.tierReached = otherTier;
            }
        }

        if (compound.contains("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                researchProgression.add(MiscUtils.getEnumEntry(ResearchProgression.class, resOrdinal));
            }
        }
    }

    public boolean isValid() {
        return true;
    }

    public PlayerPerkData getPerkData() {
        return perkData;
    }

    protected void setAttunedConstellation(IMajorConstellation constellation) {
        this.attunedConstellation = constellation;
        this.wasOnceAttuned = true;
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

    public boolean isAttuned() {
        return this.getAttunedConstellation() != null;
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
        this.perkData.receive(message);
        this.usePerkAbilities = message.doPerkAbilities;
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
        if (toMergeFrom.tierReached.isThisLaterOrEqual(this.tierReached)) {
            this.tierReached = toMergeFrom.tierReached;
        }
        for (ResearchProgression prog : toMergeFrom.researchProgression) {
            this.forceGainResearch(prog);
        }
    }
}
